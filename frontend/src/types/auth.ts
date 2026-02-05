export enum Role {
    ADMIN = 'ADMIN',
    TEACHER = 'TEACHER',
    LEARNER = 'LEARNER',
}

export interface User {
    sub: string; // The username (email) from JWT
    role: Role;
    exp: number;
}

export interface AuthResponse {
    token: string;
}

export interface LoginRequest {
    username: string;
    password: string;
}

export interface RegisterRequest {
    username: string;
    password: string;
    role: string;
}
