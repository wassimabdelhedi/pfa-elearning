import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { register } from '../../api/authApi';
import { FiBookOpen, FiUser, FiTarget, FiArrowRight, FiArrowLeft, FiCheckCircle, FiEye, FiEyeOff } from 'react-icons/fi';

export default function RegisterPage() {
  const [step, setStep] = useState(1);
  const [selectedInterests, setSelectedInterests] = useState([]);
  const [form, setForm] = useState({
    firstName: '', lastName: '', email: '', password: '', role: 'STUDENT',
    niveauEtude: 'Étudiant universitaire',
    niveauCompetence: 'Débutant',
    domaineInteret: '',
    autreDomaineInteret: '',
    objectif: 'Améliorer mes compétences',
    autreObjectif: ''
  });
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { loginUser } = useAuth();
  const navigate = useNavigate();

  const studyLevels = [
    "Lycée ou moins",
    "Étudiant universitaire",
    "Étudiant Master",
    "Doctorat",
    "Autre"
  ];

  const interestDomains = [
    { id: 'tech', label: 'Technologie' },
    { id: 'dev-perso', label: 'Développement personnel' },
    { id: 'langues', label: 'Langues' },
    { id: 'science', label: 'Sciences & Ingénierie' },
    { id: 'ia', label: 'Intelligence Artificielle' },
    { id: 'arts', label: 'Arts' },
    { id: 'business', label: 'Business' },
    { id: 'autre', label: 'Autre' }
  ];

  const competenceLevels = [
    "Débutant",
    "Intermédiaire",
    "Avancé"
  ];

  const learningGoals = [
    "Trouver un emploi",
    "Améliorer mes compétences",
    "Changer de carrière",
    "Apprendre par intérêt",
    "Autre"
  ];

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const toggleInterest = (label) => {
    if (selectedInterests.includes(label)) {
      setSelectedInterests(selectedInterests.filter(i => i !== label));
    } else if (selectedInterests.length < 3) {
      setSelectedInterests([...selectedInterests, label]);
    }
  };

  const nextStep = () => {
    if (step === 1) {
      if (!form.firstName || !form.lastName || !form.email || !form.password) {
        setError('Veuillez remplir tous les champs obligatoires');
        return;
      }
      if (form.password.length < 6) {
        setError('Le mot de passe doit faire au moins 6 caractères');
        return;
      }
      setError('');
      if (form.role === 'TEACHER') {
        handleSubmit(); 
      } else {
        setStep(2);
      }
    }
  };

  const prevStep = () => setStep(1);

  const handleSubmit = async (e) => {
    if (e) e.preventDefault();
    
    if (form.role === 'STUDENT' && selectedInterests.length === 0) {
      setError('Veuillez sélectionner au moins un domaine d\'intérêt');
      return;
    }

    setError('');
    setLoading(true);

    const finalForm = {
      ...form,
      domaineInteret: selectedInterests.join(', ')
    };

    try {
      const res = await register(finalForm);
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
      setStep(1);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="card auth-card" style={{ maxWidth: '650px', width: '100%' }}>
        <div style={{ textAlign: 'center', marginBottom: 16 }}>
          <div className="icon-badge">
            <FiBookOpen size={32} />
          </div>
          <h1 style={{ fontSize: '1.8rem', marginTop: '12px' }}>Créer un compte</h1>
          <p className="subtitle">Rejoignez la plateforme LearnAgent</p>
        </div>

        {form.role === 'STUDENT' && (
          <div className="stepper-horizontal">
            <div className={`step-circle ${step >= 1 ? 'active' : ''}`}>1</div>
            <div className={`step-line ${step >= 2 ? 'active' : ''}`}></div>
            <div className={`step-circle ${step >= 2 ? 'active' : ''}`}>2</div>
          </div>
        )}

        {error && <div className="error-message" style={{ marginBottom: 20 }}>{error}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          {step === 1 ? (
            <div className="form-step-content">
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                <div className="form-group">
                  <label>Prénom</label>
                  <input name="firstName" className="form-input" placeholder="Ahmed" 
                    value={form.firstName} onChange={handleChange} required />
                </div>
                <div className="form-group">
                  <label>Nom</label>
                  <input name="lastName" className="form-input" placeholder="Ben Ali" 
                    value={form.lastName} onChange={handleChange} required />
                </div>
              </div>

              <div className="form-group">
                <label>Email professionnel ou personnel</label>
                <input name="email" type="email" className="form-input" placeholder="votre@email.com" 
                  value={form.email} onChange={handleChange} required />
              </div>

              <div className="form-group">
                <label>Mot de passe</label>
                <div style={{ position: 'relative' }}>
                  <input
                    name="password"
                    type={showPassword ? 'text' : 'password'}
                    className="form-input"
                    placeholder="Min. 6 caractères"
                    value={form.password}
                    onChange={handleChange}
                    required
                    minLength={6}
                    style={{ width: '100%', paddingRight: '45px' }}
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    style={{
                      position: 'absolute',
                      right: '12px',
                      top: '50%',
                      transform: 'translateY(-50%)',
                      background: 'none',
                      border: 'none',
                      color: 'var(--text-muted)',
                      display: 'flex',
                      alignItems: 'center',
                      padding: '4px'
                    }}
                  >
                    {showPassword ? <FiEyeOff size={18} /> : <FiEye size={18} />}
                  </button>
                </div>
              </div>

              <div className="form-group">
                <label>Je m'inscris en tant que</label>
                <div className="role-grid">
                  <div className={`role-card ${form.role === 'STUDENT' ? 'active' : ''}`}
                    onClick={() => setForm({...form, role: 'STUDENT'})}>
                    <div className="role-icon">🎓</div>
                    <span>Étudiant</span>
                  </div>
                  <div className={`role-card ${form.role === 'TEACHER' ? 'active' : ''}`}
                    onClick={() => setForm({...form, role: 'TEACHER'})}>
                    <div className="role-icon">👨‍🏫</div>
                    <span>Enseignant</span>
                  </div>
                </div>
              </div>

              <button type="button" className="btn btn-primary btn-lg" onClick={nextStep}
                style={{ width: '100%', marginTop: '20px', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {form.role === 'TEACHER' ? (loading ? 'Inscription...' : 'Créer mon compte') : 'Continuer'} 
                {form.role === 'STUDENT' && <FiArrowRight style={{ marginLeft: '10px' }} />}
              </button>
            </div>
          ) : (
            <div className="form-step-content">
              <div className="form-group">
                <label>Niveaux d’études</label>
                <select name="niveauEtude" className="form-input" value={form.niveauEtude} onChange={handleChange}>
                  {studyLevels.map(lvl => (
                    <option key={lvl} value={lvl}>{lvl}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <span>Domaines d’intérêt (max 3)</span>
                  <span style={{ fontSize: '0.75rem', opacity: 0.7 }}>{selectedInterests.length}/3 sélectionnés</span>
                </label>
                <div className="interests-grid">
                  {interestDomains.map(domain => (
                    <div 
                      key={domain.id} 
                      className={`interest-tag ${selectedInterests.includes(domain.label) ? 'active' : ''} ${selectedInterests.length >= 3 && !selectedInterests.includes(domain.label) ? 'disabled' : ''}`}
                      onClick={() => toggleInterest(domain.label)}
                    >
                      <span className="tag-label">{domain.label}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="form-group">
                <label>Niveau de compétence</label>
                <select name="niveauCompetence" className="form-input" value={form.niveauCompetence} onChange={handleChange}>
                  {competenceLevels.map(lvl => (
                    <option key={lvl} value={lvl}>{lvl}</option>
                  ))}
                </select>
              </div>

              <div className="form-group">
                <label>Objectif d’apprentissage</label>
                <select name="objectif" className="form-input" value={form.objectif} onChange={handleChange}>
                  {learningGoals.map(goal => (
                    <option key={goal} value={goal}>{goal}</option>
                  ))}
                </select>
              </div>

              <div style={{ display: 'flex', gap: '12px', marginTop: '30px' }}>
                <button type="button" className="btn btn-secondary" onClick={prevStep} style={{ flex: 1 }}>
                  <FiArrowLeft /> Retour
                </button>
                <button type="submit" className="btn btn-primary btn-lg" disabled={loading} style={{ flex: 2 }}>
                  {loading ? 'Inscription...' : 'Terminer l\'inscription'} <FiTarget style={{ marginLeft: '10px' }} />
                </button>
              </div>
            </div>
          )}
        </form>



        <div className="auth-footer" style={{ marginTop: '24px' }}>
          Déjà un compte ? <Link to="/login">Se connecter</Link>
        </div>
      </div>
    </div>
  );
}

