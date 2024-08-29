import { useEffect, useState } from 'react';
import { ConnectButton } from '@rainbow-me/rainbowkit';
import BitCoinConnectButton from '@/components/ConnectButton.tsx';

import { useQuery } from '@tanstack/react-query';
import {
  getOrdinals,
  getUserOrdinals,
  stakeOrdinals,
  stakeToken,
} from './service/api';
import { useAddress, useFees, useProvider } from './hooks';
import { useAccount, useReadContract, useWriteContract } from 'wagmi';
import { erc20Abi } from 'viem';
import { contractAddress, stakeAddress, stakeBTCAddress } from './config';
import { Button } from 'antd';

import { SwapOutlined } from '@ant-design/icons';
import {
  buildTx,
  detectAddressTypeToScripthash,
  toPsbt,
  UTXO,
} from '@wizz-btc/wallet';
import FeeRateSelector from './components/FeeRateSelector';
import to from 'await-to-js';
import { message } from './components/EntryGlobal';
import LOGO from '@/assets/images/title.svg';


let timer: string | number | NodeJS.Timeout | undefined;
function App() {
  const address = useAddress();
  const provider = useProvider();
  const fees = useFees();
  const [loading, setLoading] = useState(false);
  const { address: ethAddress } = useAccount();
  const [checkedList, setCheckedList] = useState<string[]>([]);
  const { data: ordinals } = useQuery({
    queryKey: ['ordinals'],
    queryFn: () => getOrdinals({ pageNum: 1, pageSize: 100 }),
    refetchInterval: 1000 * 20,
    select(data) {
      if (data[0] || !data[1]) return;
      return data[1];
    },
    retry: 3,
    // enabled: isBinded == true,
  });
  const { data: userOrdinals } = useQuery({
    queryKey: ['userOrdinals', address],
    queryFn: () => getUserOrdinals({ address: address as string }),
    refetchInterval: 1000 * 20,
    select(data) {
      if (data[0] || !data[1]) return;
      return data[1];
    },
    retry: 3,
    enabled: !!address == true,
  });

  const { data: inscriptions } = useQuery({
    queryKey: ['inscriptions', address],
    queryFn: () => provider?.getInscriptionsByAddress(address as string),
    refetchInterval: 1000 * 20,
    select(data) {
      return data;
    },
    retry: 3,
    enabled: !!(provider && address),
  });

  console.log('inscriptions', inscriptions);

  const {
    data: ConData,
    isPending: ConPending,
    isError: ConError,
    writeContract,
    writeContractAsync,
  } = useWriteContract();

  const { data: wToothyBalance, refetch } = useReadContract({
    address: contractAddress,
    abi: erc20Abi,
    functionName: 'balanceOf',
    args: [ethAddress as `0x${string}`],
    account: ethAddress,
  });
  const [stakeType, setStakeType] = useState<'token' | 'nft'>('token');

  console.log('ordinals', ordinals);
  console.log('userOrdinals', userOrdinals);
  console.log('wToothyBalance', wToothyBalance, ethAddress);

  console.log('writeContract', writeContract, ConError, ConData, ConPending);

  useEffect(() => {
    if(refetch) {
      clearInterval(timer);
       timer = setInterval(() => {
        refetch();
      }, 1000 * 20);
    }
  }, []);

  const handleToken = async () => {
    if (checkedList.length === 0) return message.error('Please select NFTs');
    const hash = await writeContractAsync({
      address: contractAddress,
      abi: erc20Abi,
      functionName: 'transfer',
      args: [stakeAddress, BigInt(1000000 * checkedList.length)],
    });
    console.log('writeContract', hash);

    const [err, data] = await stakeToken({
      txid: hash,
      inputAddress: ethAddress as string,
      outputAddress: address as string,
      ordinals: checkedList,
    });
    if (err) {
      console.error(err);
      return;
    }
    console.log('data', data);
    message.success('Transaction submitted successfully');
    setCheckedList([]);
  };

  const handleOrdinals = async () => {
    console.log('result');
    if (checkedList.length === 0) return message.error('Please select NFTs');
    if (!inscriptions) return message.error('Inscriptions not found');
    if (!address) return message.error('Address not found');
    if (!fees.feeRate) return message.error('Please select fee rate');
    const utxos = inscriptions?.list.filter((item) =>
      checkedList.includes(item.inscriptionId),
    );

    try {
      const [err1, balance] = await to(provider!.getAssets());
      console.log('utxoRegular', balance);
      if (err1) return message.error('Failed to get UTXOs');
      console.log('utxos', utxos);
      if (!utxos) {
        return message.error('Please select NFTs');
      }
      const inputs: UTXO[] = [];
      const outputs = [];
      for (let i = 0; i < utxos.length; i++) {
        const utxo = utxos[i];
        const { scripthash: script } = detectAddressTypeToScripthash(
          utxo.address,
        );
        inputs.push({
          txid: utxo.output.split(':')[0],
          index: Number(utxo.output.split(':')[1]),
          value: utxo.outputValue,
          script,
        });
        outputs.push({
          address: stakeBTCAddress,
          value: utxo.outputValue,
        });
      }
      const result = buildTx({
        inputs,
        outputs,
        balances: balance.regularUTXOs,
        feeRate: fees.feeRate,
        address: address,
        amount: 0,
      });
      console.log('result', result);
      if (result.error) {
        message.error(result.error);
        return;
      }

      const tx = result.ok;
      if (!provider) {
        message.warning('Please connect wallet first.');
        return;
      }
      const pubkey = await provider.getPublicKey();
      if (!tx || !pubkey || !address) {
        return;
      }
      setLoading(true);
      try {
        const psbt = toPsbt({ pubkey, tx });
        const signed = await provider.signPsbt(psbt.toHex());
        const txid = await provider.pushPsbt(signed);
        const [err, data] = await stakeOrdinals({
          txid,
          inputAddress: address as string,
          outputAddress: ethAddress as string,
          ordinals: checkedList,
        });
        if (err) {
          console.error(err);
          return;
        }
        console.log('data', data);
        setCheckedList([]);
        message.success('Transaction submitted successfully');
      } finally {
        setLoading(false);
      }
    } finally {
      setLoading(false);
    }
  };

  // const onChange = (list: string[]) => {
  //   setCheckedList(list);
  // };

  return (
    <>
      <div className="container max-w-[1200px] mx-auto page-bg">
        <div className="flex justify-between py-7 gap-4">
          <div className="flex-1">
            <img src={LOGO} alt="" />
          </div>
          <div className="flex gap-4">
            <ConnectButton />
            <BitCoinConnectButton />
          </div>
        </div>
        <div className="flex justify-center">
          <div className="w-[560px] mt-24">
            <div
              className={`flex-1 flex w-full flex-col gap-1 relative ${
                stakeType === 'token' ? 'flex-col' : 'flex-col-reverse'
              }`}
            >
              <div className="flex-1 relative">
                <div className="bg-black-100 p-7 flex flex-col items-center rounded-2xl px-5 gap-2">
                  <div className="flex w-full justify-between">
                    <h2>{stakeType === 'token' ? 'From' : 'To'}</h2>
                    <div className="text-primary">
                      balance: {(wToothyBalance || '0')?.toString()} wToothy
                    </div>
                  </div>
                  <div className="flex flex-1 w-full text-3xl py-6 justify-between items-center text-secondary">
                    <div className="flex items-center gap-2">
                      wToothy
                      <img
                        src="https://ape.fi/assets/bayc-coin/baycCoin.png"
                        className="h-9 w-9"
                        alt=""
                      />
                    </div>
                    <h1 className="">
                      {Number(
                        checkedList.length
                          ? `${checkedList.length}000000`
                          : '0',
                      ).toLocaleString()}
                    </h1>
                  </div>
                </div>
                <div
                  className={`cursor-pointer flex justify-center items-center w-14 h-14 rounded-full bg-black-100 border-4 solid border-black-0 absolute left-1/2 -translate-x-1/2
                    ${stakeType === 'token' ? 'bottom-0 -mb-8' : 'top-0 -mt-8'}
                  `}
                  onClick={() => {
                    setStakeType(stakeType === 'token' ? 'nft' : 'token');
                  }}
                >
                  <SwapOutlined className="text-primary text-3xl rotate-90" />
                </div>
              </div>

              <div className="flex-1">
                <div className="bg-black-100 flex p-7 items-center flex-col rounded-2xl px-5 gap-2">
                  <div className="flex w-full">
                    <h2>{stakeType === 'token' ? 'To' : 'From'}</h2>
                  </div>
                  <div className="flex w-full flex-wrap items-center">
                    {(stakeType === 'token'
                      ? ordinals?.records
                      : userOrdinals
                    )?.map((item) => (
                      <div
                        key={item.inscriptionId}
                        className={`w-1/5 p-1`}
                        onClick={() => {
                          setCheckedList([item.inscriptionId]);
                        }}
                      >
                        <div
                          className={`rounded-md text-center bg-black-0 overflow-hidden border-4 solid ${
                            checkedList.includes(item.inscriptionId)
                              ? 'border-primary'
                              : 'border-none'
                          }`}
                        >
                          <img src={item.content} alt={item.inscriptionId} />
                          <h2 className="text-sm">
                            # {item.inscriptionNumber}
                          </h2>
                        </div>
                      </div>
                    ))}
                  </div>
                  {(stakeType === 'token' ? ordinals?.records : userOrdinals)
                    ?.length === 0 && <div>No Inscriptions</div>}
                  {!address && stakeType === 'nft' && (
                    <div>Please connect wallet first</div>
                  )}
                  {stakeType === 'nft' && (
                    <FeeRateSelector
                      bordered={true}
                      className="mt-2 mb-4"
                      feesoptions={['fastestFee', 'halfHourFee', 'hourFee']}
                    />
                  )}
                </div>
              </div>
            </div>
            <div className="mt-5">
              {stakeType === 'token' ? (
                <Button
                  type="primary"
                  disabled={!address || !ethAddress}
                  className="w-full"
                  loading={ConPending}
                  onClick={handleToken}
                >
                  Redeem
                </Button>
              ) : (
                <Button
                  type="primary"
                  disabled={!address || !ethAddress}
                  className="w-full"
                  loading={loading}
                  onClick={handleOrdinals}
                >
                  Stake
                </Button>
              )}
            </div>
          </div>
        </div>

        {/* <div>
          <h1>Your NFTs</h1>
          <CheckboxGroup value={checkedList}>
            <div className="flex flex-wrap">
              {inscriptions?.list?.map((item) => (
                <Checkbox value={item.inscriptionId}>
                  <div key={item.inscriptionId} className="w-1/4 p-4">
                    <img src={item.content} alt={item.inscriptionId} />
                    <h2>{item.inscriptionId}</h2>
                  </div>
                </Checkbox>
              ))}
            </div>
          </CheckboxGroup>
        </div> */}

        {/* <h1>Select NFTs</h1>
        <CheckboxGroup value={checkedList} onChange={onChange}>
          <div className="flex flex-wrap">
            {ordinals?.records?.map((item) => (
              <Checkbox value={item.inscriptionId}>
                <div key={item.inscriptionId} className="w-1/4 p-4">
                  <img src={item.content} alt={item.inscriptionId} />
                  <h2>{item.inscriptionId}</h2>
                </div>
              </Checkbox>
            ))}
          </div>
        </CheckboxGroup> */}
      </div>
    </>
  );
}

export default App;
