import api from './axios';
import { LoginRequest, RegisterRequest, AuthResponse } from '../types/auth';

export const loginUser = async (data: LoginRequest): Promise<AuthResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', data);
    return response.data;
};

export const registerUser = async (data: RegisterRequest): Promise<void> => {
    await api.post('/auth/register', data);
};
