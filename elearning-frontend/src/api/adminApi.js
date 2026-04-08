import api from './axiosConfig';

// Dashboard
export const getAdminDashboard = () =>
  api.get('/admin/dashboard');

// Users
export const getAllUsers = () =>
  api.get('/admin/users');

export const toggleUserActive = (id) =>
  api.put(`/admin/users/${id}/toggle-active`);

export const deleteUser = (id) =>
  api.delete(`/admin/users/${id}`);

// Categories
export const getAdminCategories = () =>
  api.get('/admin/categories');

export const createCategory = (data) =>
  api.post('/admin/categories', data);

export const deleteCategory = (id) =>
  api.delete(`/admin/categories/${id}`);
