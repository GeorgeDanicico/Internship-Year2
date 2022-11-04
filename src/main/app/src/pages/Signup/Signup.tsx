import React from 'react';
import { useForm, SubmitHandler } from "react-hook-form";
import { useMutation } from "react-query";
import { toast, ToastContainer } from "react-toastify";
import {useNavigate} from "react-router-dom";
import { TextField, Container, Button, CircularProgress } from '@mui/material';
import axios from "axios";
// @ts-ignore
import LogoImg from "../../assets/logo.svg";
import * as yup from 'yup';
import { LoginData } from "../../utils/interfaces";
import { yupResolver } from '@hookform/resolvers/yup'
import { API_ENDPOINT } from '../../utils/env';
import { IFormInputs, IRegisterInputs } from './types';
import 'react-toastify/dist/ReactToastify.css';
import './style.scss';

const registerSchema = yup.object().shape({
    username: yup.string().min(5).required(),
    password: yup.string().min(5).required(),
    confirmPassword: yup.string().min(5).required().oneOf([yup.ref('password')]),
    email: yup.string().min(10).email().required(),
    lastName: yup.string().min(3).required(),
    firstName: yup.string().min(2).required(),
    personalNumericalCode: yup.string().min(13).max(13).required(),
});

function Signup() {
    const { register, handleSubmit, reset } = useForm<IFormInputs>({
        resolver: yupResolver(registerSchema),
    });
    const navigate = useNavigate();

    const { mutateAsync, isLoading } = useMutation((input: IRegisterInputs) => axios.post<LoginData>(`${API_ENDPOINT}/auth/signup`, input), {
        onError: (error: any) => {
          toast.error(error.response.data.message);
        },
        onSuccess: () => {
          toast.success("Signed up successfully");
        },
    });

    const onSubmit: SubmitHandler<IFormInputs> = async (data) => {
      const registerData: IRegisterInputs = {
        username: data.username,
        email: data.email,
        password: data.password,
        lastName: data.lastName,
        firstName: data.firstName,
        personalNumericalCode: data.personalNumericalCode,
      }
      
      await mutateAsync(registerData);
      navigateToLogin();
    };

    const navigateToLogin = () => { 
        navigate('/login', { replace: true });
        reset();
    }

    return (
        <Container className="signup-container">
            <div className="logo">
                <p>
                    <img src={LogoImg} alt="logo" />
                    pen Banking Aggregator
                </p>
            </div>

            <form className="form-container" onSubmit={handleSubmit(onSubmit)}>
              <div className="row">
                <TextField
                  {...register("username")}
                  className="form-field"
                  required
                  label="Username"
                />

                <TextField
                  {...register("email")}
                  className="form-field"
                  required
                  label="Email"
                />
              </div>

              <div className="row">
                <TextField
                  {...register("password")}
                  className="form-field"
                  required
                  label="Password"
                  type="password"
                />

                <TextField
                  {...register("confirmPassword")}
                  className="form-field"
                  required
                  type="password"
                  label="Confirm Password"
                />
              </div>

              <div className="row">
                <TextField
                  {...register("lastName")}
                  className="form-field"
                  required
                  label="Last Name"
                />

                <TextField
                  {...register("firstName")}
                  className="form-field"
                  required
                  label="First Name"
                />
              </div>

              <div className="row">
                <TextField
                  {...register("personalNumericalCode")}
                  className="form-field"
                  required
                  label="CNP"
                />
              </div>

              <div className="buttons">
                  <Button type="submit">Register</Button>
                  <Button onClick={() => navigateToLogin()}>Back</Button>
              </div>

              {isLoading && <CircularProgress />}
            </form>
            <ToastContainer />
        </Container>
    );
}

export default Signup;
