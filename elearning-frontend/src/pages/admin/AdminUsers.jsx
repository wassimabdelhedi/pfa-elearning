import { useState, useEffect } from 'react';
import { getAllUsers, toggleUserActive, deleteUser } from '../../api/adminApi';
import { FiUsers, FiTrash2, FiToggleLeft, FiToggleRight } from 'react-icons/fi';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [msg, setMsg] = useState('');
  const [filter, setFilter] = useState('ALL');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      const res = await getAllUsers();
      setUsers(res.data);
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

  const handleToggle = async (id) => {
    try {
      await toggleUserActive(id);
      setUsers(users.map(u => u.id === id ? { ...u, active: !u.active } : u));
      showMsg('✅ Statut mis à jour');
    } catch {
      showMsg('❌ Erreur');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Supprimer cet utilisateur ? Cette action est irréversible.')) return;
    try {
      await deleteUser(id);
      setUsers(users.filter(u => u.id !== id));
      showMsg('✅ Utilisateur supprimé');
    } catch {
      showMsg('❌ Erreur lors de la suppression');
    }
  };

  const filtered = filter === 'ALL' ? users : users.filter(u => u.role === filter);

  const getRoleBadge = (role) => {
    const map = {
      STUDENT: { label: 'Étudiant', cls: 'badge-primary' },
      TEACHER: { label: 'Enseignant', cls: 'badge-success' },
      ADMIN: { label: 'Admin', cls: 'badge-warning' }
    };
    const info = map[role] || { label: role, cls: 'badge-primary' };
    return <span className={`badge ${info.cls}`}>{info.label}</span>;
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  return (
    <div className="page">
      <div className="page-header">
        <h1>👥 Gestion des utilisateurs</h1>
        <p>{users.length} utilisateurs inscrits</p>
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

      {/* Filter */}
      <div style={{ display: 'flex', gap: 8, marginBottom: 24, flexWrap: 'wrap' }}>
        {['ALL', 'STUDENT', 'TEACHER', 'ADMIN'].map(role => (
          <button
            key={role}
            className={`btn ${filter === role ? 'btn-primary' : 'btn-secondary'} btn-sm`}
            onClick={() => setFilter(role)}
          >
            {role === 'ALL' ? 'Tous' : role === 'STUDENT' ? 'Étudiants' : role === 'TEACHER' ? 'Enseignants' : 'Admins'}
            {role !== 'ALL' && ` (${users.filter(u => u.role === role).length})`}
          </button>
        ))}
      </div>

      {/* Users table */}
      {filtered.length > 0 ? (
        <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', background: 'rgba(255,255,255,0.03)' }}>
                <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Nom</th>
                <th style={{ padding: '14px 20px', textAlign: 'left', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Email</th>
                <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Rôle</th>
                <th style={{ padding: '14px 20px', textAlign: 'center', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Statut</th>
                <th style={{ padding: '14px 20px', textAlign: 'right', fontSize: '0.85rem', color: 'var(--text-secondary)', fontWeight: 600 }}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {filtered.map(user => (
                <tr key={user.id}
                  style={{ borderBottom: '1px solid rgba(255,255,255,0.05)', transition: 'background 0.2s' }}
                  onMouseEnter={e => e.currentTarget.style.background = 'rgba(255,255,255,0.03)'}
                  onMouseLeave={e => e.currentTarget.style.background = 'transparent'}
                >
                  <td style={{ padding: '14px 20px', fontWeight: 600, fontSize: '0.9rem' }}>{user.fullName}</td>
                  <td style={{ padding: '14px 20px', fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{user.email}</td>
                  <td style={{ padding: '14px 20px', textAlign: 'center' }}>{getRoleBadge(user.role)}</td>
                  <td style={{ padding: '14px 20px', textAlign: 'center' }}>
                    <span style={{
                      padding: '4px 12px', borderRadius: 100, fontSize: '0.8rem', fontWeight: 600,
                      background: user.active ? 'rgba(34,197,94,0.15)' : 'rgba(239,68,68,0.15)',
                      color: user.active ? '#4ade80' : '#f87171'
                    }}>
                      {user.active ? 'Actif' : 'Inactif'}
                    </span>
                  </td>
                  <td style={{ padding: '14px 20px', textAlign: 'right' }}>
                    <div style={{ display: 'flex', gap: 8, justifyContent: 'flex-end' }}>
                      <button
                        className="btn btn-secondary btn-sm"
                        onClick={() => handleToggle(user.id)}
                        title={user.active ? 'Désactiver' : 'Activer'}
                      >
                        {user.active ? <FiToggleRight size={14} /> : <FiToggleLeft size={14} />}
                      </button>
                      <button
                        className="btn btn-danger btn-sm"
                        onClick={() => handleDelete(user.id)}
                        title="Supprimer"
                      >
                        <FiTrash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : (
        <div className="card" style={{ textAlign: 'center', padding: 60 }}>
          <FiUsers size={48} color="var(--text-muted)" style={{ marginBottom: 16 }} />
          <p style={{ color: 'var(--text-secondary)' }}>Aucun utilisateur trouvé</p>
        </div>
      )}
    </div>
  );
}
