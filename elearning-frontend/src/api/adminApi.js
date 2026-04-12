import api from './axiosConfig';

// Dashboard
export const getAdminDashboard = () =>
  api.get('/admin/dashboard');

// Users
export const getAllUsers = () =>
  api.get('/admin/users');

export const toggleUserActive = (id) =>
  api.put(`/admin/users/${id}/toggle-active`);

export const updateUserRole = (id, role) =>
  api.put(`/admin/users/${id}/role`, { role });

export const deleteUser = (id) =>
  api.delete(`/admin/users/${id}`);

// Courses & Purge
export const deleteAllCourses = () =>
  api.delete('/admin/courses/all');

export const seedMockData = () =>
  api.post('/admin/system-reset-seed');

// Categories
export const getAdminCategories = () =>
  api.get('/admin/categories');

export const createCategory = (data) =>
  api.post('/admin/categories', data);

export const deleteCategory = (id) =>
  api.delete(`/admin/categories/${id}`);
