import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getMyEnrollments } from '../../api/userApi';
import { getRecommendations } from '../../api/searchApi';
import { getTopEnrolledCourses, getPersonalizedCourses } from '../../api/courseApi';
import { getPublishedQuizzes, getMyStudentResults } from '../../api/quizApi';
import { getMyCompletedExercises } from '../../api/exerciseApi';
import { Link, useNavigate } from 'react-router-dom';
import CourseCard from '../../components/course/CourseCard';
import { FiBookOpen, FiTrendingUp, FiAward, FiSearch, FiCheckSquare, FiFile, FiDownload } from 'react-icons/fi';

export default function Dashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [enrollments, setEnrollments] = useState([]);
  const [recommendations, setRecommendations] = useState([]);
  const [personalizedCourses, setPersonalizedCourses] = useState([]);
  const [topCourses, setTopCourses] = useState([]);
  const [completedQuizzesCount, setCompletedQuizzesCount] = useState(0);
  const [completedExercisesCount, setCompletedExercisesCount] = useState(0);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboard();
  }, []);

  const loadDashboard = async () => {
    try {
      const [enrollRes, recRes, myQuizRes, myExRes, topRes, persRes] = await Promise.all([
        getMyEnrollments().catch(() => ({ data: [] })),
        getRecommendations().catch(() => ({ data: [] })),
        getMyStudentResults().catch(() => ({ data: [] })),
        getMyCompletedExercises().catch(() => ({ data: [] })),
        getTopEnrolledCourses().catch(() => ({ data: [] })),
        getPersonalizedCourses().catch(() => ({ data: [] }))
      ]);
      setEnrollments(enrollRes.data);
      setRecommendations(recRes.data);
      setTopCourses(topRes.data);
      setPersonalizedCourses(persRes.data);
      setCompletedQuizzesCount(new Set(myQuizRes.data.map(q => q.quizId || q.id)).size);
      setCompletedExercisesCount(new Set(myExRes.data.map(e => e.exerciseId || e.id)).size);
    } catch (err) {
      console.error('Dashboard error:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleDownloadExercise = async (id, fileName) => {
    try {
      const response = await downloadExercise(id);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName || `exercice_${id}`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (err) {
      console.error('Erreur de téléchargement', err);
      alert('Erreur lors du téléchargement de l\'exercice');
    }
  };

  // Ensure uniqueness by course ID to avoid duplicate counting of retakes
  const completedCount = new Set(enrollments.filter(e => e.completed).map(e => e.courseId)).size;
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
      <div className="stats-grid" style={{ gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))' }}>
        <div className="stat-card animate-in">
          <div className="stat-icon">📚</div>
          <div className="stat-value">{new Set(enrollments.map(e => e.courseId)).size}</div>
          <div className="stat-label">Cours inscrits</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">🎓</div>
          <div className="stat-value">{completedCount}</div>
          <div className="stat-label">Cours terminés</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">📝</div>
          <div className="stat-value">{completedQuizzesCount}</div>
          <div className="stat-label">Quiz faits</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">✅</div>
          <div className="stat-value">{completedExercisesCount}</div>
          <div className="stat-label">Exercices faits</div>
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
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20, flexWrap: 'wrap', gap: 12 }}>
            <h2 style={{ fontSize: '1.3rem', margin: 0 }}>
              <FiBookOpen style={{ verticalAlign: 'middle', marginRight: 8 }} />
              Mes cours en cours
            </h2>
            <div style={{ background: 'rgba(99, 102, 241, 0.1)', padding: '6px 16px', borderRadius: 100, color: 'var(--primary-300)', fontWeight: 600, fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: 6 }}>
              <FiTrendingUp /> Progression globale: {avgProgress}%
            </div>
          </div>
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
            Recommandés par notre IA
          </h2>
          <div className="course-grid" style={{ marginBottom: 40 }}>
            {recommendations.slice(0, 6).map((rec, i) => (
              <CourseCard key={i} course={rec} />
            ))}
          </div>
        </>
      )}

      {/* Personalized Recommendations */}
      {personalizedCourses.length > 0 && (
        <>
          <h2 style={{ fontSize: '1.3rem', marginBottom: 20 }}>
            <FiAward style={{ verticalAlign: 'middle', marginRight: 8 }} />
            Cours correspondants à votre profil
          </h2>
          <div className="course-grid" style={{ marginBottom: 40 }}>
            {personalizedCourses.slice(0, 6).map((course) => (
              <CourseCard key={`pers-${course.id}`} course={course} />
            ))}
          </div>
        </>
      )}

      {/* Top Enrolled Recommendations */}
      {topCourses.length > 0 && (
        <>
          <h2 style={{ fontSize: '1.3rem', marginBottom: 20 }}>
            <FiTrendingUp style={{ verticalAlign: 'middle', marginRight: 8 }} />
            Cours les plus populaires
          </h2>
          <div className="course-grid" style={{ marginBottom: 40 }}>
            {topCourses.slice(0, 6).map((course) => (
              <CourseCard key={`top-${course.id}`} course={course} />
            ))}
          </div>
        </>
      )}

    </div>
  );
}
