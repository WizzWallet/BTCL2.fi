import axios, {
  AxiosRequestConfig,
  CreateAxiosDefaults,
  InternalAxiosRequestConfig,
} from 'axios';
import to from 'await-to-js';
import { store } from '@/store';
import { message } from '@/components/EntryGlobal';
// import randomstring from 'randomstring';

export interface ResponseData<T> {
  code: number;
  message: string;
  data: T;
}

export function nextId() {
  return setTimeout(() => {}, 0) as unknown as number;
}


export function requestStake<R>(
  config?: xCreateConfig | undefined,
): Promise<[any, undefined] | [null, R]> {
  const token = localStorage.getItem('token');
  const _config = {
    ...config,
    baseURL: config?.baseURL ?? 'https://btc2-fi.wizz.cash/fi',
    timeout: 1000 * 30,
    headers: {
      ...config?.headers,
      Authorization: `Bearer ${token}`,
    },
  };
  return create(_config)(_config as any);
}

// export function requestUnisat<R>(
//   config?: xCreateConfig | undefined,
// ): Promise<[any, undefined] | [null, R]> {
//   const deviceId = localStorage.getItem('device')
//     ? localStorage.getItem('device')
//     : randomstring.generate(12);
//   const address = localStorage.getItem('address');
//   const headers = {
//     'X-Client': 'UniSat Wallet',
//     'X-Version': '1.4.2',
//     'X-Address': address || '',
//     'X-Channel': 'store',
//     'X-Flag': '2',
//     'X-Udid': deviceId,
//   };
//   const _config = {
//     ...config,
//     baseURL: config?.baseURL ?? 'https://btc2-fi.wizz.cash/fi',
//     timeout: 1000 * 30,
//     headers,
//   };
//   return create(_config)(_config as any);
// }

export interface XRequestConfig extends AxiosRequestConfig {
  hideError?: boolean;
}

export interface xCreateConfig extends CreateAxiosDefaults {
  hideError?: boolean;
}

const onRequestFulfilled = (config: InternalAxiosRequestConfig<any>) => {
  return config;
};

export function create(
  config?: xCreateConfig,
): <R>(config: XRequestConfig) => Promise<[any, undefined] | [null, R]> {
  const http = axios.create(config);
  http.interceptors.request.use(
    function (config) {
      config = onRequestFulfilled(config);
      if (config.params) {
        config.params._ = Date.now();
      } else {
        config.params = { _: Date.now() };
      }
      return config;
    },
    (e) => Promise.reject(e),
  );

  http.interceptors.response.use(
    (r) => r.data,
    (e) => Promise.reject(e),
  );

  return async function <R>(
    config: XRequestConfig,
  ): Promise<[any, undefined] | [null, R]> {
    const { url, method, params, data } = config;
    const id = nextId();
    const m = method?.toUpperCase() || 'GET';
    console.log(id, m, url, ...[params, data].filter((e) => !!e));
    const rep = await to<ResponseData<R>, any>(http.request(config));
    if (rep[0]) {
      console.error(id, m, url, rep[0]);
      const msg = typeof rep[0] === 'string' ? rep[0] : rep[0].message;
      if (!config.hideError) {
        message.error(msg);
      }
      return [msg, undefined];
    } else {
      console.log(id, m, url, rep[1]);
    }
    if (rep[1]?.code === 200) {
      return [null, rep[1].data];
    } else if (rep[1]?.code === 401) {
      store.dispatch.global.save({ loginExpired: true });
      return [rep[1]?.message, undefined];
    } else {
      if (!config.hideError) {
        message.error(rep[1]?.message);
      }
      return [rep[1]?.message, undefined];
    }
  };
}
