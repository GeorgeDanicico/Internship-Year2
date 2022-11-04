import axios from "axios";
import { API_ENDPOINT } from "../../utils/env";
import { getHeaderAuthorization } from "../../utils/helper";

export async function getOauthToken(oauthCode: string | null) {
  if (oauthCode) {
    const { data } = await axios.post(`${API_ENDPOINT}/bank/bt-oauth`, {
      codeRequest: oauthCode,
    },
      getHeaderAuthorization(),
    );

    return data;
  }

  return null;
}