import React, {
  useCallback, useContext, useMemo, useReducer,
} from "react";
import { PaymentContextInterface } from "./types";

const PaymentContext = React.createContext({} as PaymentContextInterface);

export function usePaymentContext() {
  return useContext(PaymentContext);
}

const reducer = (state: any, action: any) => {
  switch (action.type) {
      case "SAVE": 
          return action.payload;
      case "DELETE":
          return null;
      default:
          return state;
  }
}

export function PaymentProvider({ children } : { children: any}) {

  const [account, changeAccount] = useReducer(reducer, null);

  const setAccount = useCallback((account: any) => {
    changeAccount({ type: "SAVE", payload: account})
  }, []);

  const contextValues = useMemo(() => ({
      account, setAccount
  }), [account, setAccount]);

  return (
      <PaymentContext.Provider value={contextValues}>
          {children}
      </PaymentContext.Provider>
  );
}