
import { MempoolUtxo } from './types';
import {
  BalanceObject,
  FeeRateResponse,
  IAtomicalWallet,
  IAtomicalWalletData,
  InscriptionsResponse,
  PriceResponse,
  SendBitcoinOption,
  SendInscriptionOption,
  SignMessageType,
  SignOptions,
} from './types';
import { EventEmitter } from 'stream';


export function getPropByKey(obj: any, key: string) {
  const keys = key.split('.');
  let result = obj;
  for (const key1 of keys) {
    if (result) {
      result = result[key1];
    }
  }
  return result;
}
export type NetworkType = 'testnet' | 'testnet4' | 'livenet' | 'mainnet' | 'signet';

export interface IWalletProvider extends EventEmitter {
  fetchAndValidateFile(url: string, filePath: string, expectSHA: string): Promise<string>;

  getProxy(): string | undefined;

  // Connect the current account.
  requestAccounts(): Promise<string[]>;

  getAccounts(): Promise<string[]>;

  getNetwork(): Promise<NetworkType>;

  // Get an address type, return null if the address is invalid
  getAddressType(address: string): Promise<string | null>;

  // Get current account PublicKey
  getPublicKey(): Promise<string>;

  // Get BTC balance
  getBalance(): Promise<BalanceObject>;

  getAssets(): Promise<any>;
  // Get BTC inscriptions
  getInscriptions(cursor?: number, size?: number): Promise<InscriptionsResponse>;

  getInscriptionsByAddress(address: string, cursor?: number, size?: number): Promise<InscriptionsResponse>;

  getUtxo(address: string): Promise<MempoolUtxo[]>;

  // Send BTC
  sendBitcoin(fromAddress: string, toAddress: string, satoshis: number, options: SendBitcoinOption): Promise<string>;

  // send inscription
  sendInscription(
    fromAddress: string,
    toAddress: string,
    inscriptionIds: string,
    options: SendInscriptionOption,
  ): Promise<string>;

  // Sign message
  signMessage(message: string, type?: string | SignMessageType): Promise<string>;

  // Sign Psbt(hex)
  signPsbt(psbtHex: string, options?: SignOptions): Promise<string>;

  signAtomicalPsbt(psbtHex: string, options?: SignOptions): Promise<string>;

  // Sign Psbts(hexs)
  signPsbts(psbtHexs: string[], options?: SignOptions): Promise<string[]>;

  getAppVersion(): Promise<string>;

  getSupportedMethods(): Promise<string[]>;

  getFee(): Promise<FeeRateResponse>;

  getPrice(): Promise<PriceResponse>;

  calculateFee(psbtHex: string, feeRate: number, options?: SignOptions): Promise<number>;

  pushTx({ rawtx }: { rawtx: string }): Promise<string>;

  pushPsbt(psbt: string): Promise<string>;

  ///*************** Desktop specific methods ***************///

  setNetwork(network: string): Promise<void>;

  initWallet(phrase?: string, path?: string): Promise<IAtomicalWallet>;

  getWalletData(): Promise<IAtomicalWallet>;

  saveWalletInstance(walletInstance: IAtomicalWallet): Promise<void>;

  importWIF(alias: string, wif: string): Promise<IAtomicalWalletData>;

  createSubAccount(subPath?: string): Promise<IAtomicalWalletData>;


  abortMintOperation(id: number): void;

}
