import { createContext, useContext, useState, useEffect } from 'react'
import api from '../api'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('token')
    const saved = localStorage.getItem('user')
    if (token && saved) {
      try {
        const u = JSON.parse(saved)
        setUser(u)
        api.get('/users/me').then(({ data }) => {
          setUser((prev) => ({ ...prev, ...data }))
        }).catch(() => {
          localStorage.removeItem('token')
          localStorage.removeItem('user')
          setUser(null)
        }).finally(() => setLoading(false))
      } catch {
        setUser(null)
        setLoading(false)
      }
    } else {
      setLoading(false)
    }
  }, [])

  const login = async (email, password) => {
    const { data } = await api.post('/auth/login', { email, password })
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
    }))
    setUser({ userId: data.userId, email: data.email, fullName: data.fullName, role: data.role })
    return data
  }

  const register = async (email, password, fullName, role = 'LEARNER') => {
    const { data } = await api.post('/auth/register', { email, password, fullName, role })
    localStorage.setItem('token', data.token)
    localStorage.setItem('user', JSON.stringify({
      userId: data.userId,
      email: data.email,
      fullName: data.fullName,
      role: data.role,
    }))
    setUser({ userId: data.userId, email: data.email, fullName: data.fullName, role: data.role })
    return data
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
