import api from './axiosConfig';

export const getPublishedExercises = () =>
  api.get('/exercises');

export const getExerciseById = (id) =>
  api.get(`/exercises/${id}`);

export const getMyTeacherExercises = () =>
  api.get('/exercises/my-exercises');

export const createExercise = (formData) =>
  api.post('/exercises', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const deleteExercise = (id) =>
  api.delete(`/exercises/${id}`);
