import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getCourseById, getChapters, addChapter, deleteChapter } from '../../api/courseApi';
import { FiPlus, FiTrash2, FiArrowLeft, FiFile, FiVideo, FiLink, FiFileText, FiCheck } from 'react-icons/fi';

const SUPPORT_TYPES = [
  { value: 'TEXT', label: '📝 Texte', icon: <FiFileText /> },
  { value: 'PDF', label: '📄 PDF / Document', icon: <FiFile /> },
  { value: 'VIDEO', label: '🎬 Vidéo', icon: <FiVideo /> },
  { value: 'LINK', label: '🔗 Lien externe', icon: <FiLink /> },
];

export default function ManageCourseChapters() {
  const { courseId } = useParams();
  const navigate = useNavigate();
  const [course, setCourse] = useState(null);
  const [chapters, setChapters] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [formLoading, setFormLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const [newChapter, setNewChapter] = useState({
    title: '', description: '', supportType: 'TEXT', supportLink: '', content: ''
  });
  const [chapterFile, setChapterFile] = useState(null);

  useEffect(() => { loadData(); }, [courseId]);

  const loadData = async () => {
    try {
      const [courseRes, chaptersRes] = await Promise.all([
        getCourseById(courseId),
        getChapters(courseId)
      ]);
      setCourse(courseRes.data);
      setChapters(chaptersRes.data);
    } catch (err) {
      console.error(err);
      setError('Erreur lors du chargement');
    } finally {
      setLoading(false);
    }
  };

  const handleAddChapter = async (e) => {
    e.preventDefault();
    setFormLoading(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('title', newChapter.title);
      if (newChapter.description) formData.append('description', newChapter.description);
      formData.append('supportType', newChapter.supportType);
      if (newChapter.supportLink) formData.append('supportLink', newChapter.supportLink);
      if (newChapter.content) formData.append('content', newChapter.content);
      if (chapterFile) formData.append('file', chapterFile);

      await addChapter(courseId, formData);

      // Reset form
      setNewChapter({ title: '', description: '', supportType: 'TEXT', supportLink: '', content: '' });
      setChapterFile(null);
      setShowForm(false);
      setSuccess('✅ Chapitre ajouté avec succès !');
      setTimeout(() => setSuccess(''), 3000);

      // Reload chapters
      const chaptersRes = await getChapters(courseId);
      setChapters(chaptersRes.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de l\'ajout du chapitre');
    } finally {
      setFormLoading(false);
    }
  };

  const handleDeleteChapter = async (chapterId) => {
    if (!window.confirm('Supprimer ce chapitre ?')) return;
    try {
      await deleteChapter(chapterId);
      setChapters(chapters.filter(c => c.id !== chapterId));
      setSuccess('✅ Chapitre supprimé');
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError('Erreur lors de la suppression');
    }
  };

  const getSupportIcon = (type) => {
    const icons = { TEXT: '📝', PDF: '📄', DOCUMENT: '📄', VIDEO: '🎬', LINK: '🔗' };
    return icons[type] || '📄';
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page" style={{ maxWidth: 850, margin: '0 auto' }}>
      <button className="btn btn-secondary btn-sm" onClick={() => navigate('/teacher/dashboard')}
        style={{ marginBottom: 24 }}>
        <FiArrowLeft size={14} /> Retour au tableau de bord
      </button>

      <div className="page-header">
        <h1>📚 Chapitres : {course?.title}</h1>
        <p>Ajoutez des chapitres avec différents supports (vidéo, PDF, liens, texte)</p>
      </div>

      {error && <div className="error-message" style={{ marginBottom: 16 }}>{error}</div>}
      {success && (
        <div style={{
          padding: '12px 16px', background: 'rgba(34,197,94,0.1)',
          border: '1px solid rgba(34,197,94,0.2)', borderRadius: 8,
          color: '#4ade80', textAlign: 'center', marginBottom: 16
        }}>
          {success}
        </div>
      )}

      {/* Existing Chapters */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: 12, marginBottom: 24 }}>
        {chapters.length === 0 && (
          <div className="card" style={{ padding: 40, textAlign: 'center', color: 'var(--text-muted)' }}>
            Aucun chapitre. Cliquez sur "Ajouter un chapitre" pour commencer.
          </div>
        )}
        {chapters.map((ch, idx) => (
          <div key={ch.id} className="card animate-in" style={{
            padding: '16px 24px', display: 'flex', alignItems: 'center', gap: 16
          }}>
            <div style={{
              width: 36, height: 36, borderRadius: 8,
              background: 'var(--primary-500)', color: '#fff',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontWeight: 'bold', fontSize: '0.9rem', flexShrink: 0
            }}>
              {idx + 1}
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontWeight: 600, fontSize: '1rem', color: 'var(--text-primary)' }}>
                {getSupportIcon(ch.supportType)} {ch.title}
              </div>
              {ch.description && (
                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: 4 }}>
                  {ch.description.substring(0, 80)}{ch.description.length > 80 ? '...' : ''}
                </div>
              )}
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: 4 }}>
                Type : {ch.supportType}
                {ch.originalFileName && ` · Fichier : ${ch.originalFileName}`}
                {ch.supportLink && ` · Lien : ${ch.supportLink.substring(0, 40)}...`}
              </div>
            </div>
            <button className="btn btn-secondary btn-sm" onClick={() => handleDeleteChapter(ch.id)}
              style={{ color: '#ef4444' }}>
              <FiTrash2 size={14} />
            </button>
          </div>
        ))}
      </div>

      {/* Add Chapter Button */}
      {!showForm && (
        <button className="btn btn-primary" onClick={() => setShowForm(true)}
          style={{ width: '100%', padding: '14px', fontSize: '1rem', marginBottom: 24 }}>
          <FiPlus size={18} /> Ajouter un chapitre
        </button>
      )}

      {/* Add Chapter Form */}
      {showForm && (
        <div className="card" style={{ padding: 32, marginBottom: 24 }}>
          <h3 style={{ marginBottom: 20 }}>➕ Nouveau chapitre</h3>
          <form onSubmit={handleAddChapter} style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            <div className="form-group">
              <label>Titre du chapitre *</label>
              <input className="form-input" placeholder="Ex: Introduction aux variables"
                value={newChapter.title} onChange={(e) => setNewChapter({...newChapter, title: e.target.value})}
                required />
            </div>

            <div className="form-group">
              <label>Description</label>
              <textarea className="form-input" rows={2} placeholder="Brève description..."
                value={newChapter.description}
                onChange={(e) => setNewChapter({...newChapter, description: e.target.value})} />
            </div>

            <div className="form-group">
              <label>Type de support</label>
              <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                {SUPPORT_TYPES.map(st => (
                  <button key={st.value} type="button"
                    className={`btn btn-sm ${newChapter.supportType === st.value ? 'btn-primary' : 'btn-secondary'}`}
                    onClick={() => setNewChapter({...newChapter, supportType: st.value})}
                    style={{ display: 'flex', alignItems: 'center', gap: 6 }}>
                    {st.label}
                  </button>
                ))}
              </div>
            </div>

            {/* Conditional fields based on support type */}
            {newChapter.supportType === 'TEXT' && (
              <div className="form-group">
                <label>Contenu textuel</label>
                <textarea className="form-input" rows={8} placeholder="Écrivez le contenu du chapitre ici..."
                  value={newChapter.content}
                  onChange={(e) => setNewChapter({...newChapter, content: e.target.value})} />
              </div>
            )}

            {newChapter.supportType === 'LINK' && (
              <div className="form-group">
                <label>Lien externe (YouTube, site web, etc.)</label>
                <input className="form-input" type="url" placeholder="https://www.youtube.com/watch?v=..."
                  value={newChapter.supportLink}
                  onChange={(e) => setNewChapter({...newChapter, supportLink: e.target.value})} />
              </div>
            )}

            {(newChapter.supportType === 'PDF' || newChapter.supportType === 'VIDEO' || newChapter.supportType === 'DOCUMENT') && (
              <div className="form-group">
                <label>
                  {newChapter.supportType === 'VIDEO' ? '🎬 Fichier vidéo (MP4, AVI, MOV...)' : '📄 Fichier (PDF, DOCX, PPTX...)'}
                </label>
                <input type="file" className="form-input"
                  accept={newChapter.supportType === 'VIDEO'
                    ? '.mp4,.avi,.mov,.webm,.mkv'
                    : '.pdf,.docx,.pptx,.txt'}
                  onChange={(e) => setChapterFile(e.target.files[0])}
                  style={{ padding: '12px' }} />
                {chapterFile && (
                  <p style={{ marginTop: 8, color: 'var(--primary-500)', fontSize: '0.9rem' }}>
                    ✅ {chapterFile.name} ({(chapterFile.size / (1024 * 1024)).toFixed(1)} MB)
                  </p>
                )}
              </div>
            )}

            <div style={{ display: 'flex', gap: 12 }}>
              <button type="submit" className="btn btn-primary" disabled={formLoading} style={{ flex: 1 }}>
                <FiCheck size={16} /> {formLoading ? 'Ajout en cours...' : 'Ajouter le chapitre'}
              </button>
              <button type="button" className="btn btn-secondary" onClick={() => setShowForm(false)}>
                Annuler
              </button>
            </div>
          </form>
        </div>
      )}

      {/* Done button */}
      {chapters.length > 0 && (
        <button className="btn btn-success" onClick={() => navigate('/teacher/dashboard')}
          style={{ width: '100%', padding: '14px', fontSize: '1rem' }}>
          <FiCheck size={18} /> Terminé — Retour au tableau de bord
        </button>
      )}
    </div>
  );
}
