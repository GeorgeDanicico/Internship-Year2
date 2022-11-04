export interface IPaymentInputs {
  debtorAccountId: string,
  creditorName: string,
  creditorIban: string,
  paymentAmount: number,
  details: string,
  paymentDate: string,
}