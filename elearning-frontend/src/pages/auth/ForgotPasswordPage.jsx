import { useState } from 'react';
import { Link } from 'react-router-dom';
import { forgotPassword } from '../../api/authApi';
import { FiMail, FiBookOpen } from 'react-icons/fi';

export default function ForgotPasswordPage() {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    setLoading(true);

    if (!email) {
      setError('Veuillez entrer votre email');
      setLoading(false);
      return;
    }

    try {
      const res = await forgotPassword(email);
      setSuccess(res.data.message || 'Un lien de réinitialisation a été envoyé à votre adresse email.');
      setEmail('');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de l\'envoi du lien de réinitialisation');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="card auth-card">
        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <FiMail size={40} color="var(--primary-400)" />
        </div>
        <h1>Mot de passe oublié ?</h1>
        <p className="subtitle">Entrez votre email pour recevoir un lien de réinitialisation sécurisé</p>

        {error && <div className="error-message">{error}</div>}
        {success && (
          <div style={{
            padding: '12px 16px', background: 'rgba(34,197,94,0.1)',
            border: '1px solid rgba(34,197,94,0.2)', borderRadius: 8,
            color: '#4ade80', textAlign: 'center', marginBottom: '16px'
          }}>
            {success}
          </div>
        )}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">Email</label>
            <input
              id="email"
              type="email"
              className="form-input"
              placeholder="votre@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <button type="submit" className="btn btn-primary btn-lg" disabled={loading || success}
            style={{ width: '100%' }}>
            {loading ? 'Envoi en cours...' : 'Envoyer le lien'}
          </button>
        </form>

        <div className="auth-footer">
          <Link to="/login">Retour à la connexion</Link>
        </div>
      </div>
    </div>
  );
}
