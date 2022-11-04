import React from 'react';
import { useForm, SubmitHandler } from "react-hook-form";
import { useMutation } from "react-query";
import { IFormInputs } from "./types";
import { toast, ToastContainer } from "react-toastify";
import {useNavigate} from "react-router-dom";
import { TextField, Container, Button, CircularProgress } from '@mui/material';
import axios from "axios";
// @ts-ignore
import LogoImg from "../../assets/logo.svg";
import { useUserContext } from "../../context/UserContext";
import * as yup from 'yup';
import { LoginData } from "../../utils/interfaces";
import { yupResolver } from '@hookform/resolvers/yup'
import { API_ENDPOINT } from '../../utils/env';
import 'react-toastify/dist/ReactToastify.css';
import './style.scss';

const loginSchema = yup.object().shape({
    username: yup.string().min(5).required(),
    password: yup.string().min(5).required(),
});

function Login() {
    const { register, handleSubmit, reset } = useForm<IFormInputs>({
        resolver: yupResolver(loginSchema),
    });
    const { login, isLogged } = useUserContext();
    const navigate = useNavigate();

    const { mutateAsync, isLoading } = useMutation((input: IFormInputs) => axios.post<LoginData>(`${API_ENDPOINT}/auth/signin`, input), {
        onError: (error: any) => {
            toast.error("Login failed. Your username or password is incorrect.");
        },
        onSuccess: () => {
            toast.success("Logged in successfully");
        },
    });

    const onSubmit: SubmitHandler<IFormInputs> = async (data) => {
        const result = await mutateAsync(data);
        const { data: responseData } = result;
  
        login(responseData.token, responseData.user);
        navigate('/', { replace: true });
        reset();
    };

    const navigateToSignUp = () => { 
        navigate('/signup', { replace: true });
        reset();
    }

    return (
        <Container className="login-container">
            <div className="logo">
                <p>
                    <img src={LogoImg} alt="logo" />
                    pen Banking Aggregator
                </p>
            </div>

            <form className="form-container" onSubmit={handleSubmit(onSubmit)}>
                <TextField
                  {...register("username", { required: true })}
                  className="form-field"
                  required
                  label="Username"
                />

                <TextField
                  {...register("password", { required: true })}
                  className="form-field"
                  required
                  label="Password"
                  type="password"
                />
                <div className="buttons">
                    <Button type="submit">Login</Button>
                    <Button onClick={() => navigateToSignUp()}>Sign Up</Button>
                </div>

                {isLoading && <CircularProgress />}
            </form>
            <ToastContainer />
        </Container>
    );
}

export default Login;
