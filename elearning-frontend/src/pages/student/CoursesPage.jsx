import { useState, useEffect } from 'react';
import { getPublishedCourses } from '../../api/courseApi';
import { getCategories, enrollInCourse } from '../../api/userApi';
import CourseCard from '../../components/course/CourseCard';

export default function CoursesPage() {
  const [courses, setCourses] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState(null);
  const [loading, setLoading] = useState(true);
  const [enrollMsg, setEnrollMsg] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [coursesRes, catRes] = await Promise.all([
        getPublishedCourses(),
        getCategories().catch(() => ({ data: [] }))
      ]);
      setCourses(coursesRes.data);
      setCategories(catRes.data);
    } catch (err) {
      console.error(err);
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
      setEnrollMsg(err.response?.data?.message || 'Erreur');
      setTimeout(() => setEnrollMsg(''), 3000);
    }
  };

  const filtered = selectedCategory
    ? courses.filter(c => c.categoryId === selectedCategory)
    : courses;

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>📚 Catalogue des cours</h1>
        <p>{courses.length} cours disponibles</p>
      </div>

      {enrollMsg && (
        <div style={{
          padding: '12px 16px', background: 'rgba(34,197,94,0.1)',
          border: '1px solid rgba(34,197,94,0.2)', borderRadius: 8,
          color: '#4ade80', textAlign: 'center', marginBottom: 20
        }}>
          {enrollMsg}
        </div>
      )}

      {/* Category filter */}
      {Array.isArray(categories) && categories.length > 0 && (
        <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8, marginBottom: 28 }}>
          <button
            className={`btn ${!selectedCategory ? 'btn-primary' : 'btn-secondary'} btn-sm`}
            onClick={() => setSelectedCategory(null)}
          >
            Tous
          </button>
          {categories.map(cat => (
            <button
              key={cat.id}
              className={`btn ${selectedCategory === cat.id ? 'btn-primary' : 'btn-secondary'} btn-sm`}
              onClick={() => setSelectedCategory(cat.id)}
            >
              {cat.name}
            </button>
          ))}
        </div>
      )}

      {Array.isArray(filtered) && filtered.length > 0 ? (
        <div className="course-grid">
          {filtered.map(course => (
            <CourseCard key={course.id} course={course} onEnroll={handleEnroll} />
          ))}
        </div>
      ) : (
        <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-secondary)' }}>
          Aucun cours dans cette catégorie
        </div>
      )}
    </div>
  );
}
