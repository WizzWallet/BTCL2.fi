import { Button, Dropdown, List, MenuProps, Modal, Typography } from 'antd';
import { useEffect, useState } from 'react';
import IconUnisat from '@/assets/icon/unisat.svg';
import IconAtom from '@/assets/icon/wizz_logo.svg';
import IconOkx from '@/assets/icon/okx.svg';
import IconBitget from '@/assets/icon/bitget.svg';
import { useAddress } from '@/hooks';
import { DisconnectOutlined } from '@ant-design/icons';
import { useDispatch, useSelector } from 'react-redux';
import { RootDispatch, RootState } from '@/store';
import { message } from './EntryGlobal';
import { WalletProviderKey } from '@/models/global';
import ICON from '@/assets/images/wallet-svgrepo-com.svg';


const providers: Record<string, any> = [
  {
    icon: IconAtom,
    name: 'Wizz Wallet',
    key: 'atom',
  },
  {
    icon: IconUnisat,
    name: 'Unisat Wallet',
    key: 'unisat',
  },
  {
    icon: IconOkx,
    name: 'OKX Wallet',
    key: 'okxwallet.bitcoin',
  },
  {
    icon: IconBitget,
    name: 'Bitget Wallet',
    key: 'bitkeep.unisat',
  },
];


const BitCoinConnectButton = () => {
  const dispatch = useDispatch<RootDispatch>();
  const [loading, setLoading] = useState(false);
  const address = useAddress();
  const providerKey = useSelector((state: RootState) => state.global?.providerKey);
  const [showProviderModal, setShowProviderModal] = useState(false);

  useEffect(() => {
    const handler = () => {
      setShowProviderModal(true);
    };
    document.addEventListener('connect-wallet', handler);
    return () => {
      document.removeEventListener('connect-wallet', handler);
    };
  }, []);

  if (address) {
    const items: MenuProps['items'] = [
      {
        key: 'disconnect',
        label: <><DisconnectOutlined /><span className="ml-2">Disconnect</span></>,
        onClick: () => {
          dispatch.global.disconnect();
        },
      },
    ];
    return (
      <Dropdown menu={{ items }} placement="bottomRight" className="relative">
        <Button type={'primary'} className={`leading-4 relative inline-flex items-center justify-center gap-1`}
                onClick={() => {
                  window.navigator.clipboard.writeText(address);
                  message.destroy();
                  message.success('Copied');
                }}>

          <img src={ICON} className={'w-6'} />
          {address.slice(0, 6)}...{address.slice(-5)}

        </Button>
      </Dropdown>
    );
  }

  const handleConnect = (key: string) => {
    setLoading(true);
    dispatch.global.connect(key as WalletProviderKey).finally(() => {
      setLoading(false);
    });
  };

  const handleOnClick = async () => {
    setShowProviderModal(true);
  };

  return <>
    <Button type="primary" className="leading-4 min-w-[100px]" onClick={handleOnClick}>
      Connect
    </Button>
    <Modal
      open={showProviderModal}
      centered
      onCancel={() => {
        setShowProviderModal(false);
        setLoading(false);
      }}
      footer={false}>
      <Typography.Title className="mt-4" level={3}>Connect a wallet to continue</Typography.Title>
      <Typography.Text type="secondary">
        Choose how you want to connect. If you don't have a wallet, you can select a provider and create one.
      </Typography.Text>
      <List
        loading={loading}
        className="mt-8"
        itemLayout="horizontal"
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        //@ts-expect-error
        dataSource={providers}
        renderItem={(item) => (
          <List.Item onClick={() => handleConnect(item.key)}>
            <div className="flex items-center cursor-pointer w-full">
              <img src={item.icon} alt={item.name}
                   className="w-10 h-10 rounded-xl object-contain list-color p-1 overflow-hidden" />
              <div className="ml-4 !mb-0 flex-1">
                <div className="text-xl leading-none relative">
                  {item.name}
                  {item.badge ?
                    <span className="absolute ml-2 -top-1 text-xs bg-primary primary-text px-1 py-0.5 rounded font-bold"
                          style={{
                            transform: 'scale(0.8)',
                            transformOrigin: 'left top',
                          }}>{item.badge}</span> : null}
                </div>
                {item.extra ? <div className="text-green-500 text-xs">{item.extra}</div> : null}
                {item.text ? <div className="secondary-text text-xs">{item.text}</div> : null}
              </div>
              {
                providerKey === item.key ?
                  <span className="text-sm text-primary">Last Used</span>
                  : null
              }
            </div>
          </List.Item>
        )}
      />
    </Modal>
  </>;
};

export default BitCoinConnectButton;
