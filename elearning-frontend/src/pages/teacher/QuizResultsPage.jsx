import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getQuizResults, getQuizById } from '../../api/quizApi';
import { FiArrowLeft, FiAward, FiUsers } from 'react-icons/fi';

export default function QuizResultsPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [results, setResults] = useState([]);
  const [quizName, setQuizName] = useState('Chargement...');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadResults();
  }, [id]);

  const loadResults = async () => {
    try {
      const quizRes = await getQuizById(id);
      setQuizName(quizRes.data.title);

      const res = await getQuizResults(id);
      setResults(res.data);
    } catch (err) {
      console.error(err);
      setQuizName('Quiz introuvable ou erreur');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  const averageScore = results.length > 0 
    ? Math.round(results.reduce((acc, r) => acc + r.percentage, 0) / results.length) 
    : 0;

  return (
    <div className="page">
      <div className="page-header" style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
        <button className="btn btn-secondary" onClick={() => navigate('/teacher/dashboard')} style={{ padding: '8px' }}>
          <FiArrowLeft size={20} />
        </button>
        <div>
          <h1 style={{ marginBottom: 4 }}>Résultats complets</h1>
          <p style={{ margin: 0, color: 'var(--primary-300)' }}>📋 {quizName}</p>
        </div>
      </div>

      <div className="stats-grid" style={{ marginBottom: 40, gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))' }}>
        <div className="stat-card animate-in">
          <div className="stat-icon">👥</div>
          <div className="stat-value">{results.length}</div>
          <div className="stat-label">Participations totales</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">📈</div>
          <div className="stat-value">{averageScore}%</div>
          <div className="stat-label">Moyenne générale</div>
        </div>
      </div>

      <div className="card animate-in" style={{ padding: 0, overflow: 'hidden' }}>
        {results.length > 0 ? (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.03)' }}>
                <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Étudiant</th>
                <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Score Complet</th>
                <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>%</th>
                <th style={{ padding: '14px 20px', textAlign: 'right', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Date de passage</th>
              </tr>
            </thead>
            <tbody>
              {results.map((result, index) => (
                <tr key={index} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                  <td style={{ padding: '14px 20px' }}>
                    <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{result.studentName}</div>
                    <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{result.studentEmail}</div>
                  </td>
                  <td style={{ padding: '14px 20px', textAlign: 'center', fontWeight: 700 }}>
                    {result.score} / {result.totalQuestions}
                  </td>
                  <td style={{ padding: '14px 20px', textAlign: 'center' }}>
                    <span style={{
                      padding: '4px 12px',
                      borderRadius: 100,
                      fontSize: '0.8rem',
                      fontWeight: 700,
                      background: result.percentage >= 60 ? 'rgba(16,185,129,0.15)' : 'rgba(239,68,68,0.15)',
                      color: result.percentage >= 60 ? '#10b981' : '#ef4444'
                    }}>
                      {result.percentage}%
                    </span>
                  </td>
                  <td style={{ padding: '14px 20px', textAlign: 'right', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                    {result.submittedAt ? new Date(result.submittedAt).toLocaleDateString('fr-FR', {
                      day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit'
                    }) : '-'}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        ) : (
          <div style={{ textAlign: 'center', padding: 60 }}>
            <FiUsers size={48} color="var(--text-muted)" style={{ marginBottom: 16 }} />
            <p style={{ color: 'var(--text-secondary)' }}>Aucun résultat trouvé pour ce quiz.</p>
          </div>
        )}
      </div>
    </div>
  );
}
