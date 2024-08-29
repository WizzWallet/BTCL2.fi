import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import App from './App.tsx';
import './index.css';
import './global.less';
import { mainnet } from 'wagmi/chains';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { WagmiProvider } from 'wagmi';
import {
  darkTheme,
  // connectorsForWallets,
  getDefaultConfig,
  RainbowKitProvider,
} from '@rainbow-me/rainbowkit';
import '@rainbow-me/rainbowkit/styles.css';
import { pwa } from './config/chains.ts';
import EntryGlobal from './components/EntryGlobal.tsx';
import { Provider } from 'react-redux';
import { store } from './store/index.ts';
import { getPersistor } from '@rematch/persist';
import { PersistGate } from 'redux-persist/integration/react';
import Global from './components/Global.tsx';

// export const config = createConfig({
//   chains: [mainnet, sepolia],
//   transports: {
//     [mainnet.id]: http(),
//     [sepolia.id]: http(),
//   },
// });
const config = getDefaultConfig({
  appName: 'My RainbowKit App',
  projectId: 'YOUR_PROJECT_ID',
  chains: [mainnet, pwa],
  ssr: true, // If your dApp uses server side rendering (SSR)
});

// const connectors = connectorsForWallets([
//   {
//     groupName: 'Recommended',
//     wallets: [
//       injectedWallet({ chains }),
//       metaMaskWallet({ chains, projectId: import.meta.env.VITE_WC_PROJECT_ID }),
//       coinbaseWallet({ chains, appName: 'Card3' }),
//       rainbowWallet({ projectId: import.meta.env.VITE_WC_PROJECT_ID, chains }),
//       walletConnectWallet({
//         projectId: import.meta.env.VITE_WC_PROJECT_ID,
//         chains,
//       }),
//     ],
//   },
// ]);

// const config = createConfig({
//   autoConnect: true,
//   connectors,
//   publicClient,
// });

const queryClient = new QueryClient();

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <Provider store={store}>
      <PersistGate persistor={getPersistor()}>
        <WagmiProvider config={config}>
          <QueryClientProvider client={queryClient}>
            <RainbowKitProvider theme={darkTheme()}>
              <Global>
                <App />
                <EntryGlobal />
              </Global>
            </RainbowKitProvider>
          </QueryClientProvider>
        </WagmiProvider>
      </PersistGate>
    </Provider>
  </StrictMode>,
);
