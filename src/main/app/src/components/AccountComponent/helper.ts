import axios from "axios";
import { API_ENDPOINT, getAccessTokenIdentifier } from "../../utils/env";
import { BANKS, getHeaderAuthorization } from "../../utils/helper";

export async function fetchTransactions(dateFrom: string, dateTo: string, accountId: string, bankName: string) {
  const accessTokenIdentifier = getAccessTokenIdentifier(bankName);

  const accessToken = localStorage.getItem(accessTokenIdentifier) || "null";
  const { headers } = getHeaderAuthorization();
  const { data } = await axios.get(`${API_ENDPOINT}/transactions/${accountId}`, {
    params: {
      accessToken,
      dateFrom,
      dateTo,
      bankName,
    }, 
    headers,
  })

  if (data) {
    console.log(data);
    if (data.tokenDTO) {
      localStorage.setItem(accessTokenIdentifier, data.tokenDTO.accessToken);
    }

    return data;
  } else {
    return null;
  }
}