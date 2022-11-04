import { IAccount } from "../../utils/interfaces";

export interface IAccountsDTO {
  accounts: IAccount[],
  accessToken: any,
}