import { faCircleCheck } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { CircularProgress } from '@mui/material';
import axios from 'axios';
import React, { useEffect } from 'react'
import { useNavigate, useSearchParams } from 'react-router-dom';
import { API_ENDPOINT, BTLinks, CECLinks } from '../../utils/env';
import { BANKS, getHeaderAuthorization } from '../../utils/helper';
import { IBankOauthResponse } from '../../utils/interfaces';
import './style.scss';

export default function SuccessfulCecPayment() {
  const [searchParams] = useSearchParams();
  const oauthCode = searchParams.get("code") || null;
  const navigate = useNavigate();

  useEffect(() => {
    if (oauthCode) {
      const fetchToken = async () => {
        const { headers } = getHeaderAuthorization();
        const paymentId = localStorage.getItem("payment-id");
        localStorage.removeItem("payment-id");
        if (paymentId) {

          axios.post<IBankOauthResponse>(`${API_ENDPOINT}/payment/auth/token`, null, {
            params: {  
              code: oauthCode,
              bankName: BANKS.cec,
              redirectURI: CECLinks.paymentRedirect,
              paymentId,
            },
            headers,
            }).finally(() => {
              navigate('/', { replace: true });
            })
          }
      }

      fetchToken();
    }
  }, [oauthCode, navigate])

  return (
    <div className="success-payment-container">
      <div className="info">
        <p className="header">Payment Successful!</p>
        <FontAwesomeIcon className="icon" icon={faCircleCheck} />
        <CircularProgress />
      </div>
    </div>
  )
}
