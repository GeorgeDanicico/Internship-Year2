import { useMemo } from "react";
import axios from "axios";
import { useQuery } from "react-query";
import { API_ENDPOINT } from "../../utils/env";
import { getHeaderAuthorization } from "../../utils/helper";
import { IAccountsDTO } from "../Dashboard/types";
import { IPaymentInputs } from "./types";

export function executePayment(input: IPaymentInputs) {
  const { headers } = getHeaderAuthorization();

  return axios.post(`${API_ENDPOINT}/payment/`, input, {
    headers,
  })
}

export function fetchAccountsForPayments() {
  const { headers } = getHeaderAuthorization();

  return axios.get<IAccountsDTO>(`${API_ENDPOINT}/accounts/for-payments `, {
    headers,
  }
  );
}

export function useFetchAccountsForPayments() {
  const { data } = useQuery(`bank-accounts`, () => fetchAccountsForPayments(), {
    enabled: true,
    refetchOnWindowFocus: false,
  });

  return useMemo(() => {
    return {
      accounts: data?.data?.accounts,
    }
  }, [data]);
}