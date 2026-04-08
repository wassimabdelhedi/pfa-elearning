import { useState, useEffect } from 'react';
import { getAdminDashboard, getAdminCategories, createCategory, deleteCategory } from '../../api/adminApi';
import { FiUsers, FiBookOpen, FiTag, FiPlus, FiTrash2, FiAward, FiLayers } from 'react-icons/fi';

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');
  const [newCat, setNewCat] = useState({ name: '', description: '', icon: '📚' });
  const [creating, setCreating] = useState(false);

  const iconOptions = ['📚', '💻', '📐', '🔬', '🤖', '📊', '🎨', '🌍', '⚡', '🧪', '📖', '🏗️', '🧠', '🎯', '🔧'];

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [dashRes, catRes] = await Promise.all([
        getAdminDashboard().catch(() => ({ data: {} })),
        getAdminCategories().catch(() => ({ data: [] }))
      ]);
      setStats(dashRes.data);
      setCategories(catRes.data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const showMsg = (text) => {
    setMsg(text);
    setTimeout(() => setMsg(''), 3000);
  };

  const handleCreateCategory = async (e) => {
    e.preventDefault();
    if (!newCat.name.trim()) return;
    setCreating(true);
    try {
      const res = await createCategory(newCat);
      setCategories([...categories, res.data]);
      setNewCat({ name: '', description: '', icon: '📚' });
      showMsg('✅ Catégorie créée avec succès');
    } catch (err) {
      if (err.response?.status === 409) {
        showMsg('❌ Cette catégorie existe déjà');
      } else {
        showMsg('❌ Erreur lors de la création');
      }
    } finally {
      setCreating(false);
    }
  };

  const handleDeleteCategory = async (id) => {
    if (!window.confirm('Supprimer cette catégorie ?')) return;
    try {
      await deleteCategory(id);
      setCategories(categories.filter(c => c.id !== id));
      showMsg('✅ Catégorie supprimée');
    } catch (err) {
      showMsg('❌ Erreur : cette catégorie est peut-être utilisée par des cours');
    }
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>🛡️ Administration</h1>
        <p>Gérez la plateforme LearnAgent</p>
      </div>

      {msg && (
        <div style={{
          padding: '12px 16px',
          background: msg.includes('✅') ? 'rgba(34,197,94,0.1)' : 'rgba(239,68,68,0.1)',
          border: `1px solid ${msg.includes('✅') ? 'rgba(34,197,94,0.2)' : 'rgba(239,68,68,0.2)'}`,
          borderRadius: 8,
          color: msg.includes('✅') ? '#4ade80' : '#f87171',
          textAlign: 'center',
          marginBottom: 20
        }}>
          {msg}
        </div>
      )}

      {/* Stats */}
      {stats && (
        <div className="stats-grid">
          <div className="stat-card animate-in">
            <div className="stat-icon">👨‍🎓</div>
            <div className="stat-value">{stats.totalStudents || 0}</div>
            <div className="stat-label">Étudiants</div>
          </div>
          <div className="stat-card animate-in">
            <div className="stat-icon">👨‍🏫</div>
            <div className="stat-value">{stats.totalTeachers || 0}</div>
            <div className="stat-label">Enseignants</div>
          </div>
          <div className="stat-card animate-in">
            <div className="stat-icon">📚</div>
            <div className="stat-value">{stats.totalCourses || 0}</div>
            <div className="stat-label">Cours</div>
          </div>
          <div className="stat-card animate-in">
            <div className="stat-icon">📝</div>
            <div className="stat-value">{stats.totalEnrollments || 0}</div>
            <div className="stat-label">Inscriptions</div>
          </div>
        </div>
      )}

      {/* ========== CATEGORY MANAGEMENT ========== */}
      <div style={{ marginTop: 16 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
          <h2 style={{ fontSize: '1.3rem', display: 'flex', alignItems: 'center', gap: 8 }}>
            <FiLayers style={{ verticalAlign: 'middle' }} />
            Gestion des catégories
          </h2>
          <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
            {categories.length} catégorie{categories.length > 1 ? 's' : ''}
          </span>
        </div>

        {/* Create category form */}
        <div className="card" style={{ padding: 24, marginBottom: 24 }}>
          <h3 style={{ fontSize: '1rem', marginBottom: 16, color: 'var(--primary-300)' }}>
            <FiPlus style={{ verticalAlign: 'middle', marginRight: 6 }} />
            Ajouter une catégorie
          </h3>
          <form onSubmit={handleCreateCategory} style={{ display: 'flex', gap: 12, flexWrap: 'wrap', alignItems: 'flex-end' }}>
            <div className="form-group" style={{ flex: '0 0 auto' }}>
              <label>Icône</label>
              <select
                className="form-input"
                value={newCat.icon}
                onChange={e => setNewCat({ ...newCat, icon: e.target.value })}
                style={{ width: 80, textAlign: 'center', fontSize: '1.2rem' }}
              >
                {iconOptions.map(icon => (
                  <option key={icon} value={icon}>{icon}</option>
                ))}
              </select>
            </div>
            <div className="form-group" style={{ flex: '1 1 200px' }}>
              <label>Nom *</label>
              <input
                className="form-input"
                placeholder="Ex: Informatique"
                value={newCat.name}
                onChange={e => setNewCat({ ...newCat, name: e.target.value })}
                required
              />
            </div>
            <div className="form-group" style={{ flex: '1 1 250px' }}>
              <label>Description</label>
              <input
                className="form-input"
                placeholder="Brève description..."
                value={newCat.description}
                onChange={e => setNewCat({ ...newCat, description: e.target.value })}
              />
            </div>
            <button type="submit" className="btn btn-primary" disabled={creating} style={{ height: 46 }}>
              {creating ? '...' : 'Ajouter'}
            </button>
          </form>
        </div>

        {/* Categories table */}
        {categories.length > 0 ? (
          <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.03)' }}>
                  <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Icône</th>
                  <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Nom</th>
                  <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Description</th>
                  <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Cours</th>
                  <th style={{ padding: '14px 20px', textAlign: 'right', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Actions</th>
                </tr>
              </thead>
              <tbody>
                {categories.map(cat => (
                  <tr key={cat.id}
                    style={{ borderBottom: '1px solid rgba(255,255,255,0.05)', transition: 'background 0.2s' }}
                    onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.03)'}
                    onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                  >
                    <td style={{ padding: '14px 20px', fontSize: '1.4rem' }}>{cat.icon || '📚'}</td>
                    <td style={{ padding: '14px 20px', fontWeight: 600, fontSize: '0.95rem' }}>{cat.name}</td>
                    <td style={{ padding: '14px 20px', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{cat.description || '—'}</td>
                    <td style={{ padding: '14px 20px', textAlign: 'center' }}>
                      <span className="badge badge-primary">{cat.courses?.length || 0}</span>
                    </td>
                    <td style={{ padding: '14px 20px', textAlign: 'right' }}>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDeleteCategory(cat.id)}
                        title="Supprimer"
                      >
                        <FiTrash2 size={14} />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="card" style={{ textAlign: 'center', padding: 60 }}>
            <FiTag size={48} color="var(--text-muted)" style={{ marginBottom: 16 }} />
            <p style={{ color: 'var(--text-secondary)' }}>
              Aucune catégorie. Ajoutez-en une ci-dessus.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
