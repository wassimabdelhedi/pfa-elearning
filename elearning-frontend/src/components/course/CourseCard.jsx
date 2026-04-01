import { FiStar, FiUser, FiClock } from 'react-icons/fi';
import { useNavigate } from 'react-router-dom';

export default function CourseCard({ course, onEnroll, onDelete, showDeleteBtn }) {
  const navigate = useNavigate();

  const getLevelBadge = (level) => {
    const classes = {
      BEGINNER: 'badge badge-beginner',
      INTERMEDIATE: 'badge badge-intermediate',
      ADVANCED: 'badge badge-advanced',
    };
    const labels = {
      BEGINNER: 'Débutant',
      INTERMEDIATE: 'Intermédiaire',
      ADVANCED: 'Avancé',
    };
    return <span className={classes[level] || 'badge badge-primary'}>{labels[level] || level}</span>;
  };

  const renderStars = (rating) => {
    const stars = [];
    for (let i = 1; i <= 5; i++) {
      stars.push(
        <FiStar
          key={i}
          size={14}
          fill={i <= Math.round(rating) ? '#fbbf24' : 'transparent'}
          color={i <= Math.round(rating) ? '#fbbf24' : 'var(--text-muted)'}
        />
      );
    }
    return <div className="stars">{stars}</div>;
  };

  const handleCardClick = () => {
    const courseId = course.courseId || course.id;
    if (courseId) {
      navigate(`/course/${courseId}`);
    }
  };

  return (
    <div className="card course-card animate-in" onClick={handleCardClick} style={{ cursor: 'pointer' }}>
      <div className="course-card-header">
        {course.categoryName && (
          <span className="badge badge-primary">{course.categoryName}</span>
        )}
        {course.level && getLevelBadge(course.level)}
      </div>

      <div className="course-card-body">
        <h3>{course.courseTitle || course.title}</h3>
        <p>{course.courseDescription || course.description}</p>

        {course.reason && (
          <p style={{ fontSize: '0.8rem', color: 'var(--accent-400)', marginTop: 8, fontStyle: 'italic' }}>
            💡 {course.reason}
          </p>
        )}
      </div>

      <div className="course-card-footer">
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <span className="course-meta">
            <FiUser size={13} />
            {course.teacherName}
          </span>
          {course.averageRating > 0 && renderStars(course.averageRating)}
        </div>

        <div style={{ display: 'flex', gap: 8 }} onClick={(e) => e.stopPropagation()}>
          {onEnroll && (
            <button className="btn btn-primary btn-sm" onClick={() => onEnroll(course.courseId || course.id)}>
              S'inscrire
            </button>
          )}
          {showDeleteBtn && onDelete && (
            <button className="btn btn-danger btn-sm" onClick={() => onDelete(course.id)}>
              Supprimer
            </button>
          )}
        </div>
      </div>
    </div>
  );
}
