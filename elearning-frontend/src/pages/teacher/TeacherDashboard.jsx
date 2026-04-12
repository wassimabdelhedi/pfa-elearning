import { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { getMyTeacherCourses, deleteCourse, togglePublishCourse } from '../../api/courseApi';
import { getMyTeacherExercises, deleteExercise, togglePublishExercise } from '../../api/exerciseApi';
import { getMyTeacherQuizzes, deleteQuiz, togglePublishQuiz, getMyQuizResults } from '../../api/quizApi';
import { FiBookOpen, FiUsers, FiPlusCircle, FiTrash2, FiFileText, FiCheckSquare, FiAward, FiEye, FiEyeOff, FiLayers } from 'react-icons/fi';
import { Link, useNavigate } from 'react-router-dom';

export default function TeacherDashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [courses, setCourses] = useState([]);
  const [exercises, setExercises] = useState([]);
  const [quizzes, setQuizzes] = useState([]);
  const [quizResults, setQuizResults] = useState([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');
  
  // Student modal state
  const [showStudentsModal, setShowStudentsModal] = useState(false);
  const [selectedCourseStudents, setSelectedCourseStudents] = useState([]);
  const [selectedCourseName, setSelectedCourseName] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [coursesRes, exercisesRes, quizzesRes, resultsRes] = await Promise.all([
        getMyTeacherCourses(),
        getMyTeacherExercises().catch(() => ({ data: [] })),
        getMyTeacherQuizzes().catch(() => ({ data: [] })),
        getMyQuizResults().catch(() => ({ data: [] }))
      ]);
      setCourses(coursesRes.data);
      setExercises(exercisesRes.data);
      setQuizzes(quizzesRes.data);
      setQuizResults(resultsRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const showMsg = (text) => {
    setMsg(text);
    setTimeout(() => setMsg(''), 3000);
  };

  const handleDeleteCourse = async (courseId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce cours ? Cette action est irréversible.')) return;
    try {
      await deleteCourse(courseId);
      setCourses(courses.filter(c => c.id !== courseId));
      showMsg('Cours supprimé avec succès');
    } catch (err) {
      showMsg(err.response?.data?.message || 'Erreur lors de la suppression');
    }
  };

  const handleTogglePublishCourse = async (courseId) => {
    try {
      const res = await togglePublishCourse(courseId);
      setCourses(courses.map(c => c.id === courseId ? res.data : c));
      showMsg(`✅ Cours ${res.data.published ? 'publié' : 'retiré des publiés'} avec succès`);
    } catch (err) {
      showMsg(err.response?.data?.message || 'Erreur lors de la modification');
    }
  };

  const handleDeleteExercise = async (exerciseId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cet exercice ?')) return;
    try {
      await deleteExercise(exerciseId);
      setExercises(exercises.filter(e => e.id !== exerciseId));
      showMsg('✅ Exercice supprimé avec succès');
    } catch (err) {
      showMsg(err.response?.data?.message || 'Erreur lors de la suppression');
    }
  };

  const handleTogglePublishExercise = async (exerciseId) => {
    try {
      const res = await togglePublishExercise(exerciseId);
      setExercises(exercises.map(e => e.id === exerciseId ? res.data : e));
      showMsg(`✅ Exercice ${res.data.published ? 'publié' : 'retiré des publiés'} avec succès`);
    } catch (err) {
      showMsg(err.response?.data?.message || 'Erreur lors de la modification');
    }
  };

  const handleDeleteQuiz = async (quizId) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer ce quiz ?')) return;
    try {
      await deleteQuiz(quizId);
      setQuizzes(quizzes.filter(q => q.id !== quizId));
      showMsg('Quiz supprimé avec succès');
    } catch (err) {
      showMsg(err.response?.data?.message || 'Erreur lors de la suppression');
    }
  };

  const handleTogglePublishQuiz = async (quizId) => {
    try {
      const res = await togglePublishQuiz(quizId);
      setQuizzes(quizzes.map(q => q.id === quizId ? res.data : q));
      showMsg(`✅ Quiz ${res.data.published ? 'publié' : 'retiré des publiés'} avec succès`);
    } catch (err) {
      showMsg(err.response?.data?.message || 'Erreur lors de la modification');
    }
  };

  const handleCourseClick = (courseId) => {
    navigate(`/course/${courseId}`);
  };

  const handleViewStudents = async (course) => {
    try {
      const { getCourseStudents } = await import('../../api/courseApi');
      const res = await getCourseStudents(course.id);
      setSelectedCourseStudents(res.data);
      setSelectedCourseName(course.title);
      setShowStudentsModal(true);
    } catch (err) {
      showMsg('❌ Impossible de charger la liste des étudiants');
    }
  };

  const getLevelLabel = (level) => {
    const labels = { BEGINNER: 'Débutant', INTERMEDIATE: 'Intermédiaire', ADVANCED: 'Avancé' };
    return labels[level] || level;
  };

  const publishedCourses = courses.filter(c => c.published).length;
  const totalEnrollments = courses.reduce((acc, c) => acc + (c.enrollmentCount || 0), 0);

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>Tableau de bord Enseignant</h1>
        <p>Bienvenue, {user?.fullName}</p>
      </div>

      {msg && (
        <div style={{
          padding: '12px 16px',
          background: msg.includes('✅') ? 'rgba(34,197,94,0.1)' : 'rgba(239,68,68,0.1)',
          border: `1px solid ${msg.includes('✅') ? 'rgba(34,197,94,0.2)' : 'rgba(239,68,68,0.2)'}`,
          borderRadius: 8,
          color: msg.includes('✅') ? '#4ade80' : '#f87171',
          textAlign: 'center',
          marginBottom: 20
        }}>
          {msg}
        </div>
      )}

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card animate-in">
          <div className="stat-icon">📚</div>
          <div className="stat-value">{courses.length}</div>
          <div className="stat-label">Cours</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">📝</div>
          <div className="stat-value">{exercises.length}</div>
          <div className="stat-label">Exercices</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">📋</div>
          <div className="stat-value">{quizzes.length}</div>
          <div className="stat-label">Quiz</div>
        </div>
        <div className="stat-card animate-in">
          <div className="stat-icon">👥</div>
          <div className="stat-value">{totalEnrollments}</div>
          <div className="stat-label">Inscriptions totales</div>
        </div>
      </div>

      {/* ====== COURSES SECTION ====== */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: '1.3rem' }}>
          <FiBookOpen style={{ verticalAlign: 'middle', marginRight: 8 }} />
          Mes cours
        </h2>
        <Link to="/teacher/create" className="btn btn-primary">
          <FiPlusCircle size={16} /> Nouveau cours
        </Link>
      </div>

      {courses.length > 0 ? (
        <div className="course-grid" style={{ marginBottom: 40 }}>
          {courses.map(course => (
            <div key={course.id} className="card course-card animate-in" style={{ cursor: 'pointer' }}>
              <div className="course-card-header" onClick={() => handleCourseClick(course.id)}>
                <span className={`badge ${course.published ? 'badge-success' : 'badge-warning'}`}>
                  {course.published ? 'Publié' : 'Brouillon'}
                </span>
                {course.level && (
                  <span className={`badge badge-${course.level === 'BEGINNER' ? 'beginner' : course.level === 'INTERMEDIATE' ? 'intermediate' : 'advanced'}`}>
                    {getLevelLabel(course.level)}
                  </span>
                )}
              </div>
              <div className="course-card-body" onClick={() => handleCourseClick(course.id)}>
                <h3>{course.title}</h3>
                <p>{course.description}</p>
              </div>
              <div className="course-card-footer">
                <span className="course-meta">
                  <FiUsers size={13} /> {course.enrollmentCount || 0} inscrits
                </span>
                <div style={{ display: 'flex', alignItems: 'center', gap: 6, flexWrap: 'wrap', justifyContent: 'flex-end', marginTop: '8px' }}>
                  <span className="course-meta" style={{ marginRight: 'auto' }}>
                    ⭐ {course.averageRating ? course.averageRating.toFixed(1) : 'N/A'}
                  </span>
                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={(e) => { e.stopPropagation(); navigate(`/teacher/course/${course.id}/chapters`); }}
                    title="Gérer les chapitres"
                    style={{ fontSize: '0.75rem' }}
                  >
                    <FiLayers size={14} /> {course.chapterCount || 0} ch.
                  </button>
                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={(e) => { e.stopPropagation(); handleTogglePublishCourse(course.id); }}
                    title={course.published ? "Passer en brouillon" : "Publier ce cours"}
                  >
                    {course.published ? <FiEyeOff size={14} /> : <FiEye size={14} />}
                  </button>
                  <button
                    className="btn btn-secondary btn-sm"
                    style={{ fontSize: '0.75rem', background: 'rgba(34,197,94,0.1)', color: '#4ade80', borderColor: 'rgba(34,197,94,0.2)' }}
                    onClick={(e) => { e.stopPropagation(); handleViewStudents(course); }}
                    title="Voir l'avancement des étudiants inscrits"
                  >
                    <FiUsers size={14} /> Suivi
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={(e) => { e.stopPropagation(); handleDeleteCourse(course.id); }}
                    title="Supprimer ce cours"
                  >
                    <FiTrash2 size={14} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: 60, marginBottom: 40 }}>
          <FiPlusCircle size={48} color="var(--text-muted)" style={{ marginBottom: 16 }} />
          <p style={{ color: 'var(--text-secondary)', marginBottom: 16 }}>
            Vous n'avez pas encore de cours
          </p>
          <Link to="/teacher/create" className="btn btn-primary">
            Créer votre premier cours
          </Link>
        </div>
      )}

      {/* ====== EXERCISES SECTION ====== */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: '1.3rem' }}>
          <FiFileText style={{ verticalAlign: 'middle', marginRight: 8 }} />
          Mes exercices
        </h2>
        <Link to="/teacher/create-exercise" className="btn btn-primary">
          <FiPlusCircle size={16} /> Nouvel exercice
        </Link>
      </div>

      {exercises.length > 0 ? (
        <div className="course-grid" style={{ marginBottom: 40 }}>
          {exercises.map(exercise => (
            <div key={exercise.id} className="card course-card animate-in" style={{ cursor: 'pointer' }}>
              <div className="course-card-header" onClick={() => navigate(`/exercises/${exercise.id}`)}>
                <span className={`badge ${exercise.published ? 'badge-success' : 'badge-warning'}`}>
                  {exercise.published ? 'Publié' : 'Brouillon'}
                </span>
                {exercise.level && (
                  <span className={`badge badge-${exercise.level === 'BEGINNER' ? 'beginner' : exercise.level === 'INTERMEDIATE' ? 'intermediate' : 'advanced'}`}>
                    {getLevelLabel(exercise.level)}
                  </span>
                )}
              </div>
              <div className="course-card-body" onClick={() => navigate(`/exercises/${exercise.id}`)}>
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
                  {exercise.originalFileName ? `📄 ${exercise.originalFileName}` : 'Pas de fichier'}
                </span>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={(e) => { e.stopPropagation(); handleTogglePublishExercise(exercise.id); }}
                    title={exercise.published ? "Passer en brouillon" : "Publier cet exercice"}
                  >
                    {exercise.published ? <FiEyeOff size={14} /> : <FiEye size={14} />}
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDeleteExercise(exercise.id)}
                    title="Supprimer cet exercice"
                  >
                    <FiTrash2 size={14} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: 40, marginBottom: 40 }}>
          <FiFileText size={40} color="var(--text-muted)" style={{ marginBottom: 12 }} />
          <p style={{ color: 'var(--text-secondary)', marginBottom: 12 }}>
            Aucun exercice créé
          </p>
          <Link to="/teacher/create-exercise" className="btn btn-primary btn-sm">
            Créer un exercice
          </Link>
        </div>
      )}

      {/* ====== QUIZZES SECTION ====== */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: '1.3rem' }}>
          <FiCheckSquare style={{ verticalAlign: 'middle', marginRight: 8 }} />
          Mes quiz
        </h2>
        <Link to="/teacher/create-quiz" className="btn btn-primary">
          <FiPlusCircle size={16} /> Nouveau quiz
        </Link>
      </div>

      {quizzes.length > 0 ? (
        <div className="course-grid">
          {quizzes.map(quiz => (
            <div key={quiz.id} className="card course-card animate-in" style={{ cursor: 'pointer' }}>
              <div className="course-card-header" onClick={() => navigate(`/quiz/${quiz.id}`)}>
                <span className={`badge ${quiz.published ? 'badge-success' : 'badge-warning'}`}>
                  {quiz.published ? 'Publié' : 'Brouillon'}
                </span>
                {quiz.level && (
                  <span className={`badge badge-${quiz.level === 'BEGINNER' ? 'beginner' : quiz.level === 'INTERMEDIATE' ? 'intermediate' : 'advanced'}`}>
                    {getLevelLabel(quiz.level)}
                  </span>
                )}
              </div>
              <div className="course-card-body" onClick={() => navigate(`/quiz/${quiz.id}`)}>
                <h3>{quiz.title}</h3>
                <p>{quiz.description}</p>
                <p style={{ fontSize: '0.8rem', color: 'var(--accent-400)', marginTop: 8 }}>
                  📝 {quiz.questionsCount || 0} questions
                </p>
              </div>
              <div className="course-card-footer">
                <span className="course-meta">
                  {quiz.courseName ? `📚 ${quiz.courseName}` : 'Pas de cours associé'}
                </span>
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                  <button
                    className="btn btn-secondary btn-sm"
                    onClick={(e) => { e.stopPropagation(); handleTogglePublishQuiz(quiz.id); }}
                    title={quiz.published ? "Passer en brouillon" : "Publier ce quiz"}
                  >
                    {quiz.published ? <FiEyeOff size={14} /> : <FiEye size={14} />}
                  </button>
                  <button
                    className="btn btn-danger btn-sm"
                    onClick={() => handleDeleteQuiz(quiz.id)}
                    title="Supprimer ce quiz"
                  >
                    <FiTrash2 size={14} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: 40 }}>
          <FiCheckSquare size={40} color="var(--text-muted)" style={{ marginBottom: 12 }} />
          <p style={{ color: 'var(--text-secondary)', marginBottom: 12 }}>
            Aucun quiz créé
          </p>
          <Link to="/teacher/create-quiz" className="btn btn-primary btn-sm">
            Créer un quiz
          </Link>
        </div>
      )}

      {/* ====== QUIZ RESULTS SECTION ====== */}
      <div style={{ marginTop: 40, display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h2 style={{ fontSize: '1.3rem' }}>
          <FiAward style={{ verticalAlign: 'middle', marginRight: 8 }} />
          Résultats des étudiants
        </h2>
      </div>

      {quizResults.length > 0 ? (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.03)' }}>
                <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Étudiant</th>
                <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Quiz</th>
                <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Score</th>
                <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>%</th>
                <th style={{ padding: '14px 20px', textAlign: 'right', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Date</th>
              </tr>
            </thead>
            <tbody>
              {quizResults.map((result, index) => (
                <tr key={index} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)', transition: 'background 0.2s' }}
                  onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.03)'}
                  onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                >
                  <td style={{ padding: '14px 20px' }}>
                    <div style={{ fontWeight: 600, fontSize: '0.9rem' }}>{result.studentName}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{result.studentEmail}</div>
                  </td>
                  <td style={{ padding: '14px 20px', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>📋 {result.quizTitle}</td>
                  <td style={{ padding: '14px 20px', textAlign: 'center', fontWeight: 700 }}>
                    {result.score} / {result.totalQuestions}
                  </td>
                  <td style={{ padding: '14px 20px', textAlign: 'center' }}>
                    <span style={{
                      padding: '4px 12px',
                      borderRadius: 100,
                      fontSize: '0.8rem',
                      fontWeight: 700,
                      background: result.percentage >= 70 ? 'rgba(34,197,94,0.15)' : result.percentage >= 40 ? 'rgba(245,158,11,0.15)' : 'rgba(239,68,68,0.15)',
                      color: result.percentage >= 70 ? '#4ade80' : result.percentage >= 40 ? '#fbbf24' : '#f87171'
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
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: 40 }}>
          <FiAward size={40} color="var(--text-muted)" style={{ marginBottom: 12 }} />
          <p style={{ color: 'var(--text-secondary)' }}>
            Aucun étudiant n'a encore répondu à vos quiz
          </p>
        </div>
      )}

      {/* STUDENT MODAL */}
      {showStudentsModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          background: 'rgba(0,0,0,0.7)', backdropFilter: 'blur(4px)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 9999
        }}>
          <div className="card animate-in" style={{ width: '100%', maxWidth: 700, padding: 30, maxHeight: '90vh', overflowY: 'auto' }}>
            <h2 style={{ fontSize: '1.4rem', marginBottom: 8, display: 'flex', justifyContent: 'space-between' }}>
              <span>👨‍🎓 Étudiants : {selectedCourseName}</span>
              <button 
                onClick={() => setShowStudentsModal(false)}
                className="btn btn-secondary btn-sm"
              >
                Fermer
              </button>
            </h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: 20 }}>
              {selectedCourseStudents.length} étudiant(s) inscrit(s)
            </p>

            {selectedCourseStudents.length > 0 ? (
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                  <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)' }}>
                    <th style={{ padding: 12, textAlign: 'left', fontSize: '0.85rem' }}>Étudiant</th>
                    <th style={{ padding: 12, textAlign: 'center', fontSize: '0.85rem' }}>Progression</th>
                    <th style={{ padding: 12, textAlign: 'right', fontSize: '0.85rem' }}>Date d'inscription</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedCourseStudents.map((s, idx) => (
                    <tr key={idx} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                      <td style={{ padding: 12 }}>
                        <div style={{ fontWeight: 600, fontSize: '0.95rem' }}>{s.fullName}</div>
                        <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>{s.email}</div>
                      </td>
                      <td style={{ padding: 12, textAlign: 'center' }}>
                        <div style={{ display: 'flex', alignItems: 'center', gap: 10, justifyContent: 'center' }}>
                          <div style={{ width: 100, height: 8, background: 'rgba(255,255,255,0.1)', borderRadius: 4, overflow: 'hidden' }}>
                            <div style={{ height: '100%', width: `${s.progressPercentage || 0}%`, background: s.completed ? '#4ade80' : 'var(--primary-500)' }} />
                          </div>
                          <span style={{ fontSize: '0.8rem', fontWeight: 600, color: s.completed ? '#4ade80' : 'var(--text-primary)' }}>
                            {Math.round(s.progressPercentage || 0)}%
                          </span>
                        </div>
                      </td>
                      <td style={{ padding: 12, textAlign: 'right', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                        {s.enrolledAt ? new Date(s.enrolledAt).toLocaleDateString('fr-FR') : '-'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div style={{ textAlign: 'center', padding: 40, background: 'rgba(255,255,255,0.02)', borderRadius: 12 }}>
                <p style={{ color: 'var(--text-secondary)' }}>Aucun étudiant n'est encore inscrit à ce cours.</p>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
