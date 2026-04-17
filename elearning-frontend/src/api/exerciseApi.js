import api from './axiosConfig';

export const getPublishedExercises = () =>
  api.get('/exercises');

export const getExerciseById = (id) =>
  api.get(`/exercises/${id}`);

export const getExercisesByCourse = (courseId) =>
  api.get(`/exercises/course/${courseId}`);

export const getMyTeacherExercises = () =>
  api.get('/exercises/my-exercises');

export const createExercise = (formData) =>
  api.post('/exercises', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const deleteExercise = (id) =>
  api.delete(`/exercises/${id}`);

export const togglePublishExercise = (id) =>
  api.patch(`/exercises/${id}/toggle-publish`);

export const downloadExercise = (id) =>
  api.get(`/exercises/${id}/download`, { responseType: 'blob' });

export const completeExercise = (id) =>
  api.post(`/exercises/${id}/complete`);

export const getMyCompletedExercises = () =>
  api.get('/exercises/my-completed-exercises');
