import { createContext, useContext, useState, useEffect } from 'react';

const ThemeContext = createContext(null);

// Three themes: dark (default), blue, light
const THEMES = ['dark', 'blue', 'light'];

export function ThemeProvider({ children }) {
  const [theme, setTheme] = useState(() => {
    return localStorage.getItem('learnagent-theme') || 'dark';
  });

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem('learnagent-theme', theme);
  }, [theme]);

  const cycleTheme = () => {
    const idx = THEMES.indexOf(theme);
    setTheme(THEMES[(idx + 1) % THEMES.length]);
  };

  const setThemeDirect = (t) => {
    if (THEMES.includes(t)) setTheme(t);
  };

  return (
    <ThemeContext.Provider value={{ theme, cycleTheme, setThemeDirect, THEMES }}>
      {children}
    </ThemeContext.Provider>
  );
}

export const useTheme = () => {
  const ctx = useContext(ThemeContext);
  if (!ctx) throw new Error('useTheme must be used within ThemeProvider');
  return ctx;
};
