import {User} from "../utils/interfaces";

export interface UserContextInterface {
    isLogged: boolean,
    isLoading: boolean,
    user: User | undefined,
    // eslint-disable-next-line no-unused-vars
    login: (token: string, user: User) => void;
    logout: () => void;
}

export interface PaymentContextInterface {
    account: any,
    setAccount: (account: any) => void,
}