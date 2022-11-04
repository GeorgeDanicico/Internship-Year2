import axios from "axios";
import { useMemo } from "react";
import { useQuery } from "react-query";
import { API_ENDPOINT } from "../../utils/env";
import { getHeaderAuthorization } from "../../utils/helper";
import { IPaymentsDTO } from "../../utils/interfaces";

export function fetchPayments(accountId: string | null) {
  return axios.get<IPaymentsDTO>(`${API_ENDPOINT}/payment/all/${accountId}`, getHeaderAuthorization());
}


export function useFetchPayments(accountId: string | null) {
  const { data } = useQuery(`payments`, () => fetchPayments(accountId), {
    enabled: !!accountId,
    refetchOnWindowFocus: false,
  });

  return useMemo(() => {
    if (data?.data?.payments) {
      return data?.data?.payments;
    } else return [];
  }, [data])

}

export function fetchApiPayments(accountId: string | null) {
  return axios.get<IPaymentsDTO>(`${API_ENDPOINT}/payment/all/api/${accountId}`, getHeaderAuthorization());
}


export function useFetchApiPayments(accountId: string | null) {
  const { data } = useQuery(`api-payments`, () => fetchApiPayments(accountId), {
    enabled: !!accountId,
    refetchOnWindowFocus: false,
  });

  return useMemo(() => {
    if (data?.data?.payments) {
      return data?.data?.payments;
    } else return [];
  }, [data])

}