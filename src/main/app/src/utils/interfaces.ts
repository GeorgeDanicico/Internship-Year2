export interface LoginData {
    token: string,
    user: User,
}

// PAYLOADS / DTOS
export interface User {
    username: string,
    firstName: string,
    lastName: string,
    email: string,
    personalNumericalCode: string,
}

export interface IPayment {
    currency: string,
    creditorName: string,
    date: Date,
    amount: number,
    paymentId: string,
    creditorIban: string,
    status: string,
}

export interface IPaymentsDTO {
    payments: IPayment[],
}

export interface IAccount {
    accountId: string,
    currency: string,
    iban: string,
    amount: string,
    bank: string,
}

export interface IBankAccountsRequest {
    accessToken: string | null,
    bankName: string | null,
}

export interface IBankOauthResponse {
    accessToken: string,
    refreshToken: string,
    expiresIn: number,
    tokenType: string,
}

export interface AccountDetails {
    iban: string,
}