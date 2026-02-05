import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api'
import styles from './CourseList.module.css'

export default function CourseList() {
  const [courses, setCourses] = useState([])
  const [category, setCategory] = useState('')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const url = category ? `/courses?category=${encodeURIComponent(category)}` : '/courses'
    api.get(url)
      .then(({ data }) => setCourses(data))
      .catch(() => setCourses([]))
      .finally(() => setLoading(false))
  }, [category])

  const categories = [...new Set(courses.map((c) => c.category).concat(['Développement', 'Frontend', 'Backend']))]

  return (
    <div className={styles.page}>
      <h1>Catalogue de cours</h1>
      <p className={styles.sub}>Parcourez les formations et inscrivez-vous.</p>

      <div className={styles.filters}>
        <label>
          Catégorie
          <select value={category} onChange={(e) => setCategory(e.target.value)}>
            <option value="">Toutes</option>
            {categories.map((cat) => (
              <option key={cat} value={cat}>{cat}</option>
            ))}
          </select>
        </label>
      </div>

      {loading ? (
        <p>Chargement...</p>
      ) : courses.length === 0 ? (
        <p className={styles.empty}>Aucun cours pour le moment.</p>
      ) : (
        <div className={styles.grid}>
          {courses.map((c) => (
            <Link key={c.id} to={`/courses/${c.id}`} className={styles.card}>
              <h3>{c.title}</h3>
              <p className={styles.desc}>{c.description || 'Sans description.'}</p>
              <div className={styles.meta}>
                <span>{c.category}</span>
                <span>{c.teacherName}</span>
                {c.enrollmentCount != null && <span>{c.enrollmentCount} inscrit(s)</span>}
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  )
}
