import { useState, useEffect } from 'react';
import { getMyStudentResults } from '../../api/quizApi';
import { useNavigate } from 'react-router-dom';
import { FiCheckSquare, FiArrowLeft, FiClock, FiAward, FiBarChart2 } from 'react-icons/fi';

export default function DoneQuizzes() {
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchResults();
  }, []);

  const fetchResults = async () => {
    try {
      const response = await getMyStudentResults();
      // Sort by date descending to ensure the most recent is first
      const sortedResults = response.data.sort((a, b) => new Date(b.submittedAt) - new Date(a.submittedAt));
      
      // Filter to keep only the most recent result for each quiz
      const uniqueResults = [];
      const seenQuizIds = new Set();
      
      sortedResults.forEach(result => {
        if (!seenQuizIds.has(result.quizId)) {
          seenQuizIds.add(result.quizId);
          uniqueResults.push(result);
        }
      });
      
      setResults(uniqueResults);
    } catch (err) {
      console.error('Error fetching quiz results:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Récemment';
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
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
          <h1>Mes Quiz réalisés</h1>
          <p>Historique de vos évaluations par quiz</p>
        </div>
      </div>

      {results.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📝</div>
          <p>Vous n'avez pas encore passé de quiz.</p>
          <button onClick={() => navigate('/dashboard')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
            Retour au tableau de bord
          </button>
        </div>
      ) : (
        <div className="course-grid">
          {results.map((result, index) => (
            <div key={result.id || index} className="card animate-in">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <div>
                  <h3 style={{ margin: 0, fontSize: '1.2rem' }}>{result.quizTitle || 'Quiz'}</h3>
                  <p className="course-meta" style={{ fontSize: '0.85rem' }}>Cours: {result.courseTitle}</p>
                </div>
                <div style={{ 
                  background: result.percentage >= 60 ? 'rgba(16, 185, 129, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                  color: result.percentage >= 60 ? '#10b981' : '#ef4444',
                  padding: '4px 12px',
                  borderRadius: '100px',
                  fontWeight: 700,
                  fontSize: '1rem'
                }}>
                  {Math.round(result.percentage)}%
                </div>
              </div>

              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px', marginBottom: '1.5rem' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                  <FiClock size={14} /> Passé le: {formatDate(result.submittedAt)}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                  <FiBarChart2 size={14} /> {result.score} bonnes réponses sur {result.totalQuestions}
                </div>
              </div>

              <div className="score-bar" style={{ height: 8, marginBottom: '1.5rem' }}>
                <div 
                  className="score-bar-fill" 
                  style={{ 
                    width: `${result.percentage}%`, 
                    background: result.percentage >= 60 ? '#10b981' : '#ef4444' 
                  }}
                ></div>
              </div>

              <button 
                onClick={() => navigate(`/quiz/${result.quizId || result.id}`)}
                className="btn btn-outline-primary" 
                style={{ width: '100%' }}
              >
                Repasser le quiz
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
