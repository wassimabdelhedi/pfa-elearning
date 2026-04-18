import { useState, useEffect } from 'react';
import { getMyEnrollments } from '../../api/userApi';
import { useNavigate } from 'react-router-dom';
import { FiAward, FiArrowLeft, FiStar, FiDownload } from 'react-icons/fi';

export default function CompletedCourses() {
  const [completedEnrollments, setCompletedEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchEnrollments();
  }, []);

  const fetchEnrollments = async () => {
    try {
      const response = await getMyEnrollments();
      const completed = response.data.filter(e => e.completed);
      setCompletedEnrollments(completed);
    } catch (err) {
      console.error('Error fetching completed courses:', err);
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
          <h1>Mes cours terminés</h1>
          <p>Félicitations pour vos réussites !</p>
        </div>
      </div>

      {completedEnrollments.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
          <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🎓</div>
          <p>Vous n'avez pas encore terminé de cours. Continuez vos efforts !</p>
          <button onClick={() => navigate('/dashboard')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
            Retour au tableau de bord
          </button>
        </div>
      ) : (
        <div className="course-grid">
          {completedEnrollments.map((enrollment) => (
            <div
              key={enrollment.id}
              className="card animate-in"
              style={{ border: '1px solid #10b981', background: 'rgba(16, 185, 129, 0.05)' }}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <h3 style={{ margin: 0, fontSize: '1.2rem' }}>{enrollment.courseTitle}</h3>
                <FiAward color="#10b981" size={24} />
              </div>
              <p className="course-meta" style={{ marginBottom: '1.5rem' }}>Par {enrollment.teacherName}</p>

              <div style={{ background: '#10b981', color: 'white', padding: '8px 16px', borderRadius: '8px', textAlign: 'center', marginBottom: '1.5rem', fontWeight: 600 }}>
                Complété à 100%
              </div>

              <div style={{ marginTop: 'auto', display: 'flex', gap: '10px' }}>
                <button 
                  onClick={() => navigate(`/course/${enrollment.courseId}`)}
                  className="btn btn-outline-primary" 
                  style={{ flex: 1 }}
                >
                  Revoir le cours
                </button>
                {/* Could add certificate download here if available */}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
