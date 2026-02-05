import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import styles from './Auth.module.css'

export default function Register() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [role, setRole] = useState('LEARNER')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const { register, user } = useAuth()
  const navigate = useNavigate()

  if (user) {
    navigate('/dashboard', { replace: true })
    return null
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await register(email, password, fullName, role)
      navigate('/dashboard', { replace: true })
    } catch (err) {
      setError(err.response?.data?.message || err.message || 'Inscription impossible')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1>Inscription</h1>
        <p className={styles.sub}>Créez votre compte apprenant ou enseignant</p>
        {error && <p className={styles.error}>{error}</p>}
        <form onSubmit={handleSubmit}>
          <label>
            Nom complet
            <input type="text" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
          </label>
          <label>
            Email
            <input type="email" value={email} onChange={(e) => setEmail(e.target.value)} required autoComplete="email" />
          </label>
          <label>
            Mot de passe (min. 6 caractères)
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required minLength={6} autoComplete="new-password" />
          </label>
          <label>
            Je suis
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="LEARNER">Apprenant</option>
              <option value="TEACHER">Enseignant</option>
            </select>
          </label>
          <button type="submit" disabled={loading}>{loading ? 'Inscription...' : "S'inscrire"}</button>
        </form>
        <p className={styles.footer}>Déjà un compte ? <Link to="/login">Se connecter</Link></p>
      </div>
    </div>
  )
}
