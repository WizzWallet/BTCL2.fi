
export interface MempoolUtxo {
  txid: string;
  vout: number;
  status: {
    confirmed: true;
    block_height: number;
    block_hash: string;
    block_time: number;
  };
  value: number;
}


export interface IAtomicalWalletData {
  address: string,
  alias?: string,
  path?: string,
  WIF: string,
}

export interface IAtomicalWallet {
  phrase: string,
  selected: IAtomicalWalletData,
  primary: IAtomicalWalletData,
  funding: IAtomicalWalletData,
  imported: { [key: string]: IAtomicalWalletData },
  subPaths: IAtomicalWalletData[],
}


export const feeRateLabelMap = {
  fastestFee: {
    label: 'Fast',
    text: '10 mins',
  },
  halfHourFee: {
    label: 'Average',
    text: '30 mins',
  },
  hourFee: {
    label: 'Slow',
    text: '1 hour',
  },
  economyFee: {
    label: 'Economy',
    text: 'days or more',
  },
  minimumFee: {
    label: 'Minimum',
    text: 'days or more',
  },
};

export interface InscriptionItem {
  inscriptionId: string;
  inscriptionNumber: number;
  address: string;
  outputValue: number;
  preview: string;
  content: string;
  contentLength: number;
  contentType: string;
  timestamp: number;
  genesisTransaction: string;
  location: string;
  output: string;
  offset: 0;
}

export interface SendBitcoinOption {
  feeRate?: number;
}

export interface SendInscriptionOption {
  feeRate?: number;
}

export interface SignOptions {
  autoFinalized?: boolean;
  signAtomical?: boolean;
  addressType?: 'p2tr' | 'p2wpkh' | 'p2sh' | 'p2pkh' | 'p2pkhtr';
}

export interface InscriptionsResponse {
  total: number;
  list: InscriptionItem[];
}

export interface PushTxRequest {
  rawtx: string;
}


export type SignMessageType = 'ecdsa' | 'bip322-simple';

export interface BalanceObject {
  confirmed: number;
  unconfirmed: number;
  total: number;
}


export interface FeeResponse {
  success: boolean;
  fee?: number;
  error?: FeeError;
}

export interface InscribeFeeResponse {
  networkFee: number;
  serviceFee: number;
  preservationFee: number;
  totalFee: number;
}

export type FeeError =
  | 'invalidAddress'
  | 'invalidAmount'
  | 'invalidInscriptions'
  | 'amountTooSmall'
  | 'invalidCustomFeeRate'
  | 'insufficientBalance'
  | 'insufficientFee'
  | 'unknownError';

export interface FeeRateResponse {
  fastestFee: number;
  halfHourFee: number;
  hourFee: number;
  economyFee: number;
  minimumFee: number;
}

export interface PriceResponse {
  'prices': Array<{
    'USD': number;
    'EUR': number;
    'GBP': number;
    'CAD': number;
    'CHF': number;
    'AUD': number;
    'JPY': number;
  }>,
}

export interface InscribeRequest {
  userAddress: string;
  loginAddress: string;
  inscribeType: 'file' | 'text' | 'sats' | 'repeat_text' | null;
  count?: number;
  feeRate: number;
  inscribeContent: string;
}

export interface InscribeResponse {
  orderNo: string;
  address: string;
  lightningAddress?: string;
  amount: number;
}
