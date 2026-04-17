import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getCourseById, updateCourse } from '../../api/courseApi';
import { getCategories } from '../../api/userApi';

export default function EditCourse() {
  const { id } = useParams();
  const [form, setForm] = useState({
    title: '', description: '', categoryId: '', level: 'BEGINNER', published: false
  });
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [catRes, courseRes] = await Promise.all([
          getCategories(),
          getCourseById(id)
        ]);
        setCategories(catRes.data);
        const c = courseRes.data;
        setForm({
          title: c.title || '',
          description: c.description || '',
          categoryId: c.categoryId || '',
          level: c.level || 'BEGINNER',
          published: c.published || false
        });
      } catch (err) {
        setError('Impossible de charger les données du cours');
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [id]);

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm({ ...form, [e.target.name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');

    try {
      const formData = new FormData();
      formData.append('title', form.title);
      if (form.description) formData.append('description', form.description);
      if (form.categoryId) formData.append('categoryId', form.categoryId);
      formData.append('level', form.level);
      formData.append('published', form.published);

      await updateCourse(id, formData);
      navigate('/teacher/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la modification');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="loading-container"><div className="spinner"></div></div>;

  return (
    <div className="page" style={{ maxWidth: 700, margin: '0 auto' }}>
      <div className="page-header">
        <h1>✏️ Modifier le cours</h1>
        <p>Mettez à jour les informations générales de votre cours</p>
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
              Publié (visible par les étudiants)
            </span>
          </label>

          <div style={{ display: 'flex', gap: 12 }}>
            <button type="submit" className="btn btn-primary btn-lg" disabled={saving} style={{ flex: 1 }}>
              {saving ? 'Enregistrement...' : 'Enregistrer les modifications'}
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

