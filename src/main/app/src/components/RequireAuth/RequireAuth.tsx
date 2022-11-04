import React from 'react';
import {
    Navigate,
} from 'react-router-dom';
import { useUserContext } from "../../context/UserContext";
// import { useUserContext } from '../../contexts/UserContext.tsx';

export default function RequireAuth({ children }: { children: any }) {
    const { isLogged, isLoading } = useUserContext();

    if (isLoading) {
        return "Loading...";
    }

    if (!isLogged) {
        return <Navigate to="/login" replace />;
    }

    return children;
}