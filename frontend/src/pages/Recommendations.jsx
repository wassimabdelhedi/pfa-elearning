import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import api from '../api'
import styles from './Recommendations.module.css'

export default function Recommendations() {
  const [items, setItems] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    api.get('/recommendations/me')
      .then(({ data }) => setItems(data))
      .catch(() => setItems([]))
      .finally(() => setLoading(false))
  }, [])

  const byType = {
    COURSE: 'Parcours / Cours',
    CONTENT: 'Contenu',
    REVISION: 'Révision',
  }

  return (
    <div className={styles.page}>
      <h1>Recommandations</h1>
      <p className={styles.sub}>
        Parcours de cours, contenus et exercices adaptés à votre profil et vos performances.
      </p>

      {loading ? (
        <p>Chargement...</p>
      ) : items.length === 0 ? (
        <div className={styles.empty}>
          <p>Aucune recommandation pour le moment.</p>
          <p>Inscrivez-vous à des cours pour recevoir des suggestions personnalisées.</p>
          <Link to="/courses">Voir le catalogue</Link>
        </div>
      ) : (
        <div className={styles.grid}>
          {items.map((r) => (
            <div key={r.id} className={styles.card}>
              <span className={styles.type}>{byType[r.type] || r.type}</span>
              {r.courseId && (
                <Link to={`/courses/${r.courseId}`} className={styles.title}>
                  {r.courseTitle || `Cours #${r.courseId}`}
                </Link>
              )}
              {r.reason && <p className={styles.reason}>{r.reason}</p>}
              {r.score != null && <span className={styles.score}>Pertinence : {Math.round(r.score * 100)}%</span>}
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
