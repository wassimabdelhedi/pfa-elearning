import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import styles from './Layout.module.css'

export default function Layout({ children }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className={styles.wrapper}>
      <header className={styles.header}>
        <Link to="/" className={styles.logo}>E-Learning</Link>
        <nav className={styles.nav}>
          <Link to="/courses">Catalogue</Link>
          {user && (
            <>
              <Link to="/dashboard">Tableau de bord</Link>
              <Link to="/recommendations">Recommandations</Link>
              {user.role === 'TEACHER' && <Link to="/my-courses">Mes cours</Link>}
              <span className={styles.user}>{user.fullName}</span>
              <button type="button" onClick={handleLogout} className={styles.btnLogout}>Déconnexion</button>
            </>
          )}
          {!user && (
            <>
              <Link to="/login">Connexion</Link>
              <Link to="/register">Inscription</Link>
            </>
          )}
        </nav>
      </header>
      <main className={styles.main}>
        {children}
      </main>
    </div>
  )
}
