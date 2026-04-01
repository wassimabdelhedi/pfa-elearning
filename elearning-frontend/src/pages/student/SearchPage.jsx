import { useState } from 'react';
import SearchBar from '../../components/search/SearchBar';
import CourseCard from '../../components/course/CourseCard';
import { searchCourses } from '../../api/searchApi';
import { enrollInCourse } from '../../api/userApi';
import { FiSearch, FiFileText, FiCheckSquare, FiBookOpen, FiUser, FiDownload } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';

export default function SearchPage() {
  const [results, setResults] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [enrollMsg, setEnrollMsg] = useState('');
  const navigate = useNavigate();

  const handleSearch = async (query) => {
    setLoading(true);
    setError('');
    setResults(null);

    try {
      const res = await searchCourses(query);
      setResults(res.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la recherche');
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async (courseId) => {
    try {
      await enrollInCourse(courseId);
      setEnrollMsg('✅ Inscription réussie !');
      setTimeout(() => setEnrollMsg(''), 3000);
    } catch (err) {
      setEnrollMsg(err.response?.data?.message || 'Erreur d\'inscription');
      setTimeout(() => setEnrollMsg(''), 3000);
    }
  };

  const handleDownloadExercise = (exerciseId) => {
    window.open(`http://localhost:8081/api/exercises/${exerciseId}/download`, '_blank');
  };

  const getLevelLabel = (level) => {
    const labels = { BEGINNER: 'Débutant', INTERMEDIATE: 'Intermédiaire', ADVANCED: 'Avancé' };
    return labels[level] || level;
  };

  const getLevelClass = (level) => {
    const classes = { BEGINNER: 'badge-beginner', INTERMEDIATE: 'badge-intermediate', ADVANCED: 'badge-advanced' };
    return classes[level] || 'badge-primary';
  };

  const courseResults = results?.recommendations?.filter(rec => rec.relevanceScore == null || rec.relevanceScore > 0.1) || [];
  const exerciseResults = results?.exercises || [];
  const quizResults = results?.quizzes || [];

  return (
    <div className="page">
      <div className="page-header" style={{ textAlign: 'center' }}>
        <h1>🔍 Recherche Intelligente</h1>
        <p>Trouvez des cours, exercices et quiz avec l'IA</p>
      </div>

      <SearchBar onSearch={handleSearch} loading={loading} />

      {error && <div className="error-message" style={{ maxWidth: 700, margin: '0 auto 20px' }}>{error}</div>}
      {enrollMsg && (
        <div style={{
          maxWidth: 700, margin: '0 auto 20px', padding: '12px 16px',
          background: 'rgba(34,197,94,0.1)', border: '1px solid rgba(34,197,94,0.2)',
          borderRadius: 8, color: '#4ade80', textAlign: 'center'
        }}>
          {enrollMsg}
        </div>
      )}

      {results && (
        <>
          {/* Results count */}
          <p style={{ color: 'var(--text-secondary)', marginBottom: 24, textAlign: 'center' }}>
            {results.totalResults} résultats trouvés pour « {results.query} »
            {courseResults.length > 0 && <span> — {courseResults.length} cours</span>}
            {exerciseResults.length > 0 && <span> · {exerciseResults.length} exercices</span>}
            {quizResults.length > 0 && <span> · {quizResults.length} quiz</span>}
          </p>

          {/* ===== COURSES SECTION ===== */}
          {courseResults.length > 0 && (
            <>
              <h2 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                <FiBookOpen size={18} color="var(--primary-400)" />
                Cours recommandés ({courseResults.length})
              </h2>
              <div className="course-grid" style={{ marginBottom: 32 }}>
                {courseResults.slice(0, 6).map((rec, i) => (
                  <CourseCard key={i} course={rec} onEnroll={handleEnroll} />
                ))}
              </div>
            </>
          )}

          {/* ===== EXERCISES SECTION ===== */}
          {exerciseResults.length > 0 && (
            <>
              <h2 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                <FiFileText size={18} color="var(--accent-400)" />
                Exercices trouvés ({exerciseResults.length})
              </h2>
              <div className="course-grid" style={{ marginBottom: 32 }}>
                {exerciseResults.map((exercise) => (
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
                      <h3>📝 {exercise.title}</h3>
                      <p>{exercise.description}</p>
                      {exercise.courseName && (
                        <p style={{ fontSize: '0.8rem', color: 'var(--accent-400)', marginTop: 8, fontStyle: 'italic' }}>
                          📚 Cours : {exercise.courseName}
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
                          onClick={() => handleDownloadExercise(exercise.id)}
                        >
                          <FiDownload size={14} /> Télécharger
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}

          {/* ===== QUIZZES SECTION ===== */}
          {quizResults.length > 0 && (
            <>
              <h2 style={{ fontSize: '1.2rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                <FiCheckSquare size={18} color="#fbbf24" />
                Quiz trouvés ({quizResults.length})
              </h2>
              <div className="course-grid" style={{ marginBottom: 32 }}>
                {quizResults.map((quiz) => (
                  <div key={quiz.id} className="card course-card animate-in" style={{ cursor: 'pointer' }} onClick={() => navigate('/quiz')}>
                    <div className="course-card-header">
                      {quiz.categoryName && (
                        <span className="badge badge-primary">{quiz.categoryName}</span>
                      )}
                      {quiz.level && (
                        <span className={`badge ${getLevelClass(quiz.level)}`}>
                          {getLevelLabel(quiz.level)}
                        </span>
                      )}
                    </div>
                    <div className="course-card-body">
                      <h3>📋 {quiz.title}</h3>
                      <p>{quiz.description}</p>
                      {quiz.questionsCount != null && (
                        <p style={{ fontSize: '0.8rem', color: 'var(--accent-400)', marginTop: 8 }}>
                          📝 {quiz.questionsCount} questions
                        </p>
                      )}
                    </div>
                    <div className="course-card-footer">
                      <span className="course-meta">
                        <FiUser size={13} /> {quiz.teacherName}
                      </span>
                      <span className="btn btn-primary btn-sm">
                        Commencer
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            </>
          )}

          {/* No results */}
          {courseResults.length === 0 && exerciseResults.length === 0 && quizResults.length === 0 && (
            <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-secondary)' }}>
              <p style={{ fontSize: '1.2rem', marginBottom: 8 }}>Aucun résultat trouvé</p>
              <p>Essayez d'autres termes de recherche</p>
            </div>
          )}
        </>
      )}

      {!results && !loading && (
        <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-muted)' }}>
          <FiSearch size={48} style={{ marginBottom: 16, opacity: 0.3 }} />
          <p style={{ fontSize: '1.1rem' }}>Tapez votre recherche pour commencer</p>
          <p style={{ fontSize: '0.9rem', marginTop: 8 }}>
            La recherche IA trouvera les cours, exercices et quiz les plus pertinents
          </p>
        </div>
      )}
    </div>
  );
}
