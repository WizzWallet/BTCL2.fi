import { PageData } from "@/types/globals";
import { requestStake } from "./request";


interface Ordinal {
  content: string;
  inscriptionId: string;
  status: 'AVAILABLE';
  inscriptionNumber: number;
}

export function stakeOrdinals(data:StakeTokenRequest) {
  return requestStake<string>({
    data,
    url:'/stake/ordinal',
    method: 'post',
  })
}

interface StakeTokenRequest {
  txid: string;
  inputAddress: string;
  outputAddress: string;
  ordinals: string[];
 }

export function stakeToken(data: StakeTokenRequest) {
  return requestStake<string>({
    data,
    url:'/stake/token',
    method: 'post',
  })
}


export function getOrdinals(data:{
  pageNum: number;
  pageSize: number;
}) {
  return requestStake<PageData<Ordinal>>({
    data,
    url:'/ordinals',
    method: 'get',
  })
}

export function getUserOrdinals(data:{
  address: string;
}) {
  return requestStake<Ordinal[]>({
    params: {
      ...data,
    },
    url:'/user_ordinals',
    method: 'get',
  })
}


