import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getUserDetails } from '../../api/adminApi';
import { FiUser, FiMail, FiCalendar, FiBook, FiAward, FiArrowLeft, FiActivity, FiBriefcase, FiTarget, FiInfo, FiCheck, FiXCircle, FiClock, FiTrendingUp, FiAlertTriangle, FiZap, FiArrowUpRight, FiArrowDownRight } from 'react-icons/fi';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Area, AreaChart } from 'recharts';

export default function UserDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const res = await getUserDetails(id);
        setUser(res.data);
      } catch (err) {
        setError("Impossible de charger les détails de l'utilisateur.");
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    fetchUser();
  }, [id]);

  if (loading) return <div className="loading-container"><div className="spinner"></div></div>;
  if (error) return <div className="page"><div className="error-message">{error}</div></div>;
  if (!user) return <div className="page">Utilisateur introuvable</div>;

  // Formatting data for chart
  const chartData = (user.quizResults || [])
    .slice(0, 10)
    .reverse()
    .map(r => ({
      name: r.dateShort,
      score: r.percentage,
      classAvg: r.classAverage,
      title: r.quizTitle
    }));

  // AI Insights extraction
  const lastQuizWithWeakTopics = user.quizResults?.find(r => r.weakTopicsRaw);
  let weakTopics = [];
  if (lastQuizWithWeakTopics?.weakTopicsRaw) {
    try {
      const parsed = JSON.parse(lastQuizWithWeakTopics.weakTopicsRaw);
      weakTopics = Array.isArray(parsed) ? parsed : [];
    } catch (e) { console.error(e); }
  }

  const evolutionColor = user.evolution > 0 ? '#4ade80' : user.evolution < 0 ? '#f87171' : '#60a5fa';

  return (
    <div className="page" style={{ maxWidth: '1240px', margin: '0 auto', width: '100%', overflowX: 'hidden', boxSizing: 'border-box' }}>
      <div className="page-header">
        <button className="btn btn-secondary btn-sm" onClick={() => navigate(-1)} style={{ marginBottom: 16 }}>
          <FiArrowLeft size={14} style={{ marginRight: 8 }} /> Retour au Dashboard
        </button>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-end' }}>
          <div>
            <h1 style={{ display: 'flex', alignItems: 'center', gap: 12, margin: 0 }}>
              <FiActivity size={32} color="var(--primary-400)" /> Analyse de Performance IA
            </h1>
            <p style={{ marginTop: 8, color: 'var(--text-secondary)' }}>Suivi analytique et courbe de progression : {user.fullName}</p>
          </div>
          <div style={{ textAlign: 'right' }}>
            <span className="badge badge-primary" style={{ padding: '8px 16px', fontSize: '0.9rem' }}>{user.role}</span>
          </div>
        </div>
      </div>

      {/* KPI CARDS SECTION */}
      {user.role === 'STUDENT' && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '20px', marginBottom: '24px' }}>
          <div className="card" style={{ padding: '20px' }}>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: 8 }}>Moyenne Actuelle</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'white' }}>{user.averageScore || 0}<span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>%</span></div>
          </div>
          <div className="card" style={{ padding: '20px' }}>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: 8 }}>Évolution</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
              <div style={{ fontSize: '2rem', fontWeight: 800, color: evolutionColor }}>
                {user.evolution > 0 ? '+' : ''}{user.evolution || 0}
              </div>
              {user.evolution > 0 ? <FiArrowUpRight size={24} color="#4ade80" /> : user.evolution < 0 ? <FiArrowDownRight size={24} color="#f87171" /> : null}
            </div>
          </div>
          <div className="card" style={{ padding: '20px' }}>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: 8 }}>Taux de Réussite</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: '#4ade80' }}>{user.successRate || 0}<span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>%</span></div>
          </div>
          <div className="card" style={{ padding: '20px' }}>
            <div style={{ color: 'var(--text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', fontWeight: 700, marginBottom: 8 }}>Dernière Note</div>
            <div style={{ fontSize: '2rem', fontWeight: 800, color: 'var(--primary-400)' }}>{user.lastScore || 0}<span style={{ fontSize: '1rem', color: 'var(--text-muted)' }}>%</span></div>
          </div>
        </div>
      )}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '24px' }}>
        
        {/* LEFT COLUMN */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          
          {/* User Profile Card */}
          <div className="card">
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              <div style={{ 
                width: 80, height: 80, borderRadius: '50%', background: 'linear-gradient(135deg, var(--primary-500), var(--primary-700))', 
                color: 'white', display: 'flex', alignItems: 'center', justifyContent: 'center',
                fontSize: '2rem', fontWeight: 800, margin: '0 auto 16px', boxShadow: '0 8px 16px rgba(0,0,0,0.3)'
              }}>
                {user.firstName?.[0]}{user.lastName?.[0]}
              </div>
              <h2 style={{ fontSize: '1.25rem', marginBottom: 4 }}>{user.fullName}</h2>
              <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>{user.email}</div>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 12, padding: '16px 0', borderTop: '1px solid rgba(255,255,255,0.05)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--text-secondary)', fontSize: '0.9rem' }}>
                <FiClock size={16} /> <span>Dernière activité : {user.lastLoginDate ? new Date(user.lastLoginDate).toLocaleString() : 'Jamais'}</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: user.active ? 'var(--success)' : 'var(--error)', fontSize: '0.9rem', fontWeight: 600 }}>
                <FiCheck size={16} /> <span>{user.active ? 'Compte Actif' : 'Compte Suspendu'}</span>
              </div>
            </div>
          </div>

          {/* AI Insights Card */}
          {user.role === 'STUDENT' && user.insights?.length > 0 && (
            <div className="card" style={{ background: 'rgba(99,102,241,0.05)', border: '1px solid rgba(99,102,241,0.2)' }}>
              <h3 style={{ fontSize: '1rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                <FiZap color="#fbbf24" /> Insights IA
              </h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
                {user.insights.map((insight, i) => (
                  <div key={i} style={{ display: 'flex', gap: 10, fontSize: '0.9rem', color: 'var(--text-secondary)', alignItems: 'flex-start' }}>
                    <div style={{ marginTop: 4, width: 6, height: 6, borderRadius: '50%', background: 'var(--primary-400)', flexShrink: 0 }} />
                    {insight}
                  </div>
                ))}
              </div>
            </div>
          )}

          {/* Strengths / Weaknesses */}
          {user.role === 'STUDENT' && weakTopics.length > 0 && (
            <div className="card">
              <h3 style={{ fontSize: '1rem', marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
                <FiAlertTriangle color="#f87171" /> Points à améliorer
              </h3>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: 8 }}>
                {weakTopics.map((wt, i) => (
                  <span key={i} style={{ padding: '6px 12px', background: 'rgba(248,113,113,0.1)', color: '#f87171', borderRadius: 8, fontSize: '0.8rem', fontWeight: 600, border: '1px solid rgba(248,113,113,0.2)' }}>
                    {wt.topic}
                  </span>
                ))}
              </div>
            </div>
          )}
        </div>

        {/* RIGHT COLUMN */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          
          {/* MAIN LINE CHART CARD */}
          <div className="card" style={{ minHeight: '400px' }}>
            <h3 style={{ fontSize: '1.1rem', marginBottom: 24, display: 'flex', alignItems: 'center', gap: 8 }}>
              <FiTrendingUp color="var(--primary-400)" /> Évolution de la Performance
            </h3>
            <div style={{ width: '100%', height: 300 }}>
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={chartData}>
                  <defs>
                    <linearGradient id="colorScore" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor={evolutionColor} stopOpacity={0.3}/>
                      <stop offset="95%" stopColor={evolutionColor} stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.05)" vertical={false} />
                  <XAxis 
                    dataKey="name" 
                    stroke="var(--text-muted)" 
                    fontSize={10} 
                    tickLine={false} 
                    axisLine={false} 
                    dy={10}
                  />
                  <YAxis 
                    stroke="var(--text-muted)" 
                    fontSize={10} 
                    tickLine={false} 
                    axisLine={false} 
                    domain={[0, 100]}
                    tickFormatter={(v) => `${v}%`}
                  />
                  <Tooltip 
                    contentStyle={{ backgroundColor: '#1e1e2e', border: '1px solid rgba(255,255,255,0.1)', borderRadius: '8px' }}
                    itemStyle={{ fontSize: '0.8rem' }}
                  />
                  <Area 
                    type="monotone" 
                    dataKey="score" 
                    name="Mon Score"
                    stroke={evolutionColor} 
                    strokeWidth={3}
                    fillOpacity={1} 
                    fill="url(#colorScore)" 
                  />
                  <Area 
                    type="monotone" 
                    dataKey="classAvg" 
                    name="Moyenne Classe"
                    stroke="rgba(255,255,255,0.3)" 
                    strokeWidth={2}
                    strokeDasharray="5 5"
                    fill="transparent"
                  />
                </AreaChart>
              </ResponsiveContainer>
            </div>
            <div style={{ display: 'flex', justifyContent: 'center', gap: 24, marginTop: 16, fontSize: '0.8rem', color: 'var(--text-muted)' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <div style={{ width: 12, height: 3, background: evolutionColor, borderRadius: 2 }} /> Score Étudiant
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <div style={{ width: 12, height: 2, borderTop: '2px dashed rgba(255,255,255,0.5)' }} /> Moyenne Classe
              </div>
            </div>
          </div>

          {/* Enrolled Courses Progress */}
          <div className="card">
            <h3 style={{ fontSize: '1.1rem', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 8 }}>
              <FiBook color="var(--primary-400)" /> Progression par Cours
            </h3>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 16 }}>
              {user.enrolledCourses?.map(course => (
                <div key={course.id} style={{ padding: 16, background: 'rgba(255,255,255,0.02)', borderRadius: 12, border: '1px solid rgba(255,255,255,0.05)' }}>
                  <div style={{ fontSize: '0.9rem', fontWeight: 600, marginBottom: 8, whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis' }}>{course.title}</div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                    <div style={{ flex: 1, height: 6, background: 'rgba(255,255,255,0.1)', borderRadius: 3, overflow: 'hidden' }}>
                      <div style={{ height: '100%', width: `${course.progress}%`, background: course.completed ? '#4ade80' : 'var(--primary-500)' }} />
                    </div>
                    <span style={{ fontSize: '0.8rem', fontWeight: 700 }}>{Math.round(course.progress || 0)}%</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* NEW SECTIONS AT THE BOTTOM */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '24px', marginTop: '24px' }}>
        
        {/* Detailed Info Card */}
        <div className="card">
          <h3 style={{ fontSize: '1.1rem', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 8 }}>
            <FiInfo color="var(--primary-400)" /> Informations profil
          </h3>
          <div style={{ display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div className="info-item">
              <label style={{ display: 'block', fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: 4, textTransform: 'uppercase', fontWeight: 600 }}>Niveau d'études</label>
              <div style={{ fontWeight: 500, fontSize: '0.9rem' }}>{user.niveauEtude || 'Non spécifié'}</div>
            </div>
            <div className="info-item">
              <label style={{ display: 'block', fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: 4, textTransform: 'uppercase', fontWeight: 600 }}>Compétences</label>
              <div style={{ fontWeight: 500, fontSize: '0.9rem' }}>{user.niveauCompetence || 'Non spécifié'}</div>
            </div>
            <div className="info-item">
              <label style={{ display: 'block', fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: 4, textTransform: 'uppercase', fontWeight: 600 }}>Domaines d'intérêt</label>
              <div style={{ fontWeight: 500, fontSize: '0.9rem' }}>{user.domaineInteret || 'Non spécifié'}</div>
            </div>
            <div className="info-item">
              <label style={{ display: 'block', fontSize: '0.7rem', color: 'var(--text-muted)', marginBottom: 4, textTransform: 'uppercase', fontWeight: 600 }}>Objectif</label>
              <div style={{ fontWeight: 500, fontSize: '0.9rem' }}>{user.objectif || 'Non spécifié'}</div>
            </div>
          </div>
        </div>

        {/* Detailed Quiz Table */}
        <div className="card">
          <h3 style={{ fontSize: '1.1rem', marginBottom: 20, display: 'flex', alignItems: 'center', gap: 8 }}>
            <FiAward color="var(--primary-400)" /> Historique complet des Quiz
          </h3>
          <div style={{ overflowX: 'auto' }}>
            <table style={{ width: '100%', borderCollapse: 'collapse' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid rgba(255,255,255,0.1)', textAlign: 'left' }}>
                  <th style={{ padding: '12px 8px', fontSize: '0.8rem', color: 'var(--text-muted)' }}>NOM DU QUIZ</th>
                  <th style={{ padding: '12px 8px', fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'center' }}>VOTRE SCORE</th>
                  <th style={{ padding: '12px 8px', fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'center' }}>MOYENNE CLASSE</th>
                  <th style={{ padding: '12px 8px', fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'center' }}>STATUT</th>
                  <th style={{ padding: '12px 8px', fontSize: '0.8rem', color: 'var(--text-muted)', textAlign: 'right' }}>DATE</th>
                </tr>
              </thead>
              <tbody>
                {user.quizResults?.map(res => (
                  <tr key={res.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                    <td style={{ padding: '14px 8px', fontSize: '0.9rem', fontWeight: 600 }}>{res.quizTitle}</td>
                    <td style={{ padding: '14px 8px', textAlign: 'center' }}>
                      <span style={{ fontWeight: 800, color: res.percentage >= 70 ? '#4ade80' : '#fbbf24' }}>{res.percentage}%</span>
                    </td>
                    <td style={{ padding: '14px 8px', textAlign: 'center' }}>
                      <span style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>{res.classAverage}%</span>
                    </td>
                    <td style={{ padding: '14px 8px', textAlign: 'center' }}>
                      {res.failed ? 
                        <span style={{ color: '#f87171', background: 'rgba(248,113,113,0.1)', padding: '4px 8px', borderRadius: 4, fontSize: '0.75rem' }}>Échec</span> : 
                        <span style={{ color: '#4ade80', background: 'rgba(74,222,128,0.1)', padding: '4px 8px', borderRadius: 4, fontSize: '0.75rem' }}>Réussi</span>
                      }
                    </td>
                    <td style={{ padding: '14px 8px', textAlign: 'right', fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      {new Date(res.date).toLocaleDateString()}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
