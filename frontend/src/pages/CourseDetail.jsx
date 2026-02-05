import { useEffect, useState } from 'react'
import { useParams, Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import api from '../api'
import styles from './CourseDetail.module.css'

export default function CourseDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const [course, setCourse] = useState(null)
  const [enrolled, setEnrolled] = useState(false)
  const [enrollmentId, setEnrollmentId] = useState(null)
  const [loading, setLoading] = useState(true)
  const [enrolling, setEnrolling] = useState(false)
  const [progress, setProgress] = useState({}) // contentId/exerciseId -> { completed, score }

  useEffect(() => {
    api.get(`/courses/${id}`)
      .then(({ data }) => setCourse(data))
      .catch(() => setCourse(null))
      .finally(() => setLoading(false))
  }, [id])

  useEffect(() => {
    if (!user || !id) return
    api.get('/enrollments/check', { params: { courseId: id } })
      .then(({ data }) => setEnrolled(data))
      .catch(() => setEnrolled(false))
    api.get('/enrollments/me').then(({ data }) => {
      const e = data.find((x) => x.courseId === Number(id))
      if (e) {
        setEnrollmentId(e.id)
        api.get(`/progress/enrollment/${e.id}`).then(({ data: list }) => {
          const map = {}
          list.forEach((p) => {
            if (p.contentId) map[`c${p.contentId}`] = p
            if (p.exerciseId) map[`e${p.exerciseId}`] = p
          })
          setProgress(map)
        }).catch(() => {})
      }
    }).catch(() => {})
  }, [user, id])

  const handleEnroll = () => {
    if (!user) return
    setEnrolling(true)
    api.post(`/enrollments/courses/${id}`)
      .then(({ data }) => {
        setEnrolled(true)
        setEnrollmentId(data.id)
      })
      .catch(() => {})
      .finally(() => setEnrolling(false))
  }

  const markContentDone = (contentId) => {
    if (!enrollmentId) return
    api.post(`/progress/enrollment/${enrollmentId}`, { contentId, completed: true })
      .then(({ data }) => setProgress((prev) => ({ ...prev, [`c${contentId}`]: data })))
      .catch(() => {})
  }

  const submitExercise = (exerciseId, score) => {
    if (!enrollmentId) return
    api.post(`/progress/enrollment/${enrollmentId}`, { exerciseId, score, completed: true })
      .then(({ data }) => setProgress((prev) => ({ ...prev, [`e${exerciseId}`]: data })))
      .catch(() => {})
  }

  if (loading || !course) {
    return <div className={styles.page}>Chargement...</div>
  }

  return (
    <div className={styles.page}>
      <div className={styles.header}>
        <h1>{course.title}</h1>
        <p className={styles.meta}>{course.category} · {course.teacherName}</p>
        {course.description && <p className={styles.desc}>{course.description}</p>}
        {user && user.role === 'LEARNER' && !enrolled && (
          <button type="button" onClick={handleEnroll} disabled={enrolling} className={styles.btnEnroll}>
            {enrolling ? 'Inscription...' : "S'inscrire au cours"}
          </button>
        )}
        {user && enrolled && <span className={styles.badge}>Inscrit</span>}
      </div>

      <div className={styles.modules}>
        {course.modules?.map((mod) => (
          <section key={mod.id} className={styles.module}>
            <h2>{mod.title}</h2>
            {mod.contents?.map((content) => (
              <div key={content.id} className={styles.content}>
                <h3>{content.title}</h3>
                {content.type === 'VIDEO' && content.videoUrl && (
                  <p><a href={content.videoUrl} target="_blank" rel="noreferrer">Voir la vidéo</a></p>
                )}
                {content.type === 'LESSON' && content.body && <p className={styles.body}>{content.body}</p>}
                {enrolled && (
                  progress[`c${content.id}`]?.completed ? (
                    <span className={styles.done}>✓ Vu</span>
                  ) : (
                    <button type="button" onClick={() => markContentDone(content.id)} className={styles.btnDone}>Marquer comme vu</button>
                  )
                )}
              </div>
            ))}
            {mod.exercises?.map((ex) => (
              <div key={ex.id} className={styles.exercise}>
                <h3>Exercice</h3>
                <p>{ex.question}</p>
                {ex.type === 'QCM' && ex.optionsJson && (
                  <p className={styles.options}>Options : {ex.optionsJson}</p>
                )}
                {enrolled && (
                  progress[`e${ex.id}`]?.completed ? (
                    <span className={styles.done}>✓ Score : {progress[`e${ex.id}`].score ?? '-'}</span>
                  ) : (
                    <button type="button" onClick={() => submitExercise(ex.id, 1)} className={styles.btnDone}>Valider (test)</button>
                  )
                )}
              </div>
            ))}
          </section>
        ))}
      </div>

      {!user && (
        <p className={styles.loginHint}><Link to="/login">Connectez-vous</Link> pour vous inscrire et suivre votre progression.</p>
      )}
    </div>
  )
}
