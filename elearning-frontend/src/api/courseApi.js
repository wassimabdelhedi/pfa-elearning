import api from './axiosConfig';

export const getPublishedCourses = () =>
  api.get('/courses');

export const getCourseById = (id) =>
  api.get(`/courses/${id}`);

export const getCoursesByCategory = (categoryId) =>
  api.get(`/courses/category/${categoryId}`);

export const getMyTeacherCourses = () =>
  api.get('/courses/my-courses');

export const createCourse = (formData) =>
  api.post('/courses', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const updateCourse = (id, formData) =>
  api.put(`/courses/${id}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const deleteCourse = (id) =>
  api.delete(`/courses/${id}`);

export const downloadCourse = (id) =>
  api.get(`/courses/${id}/download`, { responseType: 'blob' });
