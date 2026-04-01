import { useState } from 'react';
import { FiSearch } from 'react-icons/fi';

export default function SearchBar({ onSearch, loading }) {
  const [query, setQuery] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    if (query.trim()) {
      onSearch(query.trim());
    }
  };

  return (
    <form onSubmit={handleSubmit} className="search-container">
      <FiSearch className="search-icon" />
      <input
        type="text"
        className="search-input"
        placeholder="Rechercher un cours..."
        value={query}
        onChange={(e) => setQuery(e.target.value)}
        id="search-input"
      />
      <button
        type="submit"
        className="btn btn-primary search-btn"
        disabled={loading || !query.trim()}
        style={{ borderRadius: 100, padding: '12px 24px' }}
      >
        {loading ? (
          <div className="spinner" style={{ width: 18, height: 18, borderWidth: 2 }}></div>
        ) : (
          <>
            <FiSearch size={16} />
            Rechercher
          </>
        )}
      </button>
    </form>
  );
}
