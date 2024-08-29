import { global } from '../models/global';
import { Models } from '@rematch/core';

export interface RootModel extends Models<RootModel> {
  global: typeof global;
}

export const models: RootModel = { global };
