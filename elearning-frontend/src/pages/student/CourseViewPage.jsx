import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getCourseById, getChapters, downloadChapterFile, markChapterComplete, getCourseChapterProgress, downloadCourse } from '../../api/courseApi';
import { enrollInCourse, updateProgressByCourse } from '../../api/userApi';
import { FiArrowLeft, FiUser, FiDownload, FiBookOpen, FiFileText, FiPlay, FiCheckCircle, FiLock, FiLink, FiChevronRight } from 'react-icons/fi';

const VIDEO_EXTENSIONS = ['.mp4', '.avi', '.mov', '.webm', '.mkv'];

function isVideoFile(fileName) {
  if (!fileName) return false;
  const ext = fileName.toLowerCase().substring(fileName.lastIndexOf('.'));
  return VIDEO_EXTENSIONS.includes(ext);
}

export default function CourseViewPage() {
  const { id } = useParams();
  const { user } = useAuth();
  const [course, setCourse] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [enrollMsg, setEnrollMsg] = useState('');

  // Progress
  const [isEnrolled, setIsEnrolled] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);
  const [overallProgress, setOverallProgress] = useState(0);
  const [completedChapterIds, setCompletedChapterIds] = useState(new Set());

  // Active chapter
  const [activeChapterId, setActiveChapterId] = useState(null);

  useEffect(() => { loadAll(); }, [id]);

  const loadAll = async () => {
    try {
      const [courseRes, chaptersRes] = await Promise.all([
        getCourseById(id),
        getChapters(id)
      ]);
      setCourse(courseRes.data);
      setChapters(chaptersRes.data);

      // Load progress
      try {
        const progressRes = await getCourseChapterProgress(id);
        if (progressRes.data.enrolled) {
          setIsEnrolled(true);
          setIsCompleted(progressRes.data.courseCompleted || false);
          setOverallProgress(progressRes.data.overallProgress || 0);
          const completed = new Set(
            (progressRes.data.chapters || [])
              .filter(ch => ch.completed)
              .map(ch => ch.chapterId)
          );
          setCompletedChapterIds(completed);

          // Auto-select first incomplete chapter
          if (chaptersRes.data.length > 0) {
            const firstIncomplete = chaptersRes.data.find(ch => !completed.has(ch.id));
            setActiveChapterId(firstIncomplete ? firstIncomplete.id : chaptersRes.data[0].id);
          }
        } else if (chaptersRes.data.length > 0) {
          setActiveChapterId(chaptersRes.data[0].id);
        }
      } catch (e) {
        if (chaptersRes.data.length > 0) setActiveChapterId(chaptersRes.data[0].id);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async () => {
    try {
      await enrollInCourse(id);
      setIsEnrolled(true);
      setEnrollMsg('✅ Inscription réussie !');
      setTimeout(() => setEnrollMsg(''), 3000);
      loadAll(); // Reload to get progress data
    } catch (err) {
      setEnrollMsg(err.response?.data?.message || "Erreur d'inscription");
      setTimeout(() => setEnrollMsg(''), 3000);
    }
  };

  const handleMarkChapterComplete = async (chapterId) => {
    try {
      const res = await markChapterComplete(chapterId);
      const data = res.data;
      setCompletedChapterIds(prev => new Set([...prev, chapterId]));
      setOverallProgress(data.courseProgress || 0);
      if (data.courseCompleted) {
        setIsCompleted(true);
        setEnrollMsg('🎉 Félicitations ! Vous avez terminé ce cours ! Quiz et exercices débloqués !');
        setTimeout(() => setEnrollMsg(''), 5000);
      } else {
        setEnrollMsg('✅ Chapitre terminé !');
        setTimeout(() => setEnrollMsg(''), 3000);
      }
    } catch (err) {
      setEnrollMsg('Erreur de mise à jour');
      setTimeout(() => setEnrollMsg(''), 3000);
    }
  };

  const handleDownloadChapter = async (chapterId, fileName) => {
    try {
      const response = await downloadChapterFile(chapterId);
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', fileName || `chapitre_${chapterId}`);
      document.body.appendChild(link);
      link.click();
      link.parentNode.removeChild(link);
    } catch (err) {
      console.error('Download error', err);
    }
  };

  const activeChapter = chapters.find(ch => ch.id === activeChapterId);

  const getSupportIcon = (type) => {
    switch(type) {
      case 'VIDEO': return <FiPlay size={14} />;
      case 'PDF': case 'DOCUMENT': return <FiFileText size={14} />;
      case 'LINK': return <FiLink size={14} />;
      default: return <FiFileText size={14} />;
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

  if (loading) return <div className="loading-container"><div className="spinner"></div></div>;

  if (!course) {
    return (
      <div className="page" style={{ textAlign: 'center', padding: 60 }}>
        <p style={{ color: 'var(--text-secondary)' }}>Cours introuvable</p>
        <Link to="/courses" className="btn btn-primary" style={{ marginTop: 16 }}>
          <FiArrowLeft size={16} /> Retour aux cours
        </Link>
      </div>
    );
  }

  return (
    <>
      {/* Sticky Progress Bar */}
      {isEnrolled && (
        <div style={{
          position: 'sticky', top: 0, zIndex: 1000,
          background: 'var(--bg-card, #1a1a2e)', padding: '12px 24px',
          borderBottom: '1px solid var(--border)',
          display: 'flex', alignItems: 'center', gap: 16,
          boxShadow: '0 4px 12px rgba(0,0,0,0.1)'
        }}>
          <span style={{ fontWeight: 'bold', color: 'var(--text-secondary)', fontSize: '0.9rem', whiteSpace: 'nowrap' }}>
            Ma progression
          </span>
          <div style={{ flex: 1, background: 'var(--bg-page)', height: 10, borderRadius: 5, overflow: 'hidden' }}>
            <div style={{
              width: `${overallProgress}%`, background: 'var(--primary-500)',
              height: '100%', transition: 'width 0.5s ease-out'
            }}></div>
          </div>
          <span style={{ fontWeight: 'bold', color: 'var(--primary-500)', minWidth: 45, fontSize: '1rem' }}>
            {overallProgress}%
          </span>
          {isCompleted && (
            <span style={{ color: '#10b981', display: 'flex', alignItems: 'center', gap: 4, fontSize: '0.9rem', fontWeight: 'bold' }}>
              <FiCheckCircle /> Terminé
            </span>
          )}
        </div>
      )}
      <div className="page">
        <Link to="/courses" className="btn btn-secondary btn-sm" style={{ marginBottom: 24, display: 'inline-flex' }}>
          <FiArrowLeft size={14} /> Retour aux cours
        </Link>

        {enrollMsg && (
          <div style={{
            padding: '12px 16px', background: 'rgba(34,197,94,0.1)',
            border: '1px solid rgba(34,197,94,0.2)', borderRadius: 8,
            color: '#4ade80', textAlign: 'center', marginBottom: 20
          }}>
            {enrollMsg}
          </div>
        )}

        {/* Course Header */}
        <div className="card" style={{ padding: 32, marginBottom: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: 16 }}>
            <div style={{ flex: 1 }}>
              <div style={{ display: 'flex', gap: 8, marginBottom: 12, flexWrap: 'wrap' }}>
                {course.categoryName && <span className="badge badge-primary">{course.categoryName}</span>}
                {course.level && <span className={`badge ${getLevelClass(course.level)}`}>{getLevelLabel(course.level)}</span>}
                <span className="badge badge-success">{chapters.length} chapitre{chapters.length !== 1 ? 's' : ''}</span>
              </div>
              <h1 style={{ fontSize: '1.8rem', fontWeight: 800, marginBottom: 12, color: 'var(--text-primary)' }}>
                {course.title}
              </h1>
              {course.description && (
                <p style={{ color: 'var(--text-secondary)', fontSize: '1rem', lineHeight: 1.6, marginBottom: 16 }}>
                  {course.description}
                </p>
              )}
              <div style={{ display: 'flex', gap: 20, flexWrap: 'wrap', alignItems: 'center' }}>
                <span className="course-meta" style={{ fontSize: '0.9rem' }}>
                  <FiUser size={15} /> {course.teacherName}
                </span>
                <span className="course-meta" style={{ fontSize: '0.9rem' }}>
                  ⭐ {course.averageRating ? course.averageRating.toFixed(1) : 'N/A'}
                </span>
                <span className="course-meta" style={{ fontSize: '0.9rem' }}>
                  <FiBookOpen size={15} /> {course.enrollmentCount} inscrits
                </span>
              </div>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
              {!isEnrolled && user?.role !== 'TEACHER' && (
                <button className="btn btn-primary" onClick={handleEnroll}>
                  S'inscrire au cours
                </button>
              )}
            </div>
          </div>
        </div>

<<<<<<< HEAD
        {/* Main content: Sidebar + Chapter Viewer */}
        {(isEnrolled || user?.role === 'TEACHER' || user?.role === 'ADMIN') && chapters.length > 0 && (
          <div style={{ display: 'flex', gap: 24, alignItems: 'flex-start' }}>
            {/* Chapter Sidebar */}
            <div style={{ width: 280, flexShrink: 0 }}>
              <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
                <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', fontWeight: 'bold', fontSize: '0.95rem' }}>
                  📚 Chapitres ({completedChapterIds.size}/{chapters.length})
                </div>
                {chapters.map((ch, idx) => {
                  const done = completedChapterIds.has(ch.id);
                  const isActive = ch.id === activeChapterId;
                  return (
                    <div key={ch.id}
                      onClick={() => setActiveChapterId(ch.id)}
                      style={{
                        padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12,
                        cursor: 'pointer', borderBottom: '1px solid var(--border)',
                        background: isActive ? 'rgba(var(--primary-rgb, 99,102,241), 0.1)' : 'transparent',
                        borderLeft: isActive ? '3px solid var(--primary-500)' : '3px solid transparent',
                        transition: 'all 0.2s ease'
                      }}>
                      <div style={{
                        width: 28, height: 28, borderRadius: '50%', flexShrink: 0,
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: '0.75rem', fontWeight: 'bold',
                        background: done ? '#10b981' : 'var(--bg-page)',
                        color: done ? '#fff' : 'var(--text-secondary)',
                        border: done ? 'none' : '2px solid var(--border)'
                      }}>
                        {done ? <FiCheckCircle size={14} /> : idx + 1}
                      </div>
                      <div style={{ flex: 1, overflow: 'hidden' }}>
                        <div style={{
                          fontSize: '0.85rem', fontWeight: isActive ? 600 : 400,
                          color: done ? '#10b981' : 'var(--text-primary)',
                          whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis'
                        }}>
                          {ch.title}
                        </div>
                        <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: 4, marginTop: 2 }}>
                          {getSupportIcon(ch.supportType)} {ch.supportType}
                        </div>
                      </div>
                      <FiChevronRight size={14} style={{ color: 'var(--text-muted)', flexShrink: 0 }} />
                    </div>
                  );
                })}
              </div>

              {/* Quiz & Exercise Links — locked until course complete */}
              <div className="card" style={{ padding: 0, overflow: 'hidden', marginTop: 16 }}>
                <div style={{ padding: '16px 20px', borderBottom: '1px solid var(--border)', fontWeight: 'bold', fontSize: '0.95rem' }}>
                  📝 Évaluations
                </div>
                <Link to={isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN' ? `/course/${course.id}/quizzes` : '#'}
                  style={{
                    padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12,
                    borderBottom: '1px solid var(--border)', textDecoration: 'none',
                    color: (isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? 'var(--text-primary)' : 'var(--text-muted)',
                    cursor: (isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? 'pointer' : 'not-allowed',
                    opacity: (isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? 1 : 0.5
                  }}
                  onClick={(e) => { if (!isCompleted && user?.role !== 'TEACHER' && user?.role !== 'ADMIN') e.preventDefault(); }}>
                  {(isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? <FiCheckCircle size={16} color="#10b981" /> : <FiLock size={16} />}
                  <span style={{ fontSize: '0.9rem' }}>Quiz</span>
                </Link>
                <Link to={isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN' ? `/course/${course.id}/exercises` : '#'}
                  style={{
                    padding: '12px 20px', display: 'flex', alignItems: 'center', gap: 12,
                    textDecoration: 'none',
                    color: (isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? 'var(--text-primary)' : 'var(--text-muted)',
                    cursor: (isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? 'pointer' : 'not-allowed',
                    opacity: (isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? 1 : 0.5
                  }}
                  onClick={(e) => { if (!isCompleted && user?.role !== 'TEACHER' && user?.role !== 'ADMIN') e.preventDefault(); }}>
                  {(isCompleted || user?.role === 'TEACHER' || user?.role === 'ADMIN') ? <FiCheckCircle size={16} color="#10b981" /> : <FiLock size={16} />}
                  <span style={{ fontSize: '0.9rem' }}>Exercices</span>
                </Link>
                {(!isCompleted && user?.role !== 'TEACHER' && user?.role !== 'ADMIN') && (
                  <div style={{ padding: '10px 20px', fontSize: '0.75rem', color: 'var(--text-muted)', background: 'var(--bg-page)' }}>
                    🔒 Terminez tous les chapitres pour débloquer
                  </div>
                )}
              </div>
            </div>

            {/* Chapter Content Viewer */}
            <div style={{ flex: 1, minWidth: 0 }}>
              {activeChapter && (
                <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
                  {/* Chapter Header */}
                  <div style={{
                    padding: '20px 28px', borderBottom: '1px solid var(--border)',
                    display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 12
                  }}>
                    <div>
                      <h2 style={{ fontSize: '1.3rem', fontWeight: 700, marginBottom: 4 }}>{activeChapter.title}</h2>
                      {activeChapter.description && (
                        <p style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>{activeChapter.description}</p>
                      )}
                    </div>
                    {!completedChapterIds.has(activeChapter.id) && user?.role !== 'TEACHER' && (
                      <button className="btn btn-success btn-sm" onClick={() => handleMarkChapterComplete(activeChapter.id)}>
                        <FiCheckCircle size={14} /> Marquer comme terminé
                      </button>
                    )}
                    {completedChapterIds.has(activeChapter.id) && (
                      <span style={{ color: '#10b981', fontWeight: 'bold', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: 4 }}>
                        <FiCheckCircle /> Terminé
                      </span>
                    )}
                  </div>

                  {/* Chapter Body */}
                  <div style={{ padding: 28 }}>
                    {/* TEXT content */}
                    {(activeChapter.supportType === 'TEXT' || activeChapter.content) && activeChapter.content && (
                      <div style={{ color: 'var(--text-primary)', lineHeight: 1.8, whiteSpace: 'pre-wrap', fontSize: '0.95rem' }}>
                        {activeChapter.content}
                      </div>
                    )}

                    {/* VIDEO */}
                    {activeChapter.supportType === 'VIDEO' && activeChapter.filePath && (
                      <div>
                        <video controls style={{ width: '100%', maxHeight: '60vh', background: '#000', borderRadius: 8 }}
                          src={`http://localhost:8081/api/chapters/${activeChapter.id}/stream`}>
                          Votre navigateur ne supporte pas la lecture vidéo.
                        </video>
                        <div style={{ marginTop: 12, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                          <span style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>
                            🎬 {activeChapter.originalFileName}
                          </span>
                          <button className="btn btn-secondary btn-sm"
                            onClick={() => handleDownloadChapter(activeChapter.id, activeChapter.originalFileName)}>
                            <FiDownload size={14} /> Télécharger
                          </button>
                        </div>
                      </div>
                    )}

                    {/* PDF / Document */}
                    {(activeChapter.supportType === 'PDF' || activeChapter.supportType === 'DOCUMENT') && activeChapter.filePath && (
                      <div style={{ textAlign: 'center', padding: 40 }}>
                        <FiFileText size={48} color="var(--primary-400)" style={{ marginBottom: 16 }} />
                        <h3 style={{ marginBottom: 8 }}>{activeChapter.originalFileName}</h3>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: 20 }}>Téléchargez le document pour le consulter</p>
                        <button className="btn btn-primary"
                          onClick={() => handleDownloadChapter(activeChapter.id, activeChapter.originalFileName)}>
                          <FiDownload size={16} /> Télécharger
                        </button>
                      </div>
                    )}

                    {/* LINK */}
                    {activeChapter.supportType === 'LINK' && activeChapter.supportLink && (
                      <div style={{ textAlign: 'center', padding: 40 }}>
                        <FiLink size={48} color="var(--primary-400)" style={{ marginBottom: 16 }} />
                        <h3 style={{ marginBottom: 8 }}>Ressource externe</h3>
                        <p style={{ color: 'var(--text-secondary)', marginBottom: 20, wordBreak: 'break-all' }}>
                          {activeChapter.supportLink}
                        </p>
                        <a href={activeChapter.supportLink} target="_blank" rel="noopener noreferrer"
                          className="btn btn-primary">
                          <FiLink size={16} /> Ouvrir le lien
                        </a>
                      </div>
                    )}

                    {/* No content fallback */}
                    {!activeChapter.content && !activeChapter.filePath && !activeChapter.supportLink && (
                      <div style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
                        <FiFileText size={40} style={{ marginBottom: 12, opacity: 0.4 }} />
                        <p>Aucun contenu disponible pour ce chapitre</p>
                      </div>
                    )}
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Not enrolled message */}
        {!isEnrolled && user?.role !== 'TEACHER' && (
          <div className="card" style={{ padding: 40, textAlign: 'center', color: 'var(--text-secondary)' }}>
            <FiBookOpen size={48} style={{ marginBottom: 16, opacity: 0.5 }} />
            <h3>Inscrivez-vous pour visualiser le contenu</h3>
            <p>Ce cours contient {chapters.length} chapitre{chapters.length !== 1 ? 's' : ''}. Inscrivez-vous pour commencer votre apprentissage.</p>
          </div>
        )}

        {/* No chapters */}
        {(isEnrolled || user?.role === 'TEACHER' || user?.role === 'ADMIN') && chapters.length === 0 && (
          <div className="card" style={{ padding: 40, textAlign: 'center', color: 'var(--text-muted)' }}>
            <FiBookOpen size={40} style={{ marginBottom: 12, opacity: 0.4 }} />
            <p>Ce cours ne contient aucun chapitre pour le moment.</p>
          </div>
        )}
        )}
      </div>
    </>
      </div>
    </>
  );
}
