import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createCourse } from '../../api/courseApi';
import { getCategories } from '../../api/userApi';

export default function CreateCourse() {
  const [form, setForm] = useState({
    title: '', description: '', categoryId: '', level: 'BEGINNER', published: false
  });
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    getCategories().then(res => setCategories(res.data)).catch(() => {});
  }, []);

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm({ ...form, [e.target.name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('title', form.title);
      if (form.description) formData.append('description', form.description);
      if (form.categoryId) formData.append('categoryId', form.categoryId);
      formData.append('level', form.level);
      formData.append('published', form.published);

      const res = await createCourse(formData);
      // Redirect to chapter management page to add modules
      navigate(`/teacher/course/${res.data.id}/chapters`);
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
        <p>Créez un cours puis ajoutez-y des chapitres avec différents supports</p>
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
            <label htmlFor="categoryId">📂 Catégorie</label>
            <select id="categoryId" name="categoryId" className="form-input"
              value={form.categoryId} onChange={handleChange}>
              <option value="">-- Aucune catégorie --</option>
              {categories.map(cat => (
                <option key={cat.id} value={cat.id}>{cat.icon} {cat.name}</option>
              ))}
            </select>
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
              {loading ? 'Création en cours...' : 'Créer et ajouter des chapitres →'}
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
