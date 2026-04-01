import { useState, useEffect } from 'react';
import { getPublishedExercises } from '../../api/exerciseApi';
import { enrollInCourse } from '../../api/userApi';
import { FiFileText, FiUser } from 'react-icons/fi';

export default function ExercisesPage() {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadExercises();
  }, []);

  const loadExercises = async () => {
    try {
      const res = await getPublishedExercises();
      setExercises(res.data);
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

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
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
                    onClick={() => handleDownload(exercise.id)}
                  >
                    <FiFileText size={14} /> Télécharger
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
