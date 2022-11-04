import axios from "axios";
import { JWT_TOKEN } from "./env"

export function getHeaderAuthorization() {
  const token = localStorage.getItem(JWT_TOKEN);

  return {
    headers: {
      'Content-Type': 'application/json',
      'Accept' : 'application/json',
      'Authorization': `Bearer ${token}`,
      'Access-Control-Allow-Origin': '*',
    }
  }
}

export function compareDates(dateFrom: string, dateTo: string) {
  const currentDate = new Date();
  const df = new Date(dateFrom);
  const dt = new Date(dateTo);

  if (currentDate.getTime() - df.getTime() >  3600 * 24 * 120 * 1000 ||
  currentDate.getTime() - dt.getTime() >  3600 * 24 * 120 * 1000) {
    return false;
  }
  
  return true;
}


export function parseDate(date: Date) {
  const d = new Date(date);
  return d.getFullYear() + "-" + (d.getMonth() + 1) + "-" + d.getDate();
}
export function getCurrentDate() {
  const date = new Date();

  return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
}

export const GLOBAL_CURRENCIES = {
  ron: "RON",
  eur: "EUR",
}

export async function convertCurrency(currency1: string, currency2: string, amount: string) {
  return axios.get(`https://romanian-exchange-rate-bnr-api.herokuapp.com/api/convert?access_key=f7dbe1842278-43779b&from=${currency1}&to=${currency2}&amount=${amount}`)
  .then((response) => response.data.result_short)
  .catch((err) => Number(amount) * 4.93)
}

export function getAccountsTotalAvailableAmount(accounts: any[]) {
  const acc = accounts.map(async (account) => {
    if (account.currency !== GLOBAL_CURRENCIES.ron && account.amount > 0) {
      const value = await convertCurrency(account.currency, GLOBAL_CURRENCIES.ron, account.amount);
      return value;
    } else return account.amount
  });

  return Promise.all(acc).then(values => values.reduce((p, c) => p + c, 0));

}

export const BANKS = {
  bt: "Banca Transilvania",
  brd: "BRD",
  cec: "CEC Bank"
}