import { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { getQuizById, updateQuiz } from '../../api/quizApi';
import { getMyTeacherCourses } from '../../api/courseApi';
import { getCategories } from '../../api/userApi';
import { FiPlusCircle, FiTrash2 } from 'react-icons/fi';

export default function EditQuiz() {
  const { id } = useParams();
  const [form, setForm] = useState({
    title: '', description: '', categoryId: '', courseId: '', level: 'BEGINNER', published: false
  });
  const [questions, setQuestions] = useState([
    { text: '', options: ['', '', '', ''], correctAnswer: 0 }
  ]);
  const [courses, setCourses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const [coursesRes, catRes, quizRes] = await Promise.all([
          getMyTeacherCourses().catch(() => ({ data: [] })),
          getCategories().catch(() => ({ data: [] })),
          getQuizById(id)
        ]);
        setCourses(coursesRes.data);
        setCategories(catRes.data);
        
        const q = quizRes.data;
        setForm({
          title: q.title || '',
          description: q.description || '',
          categoryId: q.categoryId || '',
          courseId: q.courseId || '',
          level: q.level || 'BEGINNER',
          published: q.published || false
        });
        
        if (q.questions && q.questions.length > 0) {
          setQuestions(q.questions.map(question => ({
            text: question.text,
            options: Array.isArray(question.options) ? question.options : ['', '', '', ''],
            correctAnswer: question.correctAnswer
          })));
        }
      } catch (err) {
        setError('Impossible de charger les données du quiz');
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

  const addQuestion = () => {
    setQuestions([...questions, { text: '', options: ['', '', '', ''], correctAnswer: 0 }]);
  };

  const removeQuestion = (index) => {
    if (questions.length <= 1) return;
    setQuestions(questions.filter((_, i) => i !== index));
  };

  const updateQuestion = (index, field, value) => {
    const updated = [...questions];
    updated[index][field] = value;
    setQuestions(updated);
  };

  const updateOption = (qIndex, oIndex, value) => {
    const updated = [...questions];
    updated[qIndex].options[oIndex] = value;
    setQuestions(updated);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');

    // Validate questions
    if (!form.courseId) {
      setError('Veuillez associer le quiz à un cours');
      setSaving(false);
      return;
    }

    for (let i = 0; i < questions.length; i++) {
      if (!questions[i].text.trim()) {
        setError(`La question ${i + 1} est vide`);
        setSaving(false);
        return;
      }
      for (let j = 0; j < questions[i].options.length; j++) {
        if (!questions[i].options[j].trim()) {
          setError(`L'option ${j + 1} de la question ${i + 1} est vide`);
          setSaving(false);
          return;
        }
      }
    }

    try {
      const payload = {
        ...form,
        categoryId: form.categoryId || null,
        courseId: form.courseId || null,
        questions
      };
      await updateQuiz(id, payload);
      navigate('/teacher/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la modification');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <div className="loading-container"><div className="spinner"></div></div>;

  return (
    <div className="page" style={{ maxWidth: 800, margin: '0 auto' }}>
      <div className="page-header">
        <h1>✏️ Modifier le quiz</h1>
        <p>Mettez à jour votre quiz et ses questions</p>
      </div>

      <div className="card" style={{ padding: 32 }}>
        {error && <div className="error-message" style={{ marginBottom: 20 }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div className="form-group">
            <label htmlFor="title">Titre du quiz *</label>
            <input id="title" name="title" className="form-input"
              placeholder="Ex: Quiz Python - Les bases" value={form.title}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea id="description" name="description" className="form-input"
              placeholder="Décrivez l'objectif de ce quiz..."
              value={form.description} onChange={handleChange} rows={2} />
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

          {/* Questions */}
          <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: 20 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3 style={{ fontSize: '1.1rem' }}>Questions ({questions.length})</h3>
              <button type="button" className="btn btn-secondary btn-sm" onClick={addQuestion}>
                <FiPlusCircle size={14} /> Ajouter une question
              </button>
            </div>

            {questions.map((q, qIndex) => (
              <div key={qIndex} className="card" style={{ padding: 20, marginBottom: 16, background: 'rgba(255,255,255,0.02)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
                  <span style={{ fontWeight: 600, color: 'var(--primary-300)' }}>Question {qIndex + 1}</span>
                  {questions.length > 1 && (
                    <button type="button" className="btn btn-danger btn-sm" onClick={() => removeQuestion(qIndex)}>
                      <FiTrash2 size={12} />
                    </button>
                  )}
                </div>

                <div className="form-group" style={{ marginBottom: 12 }}>
                  <input className="form-input" placeholder="Tapez la question..."
                    value={q.text} onChange={(e) => updateQuestion(qIndex, 'text', e.target.value)} />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                  {q.options.map((opt, oIndex) => (
                    <div key={oIndex} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <input
                        type="radio"
                        name={`correct-${qIndex}`}
                        checked={q.correctAnswer === oIndex}
                        onChange={() => updateQuestion(qIndex, 'correctAnswer', oIndex)}
                        style={{ accentColor: 'var(--primary-500)', width: 18, height: 18 }}
                        title="Bonne réponse"
                      />
                      <input
                        className="form-input"
                        placeholder={`Option ${String.fromCharCode(65 + oIndex)}`}
                        value={opt}
                        onChange={(e) => updateOption(qIndex, oIndex, e.target.value)}
                        style={{ flex: 1 }}
                      />
                    </div>
                  ))}
                  <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: 4 }}>
                    Sélectionnez le bouton radio à côté de la bonne réponse
                  </p>
                </div>
              </div>
            ))}
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

