import axios from 'axios';
import React, { useEffect } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { API_ENDPOINT, CECLinks } from '../../utils/env';
import { BANKS, getHeaderAuthorization } from '../../utils/helper';
import { IBankOauthResponse } from '../../utils/interfaces';
import { CircularProgress } from '@mui/material';
import 'react-toastify/dist/ReactToastify.css';
import './style.scss';

export default function RedirectCec() {
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
            bankName: BANKS.cec,
            redirectURI: CECLinks.authRedirect,
          },
          headers,
          });

          if (data && data.accessToken && data.refreshToken) {
            localStorage.setItem("CECAccessToken", data.accessToken);

            axios.post(`${API_ENDPOINT}/accounts/save`, null, {
              params: {
                accessToken: data.accessToken,
                refreshToken: data.refreshToken,
                bankName: BANKS.cec,
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
    <div className="redirect-container">
      <div className="info">
        <p>Cec bank Redirect</p>
        <CircularProgress />
      </div>

    </div>
  )
}
