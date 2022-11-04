import { Button, FormControl, InputLabel, MenuItem, 
  Select,
} from '@mui/material';
import React from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { BANKS, getHeaderAuthorization } from '../../utils/helper';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { IFormInputs } from './types';
import { API_ENDPOINT } from '../../utils/env';
import axios from 'axios';
import './style.scss';

const addAccountSchema = yup.object().shape({
  bank: yup.string().required(),
})

export default function AddAccount() {
  const navigate = useNavigate();
  const { register, handleSubmit, reset } = useForm<IFormInputs>({
    resolver: yupResolver(addAccountSchema),
  });

  const onSubmit: SubmitHandler<IFormInputs> = (data) => {
    const { headers } = getHeaderAuthorization();

    axios.post<any>(`${API_ENDPOINT}/bank/`, null, {
      params: {
        bankName: data.bank,
      },
      headers,
    })
      .then((response) => {
        const { consentId, redirectLink } = response.data;
        // localStorage.setItem(data.bank, consentId);
        window.location.href = redirectLink;
      })
      .catch((error) => {
        console.log(error);
      })
  };

  function navigateBackToDashboard() {
    navigate('/', { replace: true });
  }

  function cancel() {
    reset();
  }

  return (
    <div className="add-account-container">
      <div className="header">
        <p>Add account</p>

        <Button className="back-button" onClick={navigateBackToDashboard}>Back</Button>
      </div>

      <div className="content">
        <form className="form-container" onSubmit={handleSubmit(onSubmit)}>
          <FormControl className="bank" fullWidth>
            <InputLabel id="bank-selector">Bank</InputLabel>
            <Select
              labelId="bank-selector"
              label="Bank"
              defaultValue=""
              {...register("bank")}
              onChange={() => {}}
            >
              <MenuItem value={BANKS.bt}>BT</MenuItem>
              <MenuItem value={BANKS.brd}>BRD</MenuItem>
              <MenuItem value={BANKS.cec}>CEC Bank</MenuItem>
            </Select>
          </FormControl>
          <div className="buttons">
            <Button type="submit" className="normal">Save</Button>
            <Button className="danger" onClick={cancel}>Cancel</Button>
          </div>
        </form>
      </div>
    </div>
  )
}
