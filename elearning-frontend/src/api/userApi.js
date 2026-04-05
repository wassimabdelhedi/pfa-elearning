import api from './axiosConfig';

export const enrollInCourse = (courseId) =>
  api.post(`/enrollments/${courseId}`);

export const getMyEnrollments = () =>
  api.get('/enrollments');

export const updateProgress = (enrollmentId, progress) =>
  api.put(`/enrollments/${enrollmentId}/progress`, { progress });

export const updateProgressByCourse = (courseId, progress) =>
  api.put(`/enrollments/course/${courseId}/progress`, { progress });

export const getCategories = () =>
  api.get('/categories');
