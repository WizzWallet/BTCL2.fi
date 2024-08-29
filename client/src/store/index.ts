import { init, RematchDispatch, RematchRootState } from '@rematch/core';
import { models, RootModel } from './models';

import persistPlugin from '@rematch/persist';
import localForage from 'localforage';

/** Plugins **/
import updatedPlugin, { ExtraModelsFromUpdated } from '@rematch/updated';
import loadingPlugin, { ExtraModelsFromLoading } from '@rematch/loading';


function storage(dbName: string) {
  const db = localForage.createInstance({
    name: dbName,
    storeName: 'global',
  });
  return {
    db,
    getItem: db.getItem,
    setItem: db.setItem,
    removeItem: db.removeItem,
  };
}

type FullModel = ExtraModelsFromLoading<RootModel> &
  ExtraModelsFromUpdated<RootModel>;


(
  () => {
    const ser = JSON.stringify;
    JSON.stringify = function(value: any, replacer?: (key: string, value: any) => any, space?: string | number) {
      replacer = replacer || ((_, value) => {
        if (typeof value == 'bigint') {
          return value.toString(10) + '/n/';
        }
        return value;
      });
      return ser(value, replacer, space);
    } as any;
    const des = JSON.parse;
    JSON.parse = function(text: string, reviver?: (this: any, key: any, value: any) => any) {
      reviver = reviver || ((_, value) => {
        if (typeof value == 'string' && value.endsWith('/n/') && /^\d+\/n\/$/.test(value)) {
          return BigInt(value.slice(0, -3));
        }
        return value;
      });
      return des(text, reviver);
    };
    const dbs = ['wizz.cash', 'wizz.cash:v1', 'wizz.cash:v2', 'wizz.cash:v3', 'wizz.cash:v4', 'wizz.cash:v5', 'wizz.cash:v6','wizz.cash:v7'];
    for (const name of dbs) {
      localForage.createInstance({ name }).dropInstance().catch(console.error);
    }
  }
)();


export const store = init<RootModel, FullModel>({
  models,
  plugins: [
    loadingPlugin(),
    updatedPlugin(),
    // immerPlugin(),
    persistPlugin({
      key: 'store',
      storage: storage('pwachain'),
      whitelist: ['global', 'ordinal'],
    }),
  ],
});

export type Store = typeof store;
export type RootDispatch = RematchDispatch<RootModel>;
export type RootState = RematchRootState<RootModel, FullModel>;
