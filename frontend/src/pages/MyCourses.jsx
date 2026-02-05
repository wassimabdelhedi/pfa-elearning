import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api'
import styles from './CourseList.module.css'

export default function MyCourses() {
  const { user } = useAuth()
  const [courses, setCourses] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!user?.userId) return
    api.get(`/courses/teacher/${user.userId}`)
      .then(({ data }) => setCourses(data))
      .catch(() => setCourses([]))
      .finally(() => setLoading(false))
  }, [user?.userId])

  return (
    <div className={styles.page}>
      <h1>Mes cours</h1>
      <p className={styles.sub}>Gérez vos formations en tant qu'enseignant.</p>

      {loading ? (
        <p>Chargement...</p>
      ) : courses.length === 0 ? (
        <p className={styles.empty}>Vous n'avez pas encore créé de cours.</p>
      ) : (
        <div className={styles.grid}>
          {courses.map((c) => (
            <Link key={c.id} to={`/courses/${c.id}`} className={styles.card}>
              <h3>{c.title}</h3>
              <p className={styles.desc}>{c.description || 'Sans description.'}</p>
              <div className={styles.meta}>
                <span>{c.category}</span>
                {c.enrollmentCount != null && <span>{c.enrollmentCount} inscrit(s)</span>}
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
