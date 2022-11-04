export interface IProps {
  iban: string,
  accountId: string,
  currency: string,
  amount: string,
  bank: string,
  deleteAccount: (accountId: string) => void
};