import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { FiBookOpen, FiLogOut, FiUser, FiSearch, FiGrid, FiPlusCircle, FiFileText, FiCheckSquare, FiMessageSquare } from 'react-icons/fi';

export default function Navbar() {
  const { user, isAuthenticated, isStudent, isTeacher, isAdmin, logout } = useAuth();
  const location = useLocation();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path ? 'nav-link active' : 'nav-link';

  return (
    <nav className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="navbar-logo">
          <FiBookOpen size={24} />
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
                    <FiGrid size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Dashboard
                  </Link>
                  <Link to="/search" className={isActive('/search')}>
                    <FiSearch size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Recherche
                  </Link>
                  <Link to="/courses" className={isActive('/courses')}>Cours</Link>
                  <Link to="/messages" className={isActive('/messages')}>
                    <FiMessageSquare size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Messages
                  </Link>
                </>
              )}

              {isTeacher && (
                <>
                  <Link to="/teacher/dashboard" className={isActive('/teacher/dashboard')}>
                    <FiGrid size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Dashboard
                  </Link>
                  <Link to="/teacher/create" className={isActive('/teacher/create')}>
                    <FiPlusCircle size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Cours
                  </Link>
                  <Link to="/teacher/create-exercise" className={isActive('/teacher/create-exercise')}>
                    <FiFileText size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Exercice
                  </Link>
                  <Link to="/teacher/create-quiz" className={isActive('/teacher/create-quiz')}>
                    <FiCheckSquare size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Quiz
                  </Link>
                  <Link to="/messages" className={isActive('/messages')}>
                    <FiMessageSquare size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Messages
                  </Link>
                </>
              )}

              {isAdmin && (
                <>
                  <Link to="/admin" className={isActive('/admin')}>
                    <FiGrid size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Dashboard
                  </Link>
                  <Link to="/admin/users" className={isActive('/admin/users')}>
                    <FiUser size={16} style={{ marginRight: 4, verticalAlign: 'middle' }} />
                    Utilisateurs
                  </Link>
                </>
              )}

              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginLeft: 12, paddingLeft: 12, borderLeft: '1px solid rgba(255,255,255,0.1)' }}>
                <span style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>
                  <FiUser size={14} style={{ verticalAlign: 'middle', marginRight: 4 }} />
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
