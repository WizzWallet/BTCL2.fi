import { store } from '@/store';
import { Completer } from '@/utils';
import { mempool } from './http';

export type PriceResponse = {
  prices: Prices[];
};
export type NetworkType = 'testnet' | 'testnet4' | 'livenet' | 'mainnet' | 'signet';

export type Prices = {
  USD: number;
  EUR: number;
  GBP: number;
  CAD: number;
  CHF: number;
  AUD: number;
  JPY: number;
};

export const getMemPoolBaseURL = (network?: string) => {
  let result = 'https://mempool.space';
  const finalNetwork = network || store.getState().global?.network;
  if (finalNetwork === 'testnet' || finalNetwork === 'testnet4' || finalNetwork === 'signet') {
    result += `/${finalNetwork}`;
  }
  return result;
};

export const getApiURL = (payload?: { network?: NetworkType }) => {
  return `${getMemPoolBaseURL(payload?.network)}/api`;
};

export const getTxURL = (txId: string | undefined): string => {
  return `${getMemPoolBaseURL()}/tx/${txId}`;
};


export async function getTx(payload: {
  txid: string;
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}) {
  return mempool<TxItem>({
    baseURL: getApiURL(payload),
    url: `/tx/${payload.txid}`,
    ...payload,
    method: 'GET',
  });
}


export async function getUtxos(payload: {
  address: string;
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}) {
  return mempool<MempoolUtxo[]>({
    baseURL: getApiURL(payload),
    url: `/address/${payload.address}/utxo`,
    ...payload,
    method: 'GET',
  });
}

// https://mempool.space/api/address/bc1pgvdp7lf89d62zadds5jvyjntxmr7v70yv33g7vqaeu2p0cuexveq9hcwdv/utxo

export type Fees = {
  fastestFee: number;
  halfHourFee: number;
  hourFee: number;
  economyFee: number;
  minimumFee: number;
};

export async function getFees(payload?: {
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, Fees]> {
  return mempool<Fees>({
    baseURL: getApiURL(payload),
    url: '/v1/fees/recommended',
    ...payload,
    method: 'GET',
  });
}

let _feesLock: Completer<[any, undefined] | [null, Fees]> | undefined;

export async function getFeesWithLock(payload?: {
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, Fees]> {
  if (_feesLock) {
    return await _feesLock.promise;
  }
  const completer = new Completer<[any, undefined] | [null, Fees]>();
  _feesLock = completer;
  getFees(payload).then((v) => {
    completer.complete(v);
  }).finally(() => {
    _feesLock = undefined;
  });
  return completer.promise;
}

export async function getBlockHeight(payload?: {
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, number]> {
  return mempool({
    baseURL: getApiURL(payload),
    url: '/blocks/tip/height',
    ...payload,
    method: 'GET',
  });
}


export type CPFPItem = {
  txid: string,
  fee: number,
  weight: number
};
export type CPFP = {
  ancestors: CPFPItem[],
  descendants?: CPFPItem[],
  bestDescendant?: CPFPItem,
  effectiveFeePerVsize?: number,
  adjustedVsize?: number
}


export async function getCPFP(payload: {
  txid: string;
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, CPFP]> {
  return mempool({
    baseURL: getApiURL(payload),
    url: `/v1/cpfp/${payload.txid}`,
    ...payload,
    method: 'GET',
  });
}

export type TxItem = {
  locktime: number;
  size: number;
  fee: number;
  txid: string;
  weight: number;
  vin: {
    scriptsig: string;
    witness: string[];
    sequence: number;
    scriptsig_asm: string;
    prevout: {
      scriptpubkey_address: string;
      scriptpubkey: string;
      scriptpubkey_asm: string;
      scriptpubkey_type: string;
      value: number;
    };
    is_coinbase: boolean;
    txid: string;
    inner_witnessscript_asm?: string;
    vout: number;
  }[];
  version: number;
  vout: {
    scriptpubkey_address: string;
    scriptpubkey: string;
    scriptpubkey_asm: string;
    scriptpubkey_type: string;
    value: number;
  }[];
  status: { confirmed: boolean };
};

export async function listTxsMempool(payload: {
  address: string;
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, TxItem[]]> {
  return mempool({
    baseURL: getApiURL(payload),
    url: `/address/${payload.address}/txs/mempool`,
    ...payload,
    method: 'GET',
  });
}

export async function listTxs(payload: {
  address: string;
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, TxItem[]]> {
  return mempool({
    baseURL: getApiURL(payload),
    url: `/address/${payload.address}/txs`,
    ...payload,
    method: 'GET',
  });
}

export async function broadcast(payload: {
  data: {
    txhex: string;
  };
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}): Promise<[any, undefined] | [null, string]> {
  return mempool<string>({
    baseURL: getApiURL(payload),
    url: '/tx',
    ...payload,
    method: 'POST',
  });
}

export interface MempoolUtxo {
  txid: string;
  vout: number;
  status?: {
    confirmed: true;
    block_height: number;
    block_hash: string;
    block_time: number;
  };
  value: number;
}


export async function getTxHex(payload: {
  data: {
    txid: string;
  };
  signal?: AbortSignal;
  hideError?: boolean;
  network?: NetworkType;
}) {
  return mempool<string>({
    baseURL: getApiURL(payload),
    url: `/tx/${payload.data.txid}/hex`,
    ...payload,
    method: 'GET',
  });
}
