import { useState, useEffect } from 'react';
import { getMyCompletedExercises } from '../../api/exerciseApi';
import { useNavigate } from 'react-router-dom';
import { FiCheckCircle, FiArrowLeft, FiClock, FiFileText } from 'react-icons/fi';

export default function DoneExercises() {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchExercises();
  }, []);

  const fetchExercises = async () => {
    try {
      const response = await getMyCompletedExercises();
      setExercises(response.data);
    } catch (err) {
      console.error('Error fetching completed exercises:', err);
    } finally {
      setLoading(false);
    }
  };



  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page">
      <div className="page-header" style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <button onClick={() => navigate(-1)} className="btn btn-secondary" style={{ padding: '8px', borderRadius: '50%' }}>
          <FiArrowLeft size={20} />
        </button>
        <div>
          <h1>Mes Exercices faits</h1>
          <p>Liste des exercices que vous avez complétés</p>
        </div>
      </div>

      {exercises.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>✅</div>
          <p>Vous n'avez pas encore terminé d'exercices.</p>
          <button onClick={() => navigate('/dashboard')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
            Retour au tableau de bord
          </button>
        </div>
      ) : (
        <div className="course-grid">
          {exercises.map((ex, index) => (
            <div key={ex.id || index} className="card animate-in">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <div>
                  <h3 style={{ margin: 0, fontSize: '1.2rem' }}>{ex.title}</h3>
                  <p className="course-meta" style={{ fontSize: '0.85rem' }}>Cours: {ex.courseTitle}</p>
                </div>
                <FiCheckCircle color="#10b981" size={24} />
              </div>

              <p style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', marginBottom: '1.5rem', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>
                {ex.description || "Pas de description disponible."}
              </p>

              <div style={{ display: 'flex', gap: '10px', marginTop: 'auto' }}>
                <button 
                  onClick={() => navigate(`/exercises/${ex.exerciseId}`)}
                  className="btn btn-primary" 
                  style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '8px' }}
                >
                  <FiFileText size={16} /> Ouvrir
                </button>
                <button 
                  onClick={() => navigate(`/course/${ex.courseId}`)}
                  className="btn btn-outline-primary" 
                  style={{ flex: 1 }}
                >
                  Voir le cours
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
