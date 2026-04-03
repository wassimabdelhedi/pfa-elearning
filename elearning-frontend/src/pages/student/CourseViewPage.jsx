import { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { getCourseById, downloadCourse } from '../../api/courseApi';
import { enrollInCourse } from '../../api/userApi';
import { FiArrowLeft, FiUser, FiDownload, FiBookOpen, FiFileText, FiPlay } from 'react-icons/fi';

const VIDEO_EXTENSIONS = ['.mp4', '.avi', '.mov', '.webm', '.mkv'];

function isVideoFile(fileName) {
  if (!fileName) return false;
  const ext = fileName.toLowerCase().substring(fileName.lastIndexOf('.'));
  return VIDEO_EXTENSIONS.includes(ext);
}

export default function CourseViewPage() {
  const { id } = useParams();
  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [enrollMsg, setEnrollMsg] = useState('');
  const [activeTab, setActiveTab] = useState('content');

  useEffect(() => {
    loadCourse();
  }, [id]);

  const loadCourse = async () => {
    try {
      const res = await getCourseById(id);
      setCourse(res.data);
      // Auto-select video tab if course has video and no text content
      if (res.data && isVideoFile(res.data.originalFileName) && !res.data.content) {
        setActiveTab('video');
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnroll = async () => {
    try {
      await enrollInCourse(course.id);
      setEnrollMsg('✅ Inscription réussie !');
      setTimeout(() => setEnrollMsg(''), 3000);
    } catch (err) {
      setEnrollMsg(err.response?.data?.message || 'Erreur d\'inscription');
      setTimeout(() => setEnrollMsg(''), 3000);
    }
  };

  const handleDownload = async () => {
    if (course.filePath) {
      try {
        const response = await downloadCourse(course.id);
        const url = window.URL.createObjectURL(new Blob([response.data]));
        const link = document.createElement('a');
        link.href = url;
        const fileName = course.originalFileName || `cours_${course.id}`;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        link.parentNode.removeChild(link);
      } catch (err) {
        console.error('Erreur lors du téléchargement', err);
        setEnrollMsg('Erreur lors du téléchargement');
        setTimeout(() => setEnrollMsg(''), 3000);
      }
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

  const hasVideo = course && isVideoFile(course.originalFileName);

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  if (!course) {
    return (
      <div className="page" style={{ textAlign: 'center', padding: 60 }}>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>Cours introuvable</p>
        <Link to="/courses" className="btn btn-primary" style={{ marginTop: 16 }}>
          <FiArrowLeft size={16} /> Retour aux cours
        </Link>
      </div>
    );
  }

  return (
    <div className="page">
      <Link to="/courses" className="btn btn-secondary btn-sm" style={{ marginBottom: 24 }}>
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
              {hasVideo && <span className="badge badge-success">🎬 Vidéo</span>}
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
            <button className="btn btn-primary" onClick={handleEnroll}>
              S'inscrire au cours
            </button>
            {course.filePath && (
              <button className="btn btn-secondary" onClick={handleDownload}>
                <FiDownload size={16} /> Télécharger
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: 4, marginBottom: 24 }}>
        <button
          className={`btn btn-sm ${activeTab === 'content' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveTab('content')}
        >
          <FiFileText size={14} /> Contenu
        </button>
        {hasVideo && (
          <button
            className={`btn btn-sm ${activeTab === 'video' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('video')}
          >
            <FiPlay size={14} /> Vidéo
          </button>
        )}
        {course.originalFileName && !hasVideo && (
          <button
            className={`btn btn-sm ${activeTab === 'document' ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => setActiveTab('document')}
          >
            <FiBookOpen size={14} /> Document
          </button>
        )}
      </div>

      {/* Tab Content */}
      {activeTab === 'content' && (
        <div className="card" style={{ padding: 32 }}>
          {course.content ? (
            <div style={{ color: 'var(--text-primary)', lineHeight: 1.8, whiteSpace: 'pre-wrap', fontSize: '0.95rem' }}>
              {course.content}
            </div>
          ) : (
            <div style={{ textAlign: 'center', padding: 40, color: 'var(--text-muted)' }}>
              <FiFileText size={40} style={{ marginBottom: 12, opacity: 0.4 }} />
              <p>Aucun contenu textuel disponible pour ce cours</p>
            </div>
          )}
        </div>
      )}

      {activeTab === 'video' && hasVideo && (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <video
            controls
            style={{ width: '100%', maxHeight: '70vh', background: '#000' }}
            src={`http://localhost:8081/api/courses/${course.id}/stream`}
          >
            Votre navigateur ne supporte pas la lecture vidéo.
          </video>
          <div style={{ padding: '16px 24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
              🎬 {course.originalFileName}
            </span>
            <button className="btn btn-secondary btn-sm" onClick={handleDownload}>
              <FiDownload size={14} /> Télécharger la vidéo
            </button>
          </div>
        </div>
      )}

      {activeTab === 'document' && course.originalFileName && !hasVideo && (
        <div className="card" style={{ padding: 32, textAlign: 'center' }}>
          <FiFileText size={48} color="var(--primary-400)" style={{ marginBottom: 16 }} />
          <h3 style={{ marginBottom: 8 }}>{course.originalFileName}</h3>
          <p style={{ color: 'var(--text-secondary)', marginBottom: 20 }}>
            Téléchargez le document pour le consulter
          </p>
          <button className="btn btn-primary" onClick={handleDownload}>
            <FiDownload size={16} /> Télécharger
          </button>
        </div>
      )}
    </div>
  );
}
