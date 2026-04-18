import { useState, useEffect } from 'react';
import { getMyEnrollments } from '../../api/userApi';
import { useNavigate } from 'react-router-dom';
import { FiBookOpen, FiTrendingUp, FiCheckSquare, FiAward, FiFile, FiArrowLeft } from 'react-icons/fi';

export default function EnrolledCourses() {
  const [enrollments, setEnrollments] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchEnrollments();
  }, []);

  const fetchEnrollments = async () => {
    try {
      const response = await getMyEnrollments();
      setEnrollments(response.data);
    } catch (err) {
      console.error('Error fetching enrollments:', err);
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
          <h1>Mes cours inscrits</h1>
          <p>Tous les cours auxquels vous participez</p>
        </div>
      </div>

      {enrollments.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '40px' }}>
          <p>Vous n'êtes inscrit à aucun cours pour le moment.</p>
          <button onClick={() => navigate('/search')} className="btn btn-primary" style={{ marginTop: '1rem' }}>
            Découvrir des cours
          </button>
        </div>
      ) : (
        <div className="course-grid">
          {enrollments.map((enrollment) => (
            <div
              key={enrollment.id}
              className="card animate-in"
              style={{ cursor: 'pointer' }}
              onClick={() => navigate(`/course/${enrollment.courseId}`)}
            >
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
                <h3 style={{ margin: 0, fontSize: '1.2rem' }}>{enrollment.courseTitle}</h3>
                {enrollment.completed && <span className="badge badge-success">Terminé</span>}
              </div>
              <p className="course-meta" style={{ marginBottom: '1.5rem' }}>Par {enrollment.teacherName}</p>

              <div className="progress-details" style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                <div className="progress-item">
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: 4 }}>
                    <span><FiFile style={{ verticalAlign: 'middle', marginRight: 4 }} /> Chapitres</span>
                    <span>{Math.round(enrollment.chaptersProgress)}%</span>
                  </div>
                  <div className="score-bar" style={{ height: 8 }}>
                    <div className="score-bar-fill" style={{ width: `${enrollment.chaptersProgress}%`, background: 'var(--primary-400)' }}></div>
                  </div>
                </div>

                <div className="progress-item">
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: 4 }}>
                    <span><FiCheckSquare style={{ verticalAlign: 'middle', marginRight: 4 }} /> Quiz</span>
                    <span>{Math.round(enrollment.quizzesProgress)}%</span>
                  </div>
                  <div className="score-bar" style={{ height: 8 }}>
                    <div className="score-bar-fill" style={{ width: `${enrollment.quizzesProgress}%`, background: '#10b981' }}></div>
                  </div>
                </div>

                <div className="progress-item">
                  <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: 4 }}>
                    <span><FiAward style={{ verticalAlign: 'middle', marginRight: 4 }} /> Exercices</span>
                    <span>{Math.round(enrollment.exercisesProgress)}%</span>
                  </div>
                  <div className="score-bar" style={{ height: 8 }}>
                    <div className="score-bar-fill" style={{ width: `${enrollment.exercisesProgress}%`, background: '#f59e0b' }}></div>
                  </div>
                </div>
              </div>

              <div style={{ marginTop: 20, paddingTop: 15, borderTop: '1px solid var(--border-color)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8, color: 'var(--primary-300)', fontWeight: 600 }}>
                  <FiTrendingUp /> {Math.round(enrollment.progress)}%
                </div>
                <span className="btn btn-sm btn-outline-primary">Continuer</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
