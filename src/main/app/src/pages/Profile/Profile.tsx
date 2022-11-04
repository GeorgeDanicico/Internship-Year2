import { Button, LinearProgress, TextField } from '@mui/material'
import axios from 'axios';
import React, { useEffect, useState } from 'react'
import { SubmitHandler, useForm } from 'react-hook-form';
import { useMutation } from 'react-query';
import { toast, ToastContainer } from 'react-toastify'
import { API_ENDPOINT } from '../../utils/env';
import { getHeaderAuthorization } from '../../utils/helper';
import { IFormInputs } from './types';
import { useUserContext } from '../../context/UserContext';
import { yupResolver } from '@hookform/resolvers/yup'
import * as yup from 'yup';
import 'react-toastify/dist/ReactToastify.css';
import './style.scss';

const passwordSchema = yup.object().shape({
  password: yup.string().min(5).required(),
  newPassword: yup.string().min(5).required()
});

export default function Profile() {
  const [isEditable, setIsEditable] = useState<boolean>(false);
  const { user } = useUserContext();
  const {
    register, handleSubmit, setValue,
  } = useForm<IFormInputs>({ resolver: yupResolver(passwordSchema)});

  const { isLoading: isUpdateLoading, mutateAsync } = useMutation((data: any) => axios.put<IFormInputs>(`${API_ENDPOINT}/profile/change_password`, data, {
    ...getHeaderAuthorization(),
  }), {
    onError: (error: any, variables, context) => {
      console.error(error);
      toast.error(error.response.data.message);
    },
    onSuccess: (data) => {
      toast.success("You've changed your password");
      setIsEditable(false);
    },
  });

  const onSubmit: SubmitHandler<IFormInputs> = (data: IFormInputs) => {
    mutateAsync({
      oldPassword: data.password,
      newPassword: data.newPassword,
    });
  }

  useEffect(() => {
    setValue('username', user?.username || '');
    setValue('email', user?.email || '');
    setValue('firstName', user?.firstName || '');
    setValue('lastName', user?.lastName || '');
    setValue('personalNumericalCode', user?.personalNumericalCode || '');
  }, [user, setValue]);

  function handleEdit() {
    if (!isEditable) {
      setIsEditable(true);
    }
  }

  function handleCancel() {
    if (isEditable) {
      setIsEditable(false);
    }
  }

  return (
    <div className="account-details-wrapper">
      <div className="account-header">
        Account Details
      </div>

      <div className="account-details-container">
          <form className="form-details" onSubmit={handleSubmit(onSubmit)}>
            <div className="row">
              <div className="column">
                <TextField
                  {...register("username")}
                  label="Username"
                  disabled
                />
              </div>
              <div className="column">
                <TextField
                  label="Email"
                  disabled
                  {...register("email")}
                />
              </div>
            </div>
            <div className="row">
              <div className="column">
                <TextField
                  label="First Name"
                  disabled
                  {...register("firstName")}
                />
              </div>
              <div className="column">
                <TextField
                  disabled
                  label="Last Name"
                  {...register("lastName")}
                />
              </div>
            </div>
            <div className="row">
              <div className="column">
                <TextField
                  type="text"
                  disabled
                  label="Personal numerical code"
                  {...register("personalNumericalCode")}
                />
              </div>
            </div>
            <div className="row">
              <div className="column">
                <TextField
                  type="password"
                  className={isEditable ? '' : 'hide'}
                  label="Old Password"
                  {...register("password")}
                />
              </div>
              <div className="column">
                <TextField
                  type="password"
                  className={isEditable ? '' : 'hide'}
                  label="New Password"
                  {...register("newPassword")}
                />
              </div>
            </div>

            <div className="row">
              <div className="buttons">
                <Button className={`normal ${isEditable ? 'hide' : ''}`} onClick={() => handleEdit()}>Change Password</Button>
                <Button className={`danger ${!isEditable ? 'hide' : ''}`} onClick={() => handleCancel()}>Cancel</Button>
                <Button className={`normal ${!isEditable ? 'hide' : ''}`} type="submit">Save</Button>
              </div>
            </div>
            {isUpdateLoading && (
              <LinearProgress className="linear-bar" variant="indeterminate" />
            )}
          </form>
      </div>
      <ToastContainer />
    </div>
  )
}

