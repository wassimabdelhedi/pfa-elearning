import React, { createContext, useContext, useState, useEffect } from 'react';
import { jwtDecode } from 'jwt-decode';
import { User, LoginRequest, RegisterRequest, Role } from '../types/auth';
import { loginUser, registerUser } from '../api/auth';

interface AuthContextType {
    user: User | null;
    token: string | null;
    login: (data: LoginRequest) => Promise<void>;
    register: (data: RegisterRequest) => Promise<void>;
    logout: () => void;
    isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [user, setUser] = useState<User | null>(null);
    const [token, setToken] = useState<string | null>(localStorage.getItem('token'));
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (token) {
            try {
                const decoded = jwtDecode<any>(token);
                // Backend returns "sub" as username. We need to map role correctly.
                // Check how backend sends role in token. Usually it's in "roles" or "authorities".
                // Assuming the backend follows standard JWT structure or we might need to inspect the token.

                /* 
                * NOTE: You might need to adjust this depending on your JWT payload structure.
                * Common Spring Security structure: { sub: "user", exp: 123, roles: ["ROLE_ADMIN"] } or authorities
                */

                // Let's assume the user details are simple for now, we can debug if needed.
                const roleString = decoded.roles?.[0] || decoded.role || 'LEARNER';
                // Normalize role string (remove ROLE_ prefix if exists)
                const normalizedRole = roleString.replace('ROLE_', '') as Role;

                setUser({
                    sub: decoded.sub,
                    role: normalizedRole,
                    exp: decoded.exp
                });

                // Check expiration
                if (decoded.exp * 1000 < Date.now()) {
                    logout();
                }

            } catch (error) {
                console.error("Invalid token", error);
                logout();
            }
        }
        setIsLoading(false);
    }, [token]);

    const login = async (data: LoginRequest) => {
        const response = await loginUser(data);
        localStorage.setItem('token', response.token);
        setToken(response.token);
    };

    const register = async (data: RegisterRequest) => {
        await registerUser(data);
    };

    const logout = () => {
        localStorage.removeItem('token');
        setToken(null);
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, token, login, register, logout, isLoading }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
