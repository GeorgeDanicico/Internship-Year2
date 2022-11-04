import { CircularProgress } from '@mui/material';
import axios from 'axios';
import React, { useEffect } from 'react'
import { useNavigate } from 'react-router-dom';
import { API_ENDPOINT } from '../../utils/env';
import { BANKS, getHeaderAuthorization } from '../../utils/helper';
import "./style.scss"

export default function RedirectBRD() {
  const navigate = useNavigate();

  useEffect(() => {
    const fetchToken = () => {
      const { headers } = getHeaderAuthorization();

      axios.post(`${API_ENDPOINT}/accounts/save`, null, {
        params: {  
          accessToken: "",
          refreshToken: "",
          bankName: BANKS.brd,
        },
        headers,
        }).finally(() => {
          navigate('/', { replace: true })
        })
    }

      fetchToken();
    }, [navigate])

  return (
    <div className="redirect-container">
      <div className="info">
        <p>BRD Redirect</p>
        <CircularProgress />
      </div>

    </div>
  )
}
