import { Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import React from 'react';
import { useParams } from 'react-router-dom';
import { parseDate } from '../../utils/helper';
import { useFetchPayments } from './hooks';
import './style.scss';

export default function AccountPayments() {
  const { accountId } = useParams()
  const payments = useFetchPayments(accountId || null);

  return (
    <div className="payments-container">
      <div>
        <p>Account: {accountId}</p>
      </div>

      <div className="content">
        <TableContainer component={Paper}>
          <Table sx={{ minWidth: 650 }} aria-label="simple table">
            <TableHead>
              <TableRow>
                <TableCell>Payment Id</TableCell>
                <TableCell>Date</TableCell>
                <TableCell>To</TableCell>
                <TableCell>Iban</TableCell>
                <TableCell>Amount</TableCell>
                <TableCell>Currency</TableCell>
                <TableCell>Status</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {payments && payments.map((row) => (
                <TableRow
                  key={row.paymentId}
                  sx={{ '&:last-child td, &:last-child th': { border: 0 } }}
                >
                  <TableCell component="th" scope="row">
                    {row.paymentId}
                  </TableCell>
                  <TableCell align="right">{parseDate(row.date)}</TableCell>
                  <TableCell align="right">{row.creditorName}</TableCell>
                  <TableCell align="right">{row.creditorIban}</TableCell>
                  <TableCell align="right">{row.amount}</TableCell>
                  <TableCell align="right">{row.currency}</TableCell>
                  <TableCell align="right">{row.status}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </TableContainer>
      </div>
    </div>
  )
}
