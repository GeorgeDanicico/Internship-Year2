import { BANKS } from "./helper";

export const JWT_TOKEN = process.env.REACT_APP_JWT_TOKEN || 'JWT_TOKEN';

export const API_ENDPOINT = process.env.REACT_APP_API_ENDPOINT;

export const BTLinks = {
  authRedirect: "https://localhost:3000/redirect-bt",
  paymentRedirect: "https://localhost:3000/success-payment"
}

export const CECLinks = {
  authRedirect: "https://localhost:3000/redirect-cec",
  paymentRedirect: "https://localhost:3000/success-payment-cec"
}

export const ACCESS_TOKEN = {
  bt: "BTAccessToken",
  cec: "CECAccessToken"
}

export function getAccessTokenIdentifier(bankName: string) {
  if (bankName === BANKS.bt) {
    return ACCESS_TOKEN.bt;
  }

  if (bankName === BANKS.cec) {
    return ACCESS_TOKEN.cec;
  }

  return "";
}