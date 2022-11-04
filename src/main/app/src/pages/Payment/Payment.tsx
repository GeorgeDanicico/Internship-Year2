import { Button, CircularProgress, FormControl, InputLabel, MenuItem, Select, TextField } from '@mui/material';
import React from 'react';
import { SubmitHandler, useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { IPaymentInputs } from './types';
import { executePayment, useFetchAccountsForPayments } from './hooks';
import { useMutation } from 'react-query';
import { toast, ToastContainer } from 'react-toastify';
import { getCurrentDate } from '../../utils/helper';
import { usePaymentContext } from '../../context/PaymentContext';
import './style.scss';
import 'react-toastify/dist/ReactToastify.css';

const paymentSchema = yup.object().shape({
  debtorAccountId: yup.string().required(),
  creditorName: yup.string().min(5).required(),
  creditorIban: yup.string().min(16).max(32).required(),
  paymentAmount: yup.number().required(),
  details: yup.string().min(5).required(),
});

export default function Payment() {
  const { account } = usePaymentContext();
  const { accounts } = useFetchAccountsForPayments();
  const { register, handleSubmit, reset } = useForm<IPaymentInputs>({
    resolver: yupResolver(paymentSchema),
  });
  const { mutateAsync, isLoading } = useMutation((input: IPaymentInputs) => executePayment(input), {
    onSuccess(data, variables, context) {
      toast.success(`Payment #${data?.data?.paymentId} confirmed.`)
      reset({
        debtorAccountId: "",
        creditorName: "",
        creditorIban: "",
        paymentAmount: 0,
        details: "",
      });
    },
    onError: (error: any) => {
      toast.error(error.response.data);
    },
  });

  const onSubmit: SubmitHandler<IPaymentInputs> = async (data) => {
    const result = await mutateAsync({ ...data, paymentDate: getCurrentDate(), });
    const redirectLink = result.data?.redirectLink;
    const paymentId = result.data?.paymentId;

    if (redirectLink && paymentId) {
      localStorage.setItem("payment-id", result.data?.paymentId);
      console.log(redirectLink);
      window.location.href = redirectLink;
    }
  };

  return (
    <div className="payment-container">
      <div className="title">
        Payment
      </div>

      <form className="content" onSubmit={handleSubmit(onSubmit)}>
        <div className="header">
          <p>My account</p>

          <FormControl fullWidth>
            <InputLabel id="label">Current account</InputLabel>
            <Select
              {...register("debtorAccountId")}
              labelId="label"
              id="select"
              label="Current account"
              defaultValue={account?.accountId}
            >
              {accounts && accounts.map((account) => (
                <MenuItem key={account.accountId} value={account.accountId}>
                  {account.iban} {account.bank}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </div>

        <div className="benficiary-data">
          <p>Beneficiary's Account</p>

          <TextField
            className="form-field"
            required
            label="Name"
            {...register("creditorName")}
          />

          <TextField
            className="form-field"
            required
            label="IBAN"
            {...register("creditorIban")}
          />

          <TextField
            className="form-field"
            required
            label="Amount"
            {...register("paymentAmount")}
          />

          <TextField
            className="form-field"
            required
            label="Details"
            {...register("details")}
          />
        </div>
        
        <Button className="button" type="submit">
          {isLoading ? (
            <CircularProgress />
          ) : "Pay"}
        </Button>
      </form>
      <ToastContainer />
    </div>
  )
}
