

import axios, { AxiosRequestConfig, CreateAxiosDefaults, InternalAxiosRequestConfig } from 'axios';
import to from 'await-to-js';
import { nextId } from '../request';

export function mempool<R>(config?: xCreateConfig | undefined): Promise<[any, undefined] | [null, R]> {
  const _config = {
    ...config,
    baseURL: config?.baseURL ?? 'https://mempool.space/testnet/api',
    timeout: 1000 * 15,
  };
  return create(_config)(_config as any);
}
export interface XRequestConfig extends AxiosRequestConfig {
  hideError?: boolean;
}

export interface xCreateConfig extends CreateAxiosDefaults {
  hideError?: boolean;
}

const onRequestFulfilled = (config: InternalAxiosRequestConfig<any>) => {
  return config;
};

export function create(config?: xCreateConfig): <R>(config: XRequestConfig) => Promise<[any, undefined] | [null, R]> {
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
    e => Promise.reject(e),
  );

  http.interceptors.response.use(
    r => r.data,
    e => Promise.reject(e),
  );

  return async function <R>(config: XRequestConfig): Promise<[any, undefined] | [null, R]> {
    const { url, method, params, data } = config;
    const id = nextId();
    const m = method?.toUpperCase() || 'GET';
    console.log(id, m, url, ...[params, data].filter(e => !!e));
    const rep = await to<R, any>(http.request(config));
    if (rep[0]) {
      console.error(id, m, url, rep[0]);
    } else {
      console.log(id, m, url, rep[1]);
    }
    return rep;
  };
}
