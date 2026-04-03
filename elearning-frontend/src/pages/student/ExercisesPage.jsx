import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getPublishedExercises } from '../../api/exerciseApi';
import { enrollInCourse } from '../../api/userApi';
import { FiFileText, FiUser, FiArrowLeft } from 'react-icons/fi';

export default function ExercisesPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeExercise, setActiveExercise] = useState(null);

  useEffect(() => {
    loadExercises();
  }, [id]);

  const loadExercises = async () => {
    try {
      setLoading(true);
      const res = await getPublishedExercises();
      setExercises(res.data);
      if (id) {
        const ex = res.data.find(e => e.id === parseInt(id));
        if (ex) {
          setActiveExercise(ex);
        }
      } else {
        setActiveExercise(null);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getLevelLabel = (level) => {
    const labels = { BEGINNER: 'Débutant', INTERMEDIATE: 'Intermédiaire', ADVANCED: 'Avancé' };
    return labels[level] || level;
  };

  const getLevelClass = (level) => {
    const classes = { BEGINNER: 'badge-beginner', INTERMEDIATE: 'badge-intermediate', ADVANCED: 'badge-advanced' };
    return classes[level] || 'badge-primary';
  };

  const handleDownload = (exerciseId) => {
    window.open(`http://localhost:8081/api/exercises/${exerciseId}/download`, '_blank');
  };

  const closeExercise = () => {
    setActiveExercise(null);
    navigate('/exercises');
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  if (activeExercise) {
    return (
      <div className="page" style={{ maxWidth: 800, margin: '0 auto' }}>
        <button onClick={closeExercise} className="btn" style={{ marginBottom: 20, background: 'transparent', border: 'none', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: 8, cursor: 'pointer' }}>
          <FiArrowLeft /> Retour aux exercices
        </button>
        <div className="card" style={{ padding: 32 }}>
          <div style={{ display: 'flex', gap: 10, marginBottom: 16 }}>
            {activeExercise.categoryName && (
              <span className="badge badge-primary">{activeExercise.categoryName}</span>
            )}
            {activeExercise.level && (
              <span className={`badge ${getLevelClass(activeExercise.level)}`}>
                {getLevelLabel(activeExercise.level)}
              </span>
            )}
          </div>
          <h1 style={{ marginBottom: 16 }}>{activeExercise.title}</h1>
          
          <div style={{ padding: '16px', background: 'rgba(255,255,255,0.03)', borderRadius: 12, marginBottom: 24 }}>
            <p style={{ margin: 0, color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: 8, marginBottom: 8 }}>
              <FiUser size={16} /> Par {activeExercise.teacherName}
            </p>
            {activeExercise.courseName && (
              <p style={{ margin: 0, color: 'var(--accent-400)' }}>
                📚 Associé au cours : <strong>{activeExercise.courseName}</strong>
              </p>
            )}
          </div>

          <div style={{ marginBottom: 32 }}>
            <h3 style={{ marginBottom: 12 }}>Description</h3>
            <p style={{ lineHeight: 1.6, color: 'var(--text-secondary)' }}>
              {activeExercise.description || "Aucune description fournie."}
            </p>
          </div>

          <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: 24, display: 'flex', justifyContent: 'center' }}>
            {activeExercise.filePath ? (
              <button
                className="btn btn-primary btn-lg"
                onClick={() => handleDownload(activeExercise.id)}
              >
                <FiFileText size={18} style={{ marginRight: 8 }} />
                Télécharger l'exercice
              </button>
            ) : (
              <div style={{ padding: 16, background: 'rgba(255,255,255,0.05)', borderRadius: 8, color: 'var(--text-secondary)', textAlign: 'center', width: '100%' }}>
                Aucun fichier n'a été attaché à cet exercice.
              </div>
            )}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>📝 Exercices</h1>
        <p>{exercises.length} exercices disponibles</p>
      </div>

      {exercises.length > 0 ? (
        <div className="course-grid">
          {exercises.map(exercise => (
            <div key={exercise.id} className="card course-card animate-in">
              <div className="course-card-header">
                {exercise.categoryName && (
                  <span className="badge badge-primary">{exercise.categoryName}</span>
                )}
                {exercise.level && (
                  <span className={`badge ${getLevelClass(exercise.level)}`}>
                    {getLevelLabel(exercise.level)}
                  </span>
                )}
              </div>
              <div className="course-card-body">
                <h3>{exercise.title}</h3>
                <p>{exercise.description}</p>
                {exercise.courseName && (
                  <p style={{ fontSize: '0.8rem', color: 'var(--accent-400)', marginTop: 8, fontStyle: 'italic' }}>
                    📚 Cours associé : {exercise.courseName}
                  </p>
                )}
              </div>
              <div className="course-card-footer">
                <span className="course-meta">
                  <FiUser size={13} /> {exercise.teacherName}
                </span>
                {exercise.filePath && (
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={() => navigate(`/exercises/${exercise.id}`)}
                  >
                    Détails
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-secondary)' }}>
          Aucun exercice disponible pour le moment
        </div>
      )}
    </div>
  );
}
