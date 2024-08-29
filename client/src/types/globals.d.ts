import { IWalletProvider } from '@/types/bitcoin/bitcoin';

declare global {
  interface Window {
    // showDirectoryPicker: FileSystemDirectoryHandle;
    _astrox_desktop_: boolean | undefined;
    atom: IWalletProvider;
    unisat: IWalletProvider;
    wizz: IWalletProvider;
    tokenpocket: IWalletProvider;
    __WIZZ_VERSION__: string;
  }
}

export type ResponseData<T> ={
  code: number;
  message: string;
  data: T;
}

export type PageData<T> = {
  current: number;
  order: any[];
  records: T[];
  pages: number;
  size: number;
  total: number;
}

