import axios from 'axios';
import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { API_ENDPOINT, BTLinks } from '../../utils/env';
import { BANKS, getHeaderAuthorization } from '../../utils/helper';
import { IBankOauthResponse } from '../../utils/interfaces';
import 'react-toastify/dist/ReactToastify.css';
import './style.scss';
import { CircularProgress } from '@mui/material';


export default function RedirectBT() {
  const [searchParams] = useSearchParams();
  const oauthCode = searchParams.get("code") || null;
  const navigate = useNavigate();

  useEffect(() => {
    if (oauthCode) {
      const fetchToken = async () => {
        const { headers } = getHeaderAuthorization();

        const { data } = await axios.post<IBankOauthResponse>(`${API_ENDPOINT}/bank/oauth/token`, null, {
          params: {  
            code: oauthCode,
            bankName: BANKS.bt,
            redirectURI: BTLinks.authRedirect,
          },
          headers,
          });

          if (data && data.accessToken && data.refreshToken) {
            axios.post(`${API_ENDPOINT}/accounts/save`, null, {
              params: {
                accessToken: data.accessToken,
                refreshToken: data.refreshToken,
                bankName: BANKS.bt,
              },
              headers,
            }).finally(() => {
              localStorage.setItem("BTAccessToken", data.accessToken);
              navigate('/', { replace: true });
            })
          }
      }

      fetchToken();
    }
  }, [oauthCode, navigate])

  return (
    <div className="redirect-container">
      <div className="info">
        <p>BT Neo Redirect</p>
        <CircularProgress />
      </div>

    </div>
  )
}
