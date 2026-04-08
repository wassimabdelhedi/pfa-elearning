import { Routes, Route, Navigate } from 'react-router-dom';
import { useAuth } from './context/AuthContext';
import Navbar from './components/common/Navbar';
import ProtectedRoute from './components/common/ProtectedRoute';

// Auth pages
import LoginPage from './pages/auth/LoginPage';
import RegisterPage from './pages/auth/RegisterPage';

// Student pages
import Dashboard from './pages/student/Dashboard';
import SearchPage from './pages/student/SearchPage';
import CoursesPage from './pages/student/CoursesPage';
import CourseViewPage from './pages/student/CourseViewPage';
import ExercisesPage from './pages/student/ExercisesPage';
import QuizPage from './pages/student/QuizPage';

// Teacher pages
import TeacherDashboard from './pages/teacher/TeacherDashboard';
import CreateCourse from './pages/teacher/CreateCourse';
import ManageCourseChapters from './pages/teacher/ManageCourseChapters';
import CreateExercise from './pages/teacher/CreateExercise';
import CreateQuiz from './pages/teacher/CreateQuiz';

function App() {
  const { isAuthenticated, user } = useAuth();

  const getDefaultRoute = () => {
    if (!isAuthenticated) return '/login';
    if (user?.role === 'TEACHER') return '/teacher/dashboard';
    if (user?.role === 'ADMIN') return '/admin';
    return '/dashboard';
  };

  return (
    <>
      <Navbar />
      <Routes>
        {/* Public routes */}
        <Route path="/login" element={
          isAuthenticated ? <Navigate to={getDefaultRoute()} replace /> : <LoginPage />
        } />
        <Route path="/register" element={
          isAuthenticated ? <Navigate to={getDefaultRoute()} replace /> : <RegisterPage />
        } />

        {/* Student routes */}
        <Route path="/dashboard" element={
          <ProtectedRoute roles={['STUDENT']}>
            <Dashboard />
          </ProtectedRoute>
        } />
        <Route path="/search" element={
          <ProtectedRoute roles={['STUDENT']}>
            <SearchPage />
          </ProtectedRoute>
        } />
        <Route path="/courses" element={
          <ProtectedRoute roles={['STUDENT']}>
            <CoursesPage />
          </ProtectedRoute>
        } />
        <Route path="/course/:id" element={
          <ProtectedRoute roles={['STUDENT', 'TEACHER']}>
            <CourseViewPage />
          </ProtectedRoute>
        } />
        <Route path="/exercises" element={
          <ProtectedRoute roles={['STUDENT']}>
            <ExercisesPage />
          </ProtectedRoute>
        } />
        <Route path="/exercises/:id" element={
          <ProtectedRoute roles={['STUDENT']}>
            <ExercisesPage />
          </ProtectedRoute>
        } />
        <Route path="/quiz" element={
          <ProtectedRoute roles={['STUDENT']}>
            <QuizPage />
          </ProtectedRoute>
        } />
        <Route path="/quiz/:id" element={
          <ProtectedRoute roles={['STUDENT']}>
            <QuizPage />
          </ProtectedRoute>
        } />

        {/* Teacher routes */}
        <Route path="/teacher/dashboard" element={
          <ProtectedRoute roles={['TEACHER']}>
            <TeacherDashboard />
          </ProtectedRoute>
        } />
        <Route path="/teacher/create" element={
          <ProtectedRoute roles={['TEACHER']}>
            <CreateCourse />
          </ProtectedRoute>
        } />
        <Route path="/teacher/course/:courseId/chapters" element={
          <ProtectedRoute roles={['TEACHER']}>
            <ManageCourseChapters />
          </ProtectedRoute>
        } />
        <Route path="/teacher/courses" element={
          <ProtectedRoute roles={['TEACHER']}>
            <TeacherDashboard />
          </ProtectedRoute>
        } />
        <Route path="/teacher/create-exercise" element={
          <ProtectedRoute roles={['TEACHER']}>
            <CreateExercise />
          </ProtectedRoute>
        } />
        <Route path="/teacher/create-quiz" element={
          <ProtectedRoute roles={['TEACHER']}>
            <CreateQuiz />
          </ProtectedRoute>
        } />

        {/* Default redirect */}
        <Route path="/" element={<Navigate to={getDefaultRoute()} replace />} />
        <Route path="*" element={<Navigate to={getDefaultRoute()} replace />} />
      </Routes>
    </>
  );
}

export default App;
