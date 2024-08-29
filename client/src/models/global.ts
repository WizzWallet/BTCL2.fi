import { createModel } from '@rematch/core';
import type { RootModel } from '@/store/models';
import to from 'await-to-js';
import { notification } from '@/components/EntryGlobal';
import { store } from '@/store';
import { getPropByKey, IWalletProvider, NetworkType } from '@/types/bitcoin/bitcoin';
import { Fees } from '@/service/mempool';

export type WalletProviderKey =
  'wizz'
  | 'unisat'
  | 'atom'
  | 'okxwallet.bitcoinTestnet'
  | 'okxwallet.bitcoin'
  | 'okxwallet.bitcoinSignet';


export type ProxyObject = {
  [key in NetworkType]?: string | undefined;
};

type GlobalProps = {
  address?: string;
  providerKey?: WalletProviderKey;
  feeLevel: keyof Fees | 'custom';
  fees?: Fees;
  customFee?: number;
  // address balance
  // provider?: IWalletProvider;
  [key: string]: unknown;
};

export const global = createModel<RootModel>()({
  state: {
    theme: 'system',
    feeLevel: 'hourFee',
    refreshId: 0,
    autoEndpoint: false,
  } as GlobalProps,
  reducers: {
    save(state: GlobalProps, payload) {
      return {
        ...state,
        ...payload,
      };
    },
  },
  effects: (dispatch) => ({
    async connect(providerKey: WalletProviderKey) {
      const provider = getPropByKey(window, providerKey) as IWalletProvider;
      if (!provider) {
        let wallet;
        if (providerKey === 'wizz' || providerKey === 'atom') {
          wallet = 'Wizz Wallet';
        } else if (providerKey === 'unisat') {
          wallet = 'Unisat Wallet';
        } else if (providerKey?.includes('okxwallet')) {
          wallet = 'OKX Wallet';
        }
        notification.warning({ message: `Please install the ${wallet || 'wallet'}. If already installed, refresh the page and proceed.` });
        return;
      }
      const [error, accounts] = await to(provider.requestAccounts());
      if ((error?.message)) {
        notification.warning({
          message: error?.message,
        });
      } else if (accounts?.length) {
        const network = await provider.getNetwork();
        const address = accounts[0];
        localStorage.setItem('address', address);
        dispatch.global.save({
          address,
          providerKey,
          network,
          // provider,
        });
      } else {
        notification.warning({
          message: 'Connect failed, please try again later',
        });
      }
    },
    disconnect() {
      const address = store.getState().global?.address || '';
      dispatch.global.save({
        address: undefined,
        publicKey: undefined,
        providerKey: undefined,
        [address]: undefined,
      });
    },

  }),
});
