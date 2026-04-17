import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getExerciseById, updateExercise } from '../../api/exerciseApi';
import { getMyTeacherCourses } from '../../api/courseApi';
import { getCategories } from '../../api/userApi';

export default function EditExercise() {
  const { id } = useParams();
  const [form, setForm] = useState({
    title: '', description: '', categoryId: '', courseId: '', level: 'BEGINNER', published: false
  });
  const [file, setFile] = useState(null);
  const [currentFileName, setCurrentFileName] = useState('');
  const [courses, setCourses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [coursesRes, catRes, exerciseRes] = await Promise.all([
          getMyTeacherCourses().catch(() => ({ data: [] })),
          getCategories().catch(() => ({ data: [] })),
          getExerciseById(id)
        ]);
        setCourses(coursesRes.data);
        setCategories(catRes.data);
        
        const ex = exerciseRes.data;
        setForm({
          title: ex.title || '',
          description: ex.description || '',
          categoryId: ex.categoryId || '',
          courseId: ex.courseId || '',
          level: ex.level || 'BEGINNER',
          published: ex.published || false
        });
        setCurrentFileName(ex.originalFileName || '');
      } catch (err) {
        setError('Impossible de charger les données de l\'exercice');
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

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      const allowedTypes = ['.pdf', '.docx', '.pptx', '.txt'];
      const ext = '.' + selectedFile.name.split('.').pop().toLowerCase();
      if (!allowedTypes.includes(ext)) {
        setError(`Type de fichier non supporté: ${ext}. Types acceptés: PDF, DOCX, PPTX, TXT`);
        e.target.value = '';
        return;
      }
      setFile(selectedFile);
      setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');

    try {
      if (!form.courseId) {
        setError("Veuillez associer l'exercice à un cours");
        setSaving(false);
        return;
      }

      const formData = new FormData();
      formData.append('title', form.title);
      if (form.description) formData.append('description', form.description);
      if (form.categoryId) formData.append('categoryId', form.categoryId);
      if (form.courseId) formData.append('courseId', form.courseId);
      formData.append('level', form.level);
      formData.append('published', form.published);
      if (file) formData.append('file', file);

      await updateExercise(id, formData);
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
        <h1>✏️ Modifier l'exercice</h1>
        <p>Mettez à jour les informations de votre exercice</p>
      </div>

      <div className="card" style={{ padding: 32 }}>
        {error && <div className="error-message" style={{ marginBottom: 20 }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div className="form-group">
            <label htmlFor="title">Titre de l'exercice *</label>
            <input id="title" name="title" className="form-input"
              placeholder="Ex: Exercice sur les boucles Python" value={form.title}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea id="description" name="description" className="form-input"
              placeholder="Décrivez l'exercice et les objectifs..."
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
            <label htmlFor="file">📄 Fichier de l'exercice (PDF, DOCX, PPTX, TXT)</label>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)', marginBottom: 8 }}>
              {currentFileName ? `Fichier actuel : ${currentFileName}` : 'Aucun fichier actuellement.'}
            </p>
            <input id="file" name="file" type="file" className="form-input"
              accept=".pdf,.docx,.pptx,.txt" onChange={handleFileChange}
              style={{ padding: '12px' }} />
            {file && (
              <p style={{ marginTop: 8, color: 'var(--primary-500)', fontSize: '0.9rem' }}>
                ✅ Nouveau fichier sélectionné : {file.name} ({(file.size / 1024).toFixed(1)} KB)
              </p>
            )}
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

          <div className="form-group">
            <label htmlFor="courseId">Cours associé (obligatoire)</label>
            <select id="courseId" name="courseId" className="form-input"
              value={form.courseId} onChange={handleChange} required>
              <option value="">-- Sélectionnez un cours --</option>
              {courses.map(course => (
                <option key={course.id} value={course.id}>{course.title}</option>
              ))}
            </select>
          </div>

          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" name="published" checked={form.published}
              onChange={handleChange} style={{ width: 18, height: 18, accentColor: 'var(--primary-500)' }} />
            <span style={{ color: 'var(--text-secondary)' }}>
              Publié
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

