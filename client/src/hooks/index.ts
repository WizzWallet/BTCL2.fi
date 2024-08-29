import { getFees } from "@/service/mempool";
import { RootDispatch, RootState } from "@/store";
import { getPropByKey, IWalletProvider } from "@/types/bitcoin/bitcoin";
import { isPageHidden } from "@/utils";
import { useCallback, useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";


export const useAddress = () => {
  const { address } = useSelector((state: RootState) => state.global);
  return address;
}


export const useProvider= () => {
  const { providerKey } = useSelector((state: RootState) => state.global);
  if(!providerKey) return undefined;
  const provider = getPropByKey(window, providerKey) as IWalletProvider;
  return provider;
}


export function useLoading(): [boolean, () => void, () => void] {
  const [count, setCount] = useState(0);

  const increment = useCallback(() => {
    setCount((prevCount) => prevCount + 1);
  }, []);

  const decrement = useCallback(() => {
    setCount((prevCount) => prevCount - 1);
  }, []);

  return [count > 0, increment, decrement];
}


export const useFees = () => {
  const fees = useSelector((state: RootState) => state.global?.fees) || {
    fastestFee: 0,
    halfHourFee: 0,
    hourFee: 0,
    economyFee: 0,
    minimumFee: 0,
  };
  const level = useSelector((state: RootState) => state.global?.feeLevel);
  const customFee = useSelector((state: RootState) => state.global?.customFee);
  return {
    fees,
    level,
    customFee,
    feeRate: level === 'custom' ? customFee : fees[level],
  };
};

export const useFeesWithRefresh = () => {
  const fees = useSelector((state: RootState) => state.global?.fees) || {
    fastestFee: 0,
    halfHourFee: 0,
    hourFee: 0,
    economyFee: 0,
    minimumFee: 0,
  };
  const level = useSelector((state: RootState) => state.global?.feeLevel);
  const customFee = useSelector((state: RootState) => state.global?.customFee);
  const [loading, plus, minus] = useLoading();
  const dispatch = useDispatch<RootDispatch>();
  useEffect(() => {
    const controller = new AbortController();
    const loadFees = () => {
      if (isPageHidden()) {
        return;
      }
      plus();
      (async () => {
        const [, fees] = await getFees({ signal: controller.signal });
        if (fees) {
          dispatch.global.save({ fees });
        }
      })().finally(() => minus());
    };
    loadFees();
    const id = setInterval(() => {
      loadFees();
    }, 1000 * 15);
    return () => {
      clearInterval(id);
      controller.abort();
    };
  }, [dispatch.global, minus, plus]);
  return {
    fees,
    level,
    loading,
    customFee,
    feeRate: level === 'custom' ? customFee : fees[level],
  };
};
