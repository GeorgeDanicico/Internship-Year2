import React from 'react'
import { IProps } from './types'
import "./style.scss"
import { Tooltip } from '@mui/material';

export default function Transaction(props: IProps) {
  const { amount, currency, date, transactionId, creditorName, debtorName } = props;
  const d = new Date(date);

  return (
    <Tooltip title={transactionId}>
      <div className="transaction">
        <p className="element">{amount} {currency}</p>
        <p className="element">{`${d.getFullYear()}-${d.getMonth() + 1}-${d.getDate()}`}</p>
        <p className="element">To {creditorName}</p>
      </div>
    </Tooltip>
  )
}
