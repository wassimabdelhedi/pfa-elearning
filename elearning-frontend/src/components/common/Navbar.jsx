import { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { useTheme } from '../../context/ThemeContext';
import { getUnreadCount } from '../../api/messageApi';
import {
  FiBookOpen, FiLogOut, FiUser, FiSearch, FiGrid,
  FiPlusCircle, FiFileText, FiCheckSquare, FiSun, FiMoon, FiDroplet, FiMessageSquare
} from 'react-icons/fi';

const themeIcons = {
  dark: <FiMoon size={14} />,
  blue: <FiDroplet size={14} />,
  light: <FiSun size={14} />,
};

const themeLabels = {
  dark: 'Dark',
  blue: 'Blue',
  light: 'Light',
};

export default function Navbar() {
  const { user, isAuthenticated, isStudent, isTeacher, isAdmin, logout } = useAuth();
  const { theme, cycleTheme } = useTheme();
  const [unreadCount, setUnreadCount] = useState(0);
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    let interval;
    if (isAuthenticated) {
      const fetchCount = async () => {
        try {
          const res = await getUnreadCount();
          setUnreadCount(res.data.unreadCount);
        } catch (err) {
          console.error("Failed to fetch unread count", err);
        }
      };

      fetchCount();
      interval = setInterval(fetchCount, 10000); // Check every 10 seconds
    }
    return () => clearInterval(interval);
  }, [isAuthenticated, location.pathname]);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path ? 'nav-link active' : 'nav-link';

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="navbar-logo">
          <FiBookOpen size={22} />
          LearnAgent
        </Link>

        <div className="navbar-nav">
          {!isAuthenticated ? (
            <>
              <Link to="/login" className={isActive('/login')}>Connexion</Link>
              <Link to="/register" className="btn btn-primary btn-sm">S'inscrire</Link>
            </>
          ) : (
            <>
              {isStudent && (
                <>
                  <Link to="/dashboard" className={isActive('/dashboard')}>
                    <FiGrid size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Dashboard
                  </Link>
                  <Link to="/search" className={isActive('/search')}>
                    <FiSearch size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Recherche
                  </Link>
                  <Link to="/courses" className={isActive('/courses')}>Cours</Link>
                  <Link to="/messages" className={isActive('/messages')}>
                    <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                      <FiMessageSquare size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                      Messages
                      {unreadCount > 0 && (
                        <span className="notification-badge">{unreadCount}</span>
                      )}
                    </div>
                  </Link>
                </>
              )}

              {isTeacher && (
                <>
                  <Link to="/teacher/dashboard" className={isActive('/teacher/dashboard')}>
                    <FiGrid size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Dashboard
                  </Link>
                  <Link to="/teacher/create" className={isActive('/teacher/create')}>
                    <FiPlusCircle size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Cours
                  </Link>
                  <Link to="/teacher/create-exercise" className={isActive('/teacher/create-exercise')}>
                    <FiFileText size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Exercice
                  </Link>
                  <Link to="/teacher/create-quiz" className={isActive('/teacher/create-quiz')}>
                    <FiCheckSquare size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Quiz
                  </Link>
                  <Link to="/messages" className={isActive('/messages')}>
                    <div style={{ position: 'relative', display: 'flex', alignItems: 'center' }}>
                      <FiMessageSquare size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                      Messages
                      {unreadCount > 0 && (
                        <span className="notification-badge">{unreadCount}</span>
                      )}
                    </div>
                  </Link>
                </>
              )}

              {isAdmin && (
                <>
                  <Link to="/admin" className={isActive('/admin')}>
                    <FiGrid size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Dashboard
                  </Link>
                  <Link to="/admin/users" className={isActive('/admin/users')}>
                    <FiUser size={15} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Utilisateurs
                  </Link>
                </>
              )}

              <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginLeft: 8, paddingLeft: 12, borderLeft: '1px solid var(--border-color)' }}>
                <button
                  onClick={cycleTheme}
                  className="theme-toggle"
                  title={`Theme: ${themeLabels[theme]} — click to switch`}
                >
                  {themeIcons[theme]}
                  {themeLabels[theme]}
                </button>
                <span style={{ fontSize: '0.82rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: 4 }}>
                  <FiUser size={13} />
                  {user?.fullName}
                </span>
                <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Déconnexion">
                  <FiLogOut size={14} />
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}
