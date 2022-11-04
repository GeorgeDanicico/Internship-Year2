import { Button } from '@mui/material';
import axios from 'axios';
import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import AccountComponent from '../../components/AccountComponent/AccountComponent';
import { useUserContext } from '../../context/UserContext';
import { API_ENDPOINT } from '../../utils/env';
import { getAccountsTotalAvailableAmount, getHeaderAuthorization } from '../../utils/helper';
import { IAccount } from '../../utils/interfaces';
import { useFetchAllAccounts } from './hooks';
import './style.scss';

const Dashboard = () => {
    const { user } = useUserContext();
    const navigate = useNavigate();
    const [totalBalance, setTotalBalance] = useState<string | null>(null);
    const { accounts } = useFetchAllAccounts();
    const [bankAccounts, setBankAccounts] = useState<IAccount[]>([]);
    const availableAmount = getAccountsTotalAvailableAmount(bankAccounts);

    
    useEffect(() => {
        if (accounts) {
            setBankAccounts(accounts);
        }
    }, [accounts])

    useEffect(() => {
        if (availableAmount) {
            
            availableAmount.then((value: number) => {
                const valueWith2Decimals = value.toFixed(2);
                if (totalBalance !== valueWith2Decimals) {
                    setTotalBalance(valueWith2Decimals);
                }
            })
        }
    }, [availableAmount, totalBalance])
    
    function navigateToAddAccount() {
        navigate("/add_account", { replace: true });
    }

    function handleDeleteAccount(accountId: string) {
        axios.delete(`${API_ENDPOINT}/accounts/delete/${accountId}`, getHeaderAuthorization())
            .then(() => {
                setBankAccounts(prev => prev.filter(account => account.accountId !== accountId))
            })
    }

    return (
        <div className="dashboard-page">
            <div className="header">
                <p>Welcome, <span className="user-name">{`${user?.firstName} ${user?.lastName}`}</span></p>

                <Button className="add-account-button" onClick={navigateToAddAccount}>
                    Add Account
                </Button>

                <p>Total Balance: <span className="user-name">{totalBalance} RON</span></p>
            </div>

            <div className="content">
                {bankAccounts && bankAccounts.map((account: IAccount) => (
                    <AccountComponent
                        key={account.accountId}
                        iban={account.iban}
                        accountId={account.accountId}
                        currency={account.currency}
                        amount={account.amount}
                        bank={account?.bank}
                        deleteAccount={handleDeleteAccount}
                    />
                ))}
            </div>
        </div>
    );
};

export default Dashboard;
