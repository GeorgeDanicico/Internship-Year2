import { useMemo } from "react";
import axios from "axios";
import { useQuery } from "react-query";
import { API_ENDPOINT, getAccessTokenIdentifier } from "../../utils/env";
import { BANKS, getHeaderAuthorization } from "../../utils/helper";
import { IAccount, IBankAccountsRequest } from "../../utils/interfaces";
import { IAccountsDTO } from "./types";

export function fetchAccounts(payload: IBankAccountsRequest) {
  const { headers } = getHeaderAuthorization();

  return axios.get<IAccountsDTO>(`${API_ENDPOINT}/accounts/`, {
    params: payload,
    headers,
  }
  );
}

export function useFetchAccounts(bankName: string) {
  const accessTokenIdentifier = getAccessTokenIdentifier(bankName);

  const payload: IBankAccountsRequest = {
    accessToken: localStorage.getItem(accessTokenIdentifier) || "null",
    bankName,
  }

  const { data } = useQuery(`bank-accounts-${bankName}`, () => fetchAccounts(payload), {
    enabled: true,
    refetchOnWindowFocus: false,
  });
  const accessToken = data?.data.accessToken?.accessToken;

  if (accessToken) {
    localStorage.setItem(accessTokenIdentifier, accessToken);
  } 

  return useMemo(() => {
    return {
      data: data?.data,
    }
  }, [data]);
}

export function useFetchAllAccounts() {
  const { data: btData } = useFetchAccounts(BANKS.bt);
  const { data: brdData } = useFetchAccounts(BANKS.brd);
  const { data: cecData } = useFetchAccounts(BANKS.cec);

  return useMemo(() => {
    let result: IAccount[] = [];

    if (btData && btData?.accounts) {
      result = [...result, ...btData?.accounts]
    }

    if (brdData && brdData?.accounts) {
      result = [...result, ...brdData?.accounts]
    }

    if (cecData && cecData?.accounts) {
      result = [...result, ...cecData?.accounts]
    }

    return { accounts: result }
  }, [btData, brdData, cecData])
}