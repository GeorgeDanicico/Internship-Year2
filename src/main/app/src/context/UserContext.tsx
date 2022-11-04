import React, {
    useCallback, useContext, useMemo, useEffect, useReducer,
} from "react";
import axios from "axios";
import {UserContextInterface} from "./types";
import {User} from "../utils/interfaces";
import { ACCESS_TOKEN, API_ENDPOINT, JWT_TOKEN } from "../utils/env";
import { getHeaderAuthorization } from "../utils/helper";

const UserContext = React.createContext({} as UserContextInterface);

export function useUserContext() {
    return useContext(UserContext);
}

const userInitialState: User | undefined = undefined;

const userReducer = (state: any, action: any) => {
    switch (action.type) {
        case "SAVE": 
            return action.payload;
        case "DELETE":
            return undefined;
        default:
            return state;
    }
}

const reducer = (state: any, action: any) => {
    switch (action.type) {
        case "TRUE": 
            return true;
        case "FALSE":
            return false;
        default:
            return state;
    }
}

export function UserProvider({ children } : { children: any}) {

    const [isLogged, setIsLogged] = useReducer(reducer, false);
    const [isLoading, dispatch] = useReducer(reducer, true);
    const [user, setUser] = useReducer(userReducer, userInitialState);

    const login = useCallback((token: string, loginUser: User) => {
        localStorage.setItem(JWT_TOKEN, token);
        setIsLogged({ type: "TRUE" });

        setUser({ type: "SAVE", payload: loginUser });
    }, [setUser]);

    const logout = useCallback(() => {
        localStorage.removeItem(JWT_TOKEN);
        localStorage.removeItem(ACCESS_TOKEN.bt);
        localStorage.removeItem(ACCESS_TOKEN.cec);
        setIsLogged({ type: "FALSE" });
        setUser({ type: "DELETE" });
    }, [setIsLogged, setUser]);

    useEffect(() => {
        const fetchUser = async () => {
            const token = localStorage.getItem(JWT_TOKEN);
          
            if (!token) {
                dispatch({ type: "FALSE" })
                return;
            }

            const { data } = await axios.get<User>(`${API_ENDPOINT}/auth/me`, getHeaderAuthorization())

            if (data) {
                setIsLogged({ type: "TRUE" });
                setUser({type: "SAVE", payload: data });
            }

            dispatch({ type: "FALSE" });
    };

        fetchUser();
    }, [setUser, dispatch, setIsLogged])
    
    const contextValues = useMemo(() => ({
        isLogged, user, login, logout, isLoading,
    }), [user, isLogged, login, logout, isLoading]);

    return (
        <UserContext.Provider value={contextValues}>
            {children}
        </UserContext.Provider>
    );
}