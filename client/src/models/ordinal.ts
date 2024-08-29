import { createModel } from '@rematch/core';
import type { RootModel } from '@/store/models';
import { getOrdinals } from '@/service/api';


type OrdinalProps = {
  address?: string;
  ordinals: any;
  // address balance
  [key: string]: unknown;
};

export const ordinal = createModel<RootModel>()({
  state: {
    ordinals: [],
  } as OrdinalProps,
  reducers: {
    save(state: OrdinalProps, payload) {
      return {
        ...state,
        ...payload,
      };
    },
  },
  effects: (dispatch) => ({
    async getOrdinals(payload) {
      const [error, data] = await getOrdinals(payload);
      if (error) {
        return error;
      }
      dispatch.ordinal.save({ ordinals: data});
    },
    async getOwnerOrdinals(payload) {
      const [error, data] = await getOrdinals(payload);
      if (error) {
        return error;
      }
      dispatch.ordinal.save({ ordinals: data});
    },

  }),
});
