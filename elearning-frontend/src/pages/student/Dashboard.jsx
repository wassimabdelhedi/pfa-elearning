import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getMyEnrollments } from '../../api/userApi';
import { getRecommendations } from '../../api/searchApi';
import { Link, useNavigate } from 'react-router-dom';
import CourseCard from '../../components/course/CourseCard';
import { FiBookOpen, FiTrendingUp, FiAward, FiSearch } from 'react-icons/fi';

export default function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [enrollments, setEnrollments] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [enrollRes, recRes] = await Promise.all([
        getMyEnrollments(),
        getRecommendations().catch(() => ({ data: [] }))
      ]);
      setEnrollments(enrollRes.data);
      setRecommendations(recRes.data);
    } catch (err) {
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  const completedCount = enrollments.filter(e => e.completed).length;
  const avgProgress = enrollments.length > 0
    ? Math.round(enrollments.reduce((acc, e) => acc + e.progress, 0) / enrollments.length)
    : 0;

  if (loading) {
    return (
      <div className="loading-container">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Bonjour, {user?.fullName?.split(' ')[0]} 👋</h1>
        <p>Voici un aperçu de votre progression</p>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon">📚</div>
          <div className="stat-value">{enrollments.length}</div>
          <div className="stat-label">Cours inscrits</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">✅</div>
          <div className="stat-value">{completedCount}</div>
          <div className="stat-label">Cours terminés</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">📈</div>
          <div className="stat-value">{avgProgress}%</div>
          <div className="stat-label">Progression moyenne</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">🤖</div>
          <div className="stat-value">{recommendations.length}</div>
          <div className="stat-label">Recommandations</div>
        </div>
      </div>

      {/* Quick search CTA */}
      <div className="card" style={{ textAlign: 'center', padding: '40px 24px', marginBottom: 32 }}>
        <FiSearch size={32} color="var(--primary-400)" />
        <h2 style={{ margin: '16px 0 8px', fontSize: '1.3rem' }}>Recherche</h2>
        <Link to="/search" className="btn btn-primary" style={{ marginTop: 12 }}>
          <FiSearch size={16} /> Commencer une recherche
        </Link>
      </div>

      {/* Enrollments - clickable to open course */}
      {enrollments.length > 0 && (
        <>
          <h2 style={{ fontSize: '1.3rem', marginBottom: 20 }}>
            <FiBookOpen style={{ verticalAlign: 'middle', marginRight: 8 }} />
            Mes cours en cours
          </h2>
          <div className="course-grid" style={{ marginBottom: 40 }}>
            {enrollments.filter(e => !e.completed).slice(0, 3).map((enrollment) => (
              <div
                key={enrollment.id}
                className="card animate-in"
                style={{ cursor: 'pointer' }}
                onClick={() => navigate(`/course/${enrollment.courseId}`)}
              >
                <h3 style={{ marginBottom: 8 }}>{enrollment.courseTitle}</h3>
                <p className="course-meta" style={{ marginBottom: 12 }}>
                  Par {enrollment.teacherName}
                </p>
                <div className="score-bar" style={{ marginBottom: 8 }}>
                  <div className="score-bar-fill" style={{ width: `${enrollment.progress}%` }}></div>
                </div>
                <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                  {Math.round(enrollment.progress)}% complété
                </span>
              </div>
            ))}
          </div>
        </>
      )}

      {/* Recommendations */}
      {recommendations.length > 0 && (
        <>
          <h2 style={{ fontSize: '1.3rem', marginBottom: 20 }}>
            <FiTrendingUp style={{ verticalAlign: 'middle', marginRight: 8 }} />
            Recommandés pour vous
          </h2>
          <div className="course-grid">
            {recommendations.slice(0, 6).map((rec, i) => (
              <CourseCard key={i} course={rec} />
            ))}
          </div>
        </>
      )}
    </div>
  );
}
