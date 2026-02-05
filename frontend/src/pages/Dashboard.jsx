import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api'
import styles from './Dashboard.module.css'

export default function Dashboard() {
  const { user } = useAuth()
  const [enrollments, setEnrollments] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user) return
    api.get('/enrollments/me')
      .then(({ data }) => setEnrollments(data))
      .catch(() => {})
      .finally(() => setLoading(false))
  }, [user])

  const isTeacher = user?.role === 'TEACHER'

  return (
    <div className={styles.page}>
      <h1>Tableau de bord</h1>
      <p className={styles.welcome}>Bienvenue, {user?.fullName}</p>

      {isTeacher ? (
        <section className={styles.section}>
          <h2>Espace enseignant</h2>
          <p>Gérez vos cours et suivez vos apprenants.</p>
          <Link to="/my-courses" className={styles.cta}>Voir mes cours</Link>
        </section>
      ) : (
        <>
          <section className={styles.section}>
            <h2>Mes inscriptions</h2>
            {loading ? (
              <p>Chargement...</p>
            ) : enrollments.length === 0 ? (
              <p className={styles.muted}>Aucune inscription. <Link to="/courses">Parcourir le catalogue</Link></p>
            ) : (
              <ul className={styles.list}>
                {enrollments.map((e) => (
                  <li key={e.id}>
                    <Link to={`/courses/${e.courseId}`}>{e.courseTitle}</Link>
                    {e.completed && <span className={styles.badge}>Terminé</span>}
                  </li>
                ))}
              </ul>
            )}
          </section>
          <section className={styles.section}>
            <h2>Recommandations</h2>
            <p>Contenus et parcours suggérés selon votre profil.</p>
            <Link to="/recommendations" className={styles.cta}>Voir les recommandations</Link>
          </section>
        </>
      )}
    </div>
  )
}
