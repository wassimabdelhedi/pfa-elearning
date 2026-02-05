import { Routes, Route, Navigate } from 'react-router-dom'
import Layout from './components/Layout'
import ProtectedRoute from './components/ProtectedRoute'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import CourseList from './pages/CourseList'
import CourseDetail from './pages/CourseDetail'
import Recommendations from './pages/Recommendations'
import MyCourses from './pages/MyCourses'

export default function App() {
  return (
    <Layout>
      <Routes>
        <Route path="/" element={<Navigate to="/courses" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/courses" element={<CourseList />} />
        <Route path="/courses/:id" element={<CourseDetail />} />
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/recommendations" element={<ProtectedRoute><Recommendations /></ProtectedRoute>} />
        <Route path="/my-courses" element={<ProtectedRoute roles={['TEACHER']}><MyCourses /></ProtectedRoute>} />
        <Route path="*" element={<Navigate to="/courses" replace />} />
      </Routes>
    </Layout>
  )
}
