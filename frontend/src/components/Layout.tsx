import React from 'react';
import { Outlet, Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { LogOut, LayoutDashboard, User } from 'lucide-react';

const Layout: React.FC = () => {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <div className="flex h-screen bg-gray-100">
            {/* Sidebar */}
            <aside className="w-64 bg-white shadow-md">
                <div className="p-6">
                    <h1 className="text-2xl font-bold text-blue-600">E-Learning</h1>
                </div>
                <nav className="mt-6 px-4 space-y-2">
                    <Link to="/dashboard" className="flex items-center p-3 text-gray-700 rounded-lg hover:bg-blue-50 hover:text-blue-600 transition-colors">
                        <LayoutDashboard className="w-5 h-5 mr-3" />
                        Dashboard
                    </Link>
                    <div className="flex items-center p-3 text-gray-700">
                        <User className="w-5 h-5 mr-3" />
                        <span className="capitalize">{user?.role.toLowerCase()}</span>
                    </div>
                </nav>
                <div className="absolute bottom-0 w-64 p-4 border-t">
                    <button
                        onClick={handleLogout}
                        className="flex items-center w-full p-3 text-red-600 rounded-lg hover:bg-red-50 transition-colors"
                    >
                        <LogOut className="w-5 h-5 mr-3" />
                        Logout
                    </button>
                </div>
            </aside>

            {/* Main Content */}
            <main className="flex-1 overflow-y-auto p-8">
                <Outlet />
            </main>
        </div>
    );
};

export default Layout;
