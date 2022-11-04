import React, { useState } from 'react'
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'
import { faEllipsisVertical } from '@fortawesome/free-solid-svg-icons'
import { Button, Menu, MenuItem, TextField } from '@mui/material';
import { IProps } from './types';
import './style.scss';
import { compareDates } from '../../utils/helper';
import { fetchTransactions } from './helper';
import Transaction from '../Transaction/Transaction';
import { useNavigate } from 'react-router-dom';
import { usePaymentContext } from '../../context/PaymentContext';

export default function AccountComponent(props: IProps) {
  const { iban, accountId, currency, 
    amount, bank, deleteAccount } = props;
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [showHistory, setShowHistory] = useState<boolean>(false);
  const [dateFrom, setDateFrom] = useState<string>("2022-07-18");
  const [dateTo, setDateTo] = useState<string>("2022-07-18");
  const [transactions, setTransactions] = useState([]);
  const navigate = useNavigate();
  const open = Boolean(anchorEl);
  const { setAccount } = usePaymentContext();

  const handleOpenMenu = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  }

  const handleCloseMenu = () => {
    setAnchorEl(null);
  }

  const handleRemoveAccount = () => {
    deleteAccount(accountId);
    handleCloseMenu();
  }

  const handleShowTransactionHistory = () => {
    setShowHistory(prev => !prev);
    setTransactions([]);
  }

  const handleSubmitPayment = () => {
    setAccount({ accountId, amount, currency, iban});
    navigate('/payment', { replace: true })
  }

  const handleShowPaymentHistory = () => {
    navigate(`/payments/${accountId}`, { replace: true })
  }

  const handleShowTransactions = async () => {
    if (!compareDates(dateFrom, dateTo)) {
      alert("Invalid dates! The difference between the dates should be at most 7 days. Date From is limited to 120 days in the past!");
      return;
    }

    const data = await fetchTransactions(dateFrom, dateTo, accountId, bank);

    if (data && data.transactions) {
      setTransactions(data.transactions);
    }
  }

  return (
    <div className="account-component">
      <div className="card-header">
        <div className="currency">
          <p>Cont curent {bank}</p>
          <p>{iban}</p>
          <p>{amount} {currency}</p>
        </div>

        <div className="account-menu">
          <Button onClick={handleOpenMenu}>
            <FontAwesomeIcon className="account-menu-icon" icon={faEllipsisVertical} />
          </Button>
          <Menu
            id="basic-menu"
            anchorEl={anchorEl}
            open={open}
            onClose={handleCloseMenu}
            MenuListProps={{
              'aria-labelledby': 'basic-button',
            }}
          >
            <MenuItem onClick={handleRemoveAccount}>Remove</MenuItem>
          </Menu>
        </div>
      </div>

      <div className="card-buttons">
        <Button className="basic" onClick={handleShowTransactionHistory}>Transaction History</Button>
        <Button className="normal" onClick={handleSubmitPayment}>Pay</Button>
        <Button className="basic" onClick={handleShowPaymentHistory}>Payment History</Button>
      </div>

      {showHistory && (
        <div className="history">
          <div className="dates-container">
            <TextField
              className="date"
              label="Date From"
              type="date"
              value={dateFrom}
              onChange={(e) => setDateFrom(e.target.value)}
            />

            <TextField
              className="date"
              label="Date To"
              type="date"
              value={dateTo}
              onChange={(e) => setDateTo(e.target.value)}

            />

            <Button onClick={handleShowTransactions}>Show</Button>
          </div>

          <div className="transactions">
            {transactions && transactions.map((t: any) => (
              <Transaction
                key={t.transactionId}
                amount={t.amount}
                currency={t.currency}
                transactionId={t.transactionId}
                date={t.date}
                creditorName={t.creditorName}
                debtorName={t.debtorName}
              />
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
