import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createCourse } from '../../api/courseApi';

export default function CreateCourse() {
  const [form, setForm] = useState({
    title: '', description: '', categoryId: '', level: 'BEGINNER', published: false
  });
  const [file, setFile] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm({ ...form, [e.target.name]: value });
  };

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      const allowedTypes = ['.pdf', '.docx', '.pptx', '.txt', '.mp4', '.avi', '.mov', '.webm', '.mkv'];
      const ext = '.' + selectedFile.name.split('.').pop().toLowerCase();
      if (!allowedTypes.includes(ext)) {
        setError(`Type de fichier non supporté: ${ext}. Types acceptés: PDF, DOCX, PPTX, TXT, MP4, AVI, MOV, WEBM, MKV`);
        e.target.value = '';
        return;
      }
      setFile(selectedFile);
      setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('title', form.title);
      if (form.description) formData.append('description', form.description);
      formData.append('level', form.level);
      formData.append('published', form.published);
      if (file) formData.append('file', file);

      await createCourse(formData);
      navigate('/teacher/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data?.detail || 'Erreur lors de la création');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page" style={{ maxWidth: 700, margin: '0 auto' }}>
      <div className="page-header">
        <h1>📝 Nouveau cours</h1>
        <p>Créez et publiez un nouveau cours pour vos étudiants</p>
      </div>

      <div className="card" style={{ padding: 32 }}>
        {error && <div className="error-message" style={{ marginBottom: 20 }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div className="form-group">
            <label htmlFor="title">Titre du cours *</label>
            <input id="title" name="title" className="form-input"
              placeholder="Ex: Introduction à Python" value={form.title}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea id="description" name="description" className="form-input"
              placeholder="Décrivez le contenu et les objectifs du cours..."
              value={form.description} onChange={handleChange} rows={3} />
          </div>

          <div className="form-group">
            <label htmlFor="file">📄 Fichier du cours (PDF, DOCX, PPTX, TXT, MP4, AVI, MOV...)</label>
            <input
              id="file"
              name="file"
              type="file"
              className="form-input"
              accept=".pdf,.docx,.pptx,.txt,.mp4,.avi,.mov,.webm,.mkv"
              onChange={handleFileChange}
              style={{ padding: '12px' }}
            />
            {file && (
              <p style={{ marginTop: 8, color: 'var(--primary-500)', fontSize: '0.9rem' }}>
                ✅ Fichier sélectionné : {file.name} ({(file.size / (1024 * 1024)).toFixed(1)} MB)
              </p>
            )}
            <p style={{ marginTop: 4, color: 'var(--text-muted)', fontSize: '0.8rem' }}>
              Documents et vidéos acceptés. Le contenu texte sera extrait automatiquement.
            </p>
          </div>

          <div className="form-group">
            <label htmlFor="level">Niveau</label>
            <select id="level" name="level" className="form-input"
              value={form.level} onChange={handleChange}>
              <option value="BEGINNER">🟢 Débutant</option>
              <option value="INTERMEDIATE">🟡 Intermédiaire</option>
              <option value="ADVANCED">🔴 Avancé</option>
            </select>
          </div>

          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" name="published" checked={form.published}
              onChange={handleChange} style={{ width: 18, height: 18, accentColor: 'var(--primary-500)' }} />
            <span style={{ color: 'var(--text-secondary)' }}>
              Publier immédiatement (visible par les étudiants)
            </span>
          </label>

          <div style={{ display: 'flex', gap: 12 }}>
            <button type="submit" className="btn btn-primary btn-lg" disabled={loading} style={{ flex: 1 }}>
              {loading ? 'Création en cours...' : 'Créer le cours'}
            </button>
            <button type="button" className="btn btn-secondary btn-lg"
              onClick={() => navigate('/teacher/dashboard')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
