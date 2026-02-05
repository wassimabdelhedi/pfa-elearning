import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Role } from '../types/auth';

interface ProtectedRouteProps {
    allowedRoles?: Role[];
}

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ allowedRoles }) => {
    const { user, token, isLoading } = useAuth();

    if (isLoading) {
        return <div className="flex items-center justify-center h-screen">Loading...</div>;
    }

    if (!token || !user) {
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        return <div className="flex items-center justify-center h-screen text-red-500">Unauthorized Access</div>;
    }

    return <Outlet />;
};

export default ProtectedRoute;
