import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { register } from '../../api/authApi';
import { FiBookOpen } from 'react-icons/fi';

export default function RegisterPage() {
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', role: 'STUDENT',
    niveau: 'Débutant',
    domaineInteret: 'Web',
    autreDomaineInteret: '',
    objectif: 'Apprendre',
    autreObjectif: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const res = await register(form);
      const { token, userId, fullName, role } = res.data;
      loginUser({ id: userId, email: form.email, fullName, role }, token);

      if (role === 'TEACHER') navigate('/teacher/dashboard');
      else navigate('/dashboard');
    } catch (err) {
      const data = err.response?.data;
      if (data?.details) {
        setError(Object.values(data.details).join(', '));
      } else {
        setError(data?.message || 'Erreur lors de l\'inscription');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="card auth-card">
        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <FiBookOpen size={40} color="var(--primary-400)" />
        </div>
        <h1>Créer un compte</h1>
        <p className="subtitle">Rejoignez la plateforme LearnAgent</p>

        {error && <div className="error-message">{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="firstName">Prénom</label>
            <input id="firstName" name="firstName" className="form-input"
              placeholder="Ahmed" value={form.firstName}
              onChange={handleChange} required />
          </div>
          
          <div className="form-group">
            <label htmlFor="lastName">Nom</label>
            <input id="lastName" name="lastName" className="form-input"
              placeholder="Ben Ali" value={form.lastName}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="reg-email">Email</label>
            <input id="reg-email" name="email" type="email" className="form-input"
              placeholder="votre@email.com" value={form.email}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="reg-password">Mot de passe</label>
            <input id="reg-password" name="password" type="password" className="form-input"
              placeholder="Minimum 6 caractères" value={form.password}
              onChange={handleChange} required minLength={6} />
          </div>

          <div className="form-group">
            <label htmlFor="role">Je suis</label>
            <select id="role" name="role" className="form-input"
              value={form.role} onChange={handleChange}>
              <option value="STUDENT">🎓 Étudiant</option>
              <option value="TEACHER">👨‍🏫 Enseignant</option>
            </select>
          </div>

          {form.role === 'STUDENT' && (
            <>
              <div className="form-group">
                <label htmlFor="niveau">Niveau</label>
                <select id="niveau" name="niveau" className="form-input"
                  value={form.niveau} onChange={handleChange}>
                  <option value="Débutant">Débutant</option>
                  <option value="Intermédiaire">Intermédiaire</option>
                  <option value="Avancé">Avancé</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="domaineInteret">Domaine d'intérêt principal</label>
                <select id="domaineInteret" name="domaineInteret" className="form-input"
                  value={form.domaineInteret} onChange={handleChange}>
                  <option value="Web">Web</option>
                  <option value="IA">IA</option>
                  <option value="Data">Data</option>
                  <option value="Autre">Autre</option>
                </select>
              </div>

              <div className="form-group">
                <label htmlFor="objectif">Objectif</label>
                <select id="objectif" name="objectif" className="form-input"
                  value={form.objectif} onChange={handleChange}>
                  <option value="Apprendre">Apprendre</option>
                  <option value="Se perfectionner">Se perfectionner</option>
                  <option value="Réussir un examen">Réussir un examen</option>
                  <option value="Autre">Autre</option>
                </select>
              </div>
            </>
          )}

          <button type="submit" className="btn btn-primary btn-lg" disabled={loading}
            style={{ width: '100%' }}>
            {loading ? 'Inscription...' : 'Créer mon compte'}
          </button>
        </form>

        <div className="auth-footer">
          Déjà un compte ?{' '}
          <Link to="/login">Se connecter</Link>
        </div>
      </div>
    </div>
  );
}
