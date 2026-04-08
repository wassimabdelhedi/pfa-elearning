import api from './axiosConfig';

export const getPublishedQuizzes = () =>
  api.get('/quizzes');

export const getQuizById = (id) =>
  api.get(`/quizzes/${id}`);

export const getMyTeacherQuizzes = () =>
  api.get('/quizzes/my-quizzes');

export const createQuiz = (data) =>
  api.post('/quizzes', data);

export const deleteQuiz = (id) =>
  api.delete(`/quizzes/${id}`);

export const togglePublishQuiz = (id) =>
  api.patch(`/quizzes/${id}/toggle-publish`);

export const submitQuizResult = (quizId, data) =>
  api.post(`/quizzes/${quizId}/submit`, data);

export const getQuizResults = (quizId) =>
  api.get(`/quizzes/${quizId}/results`);

export const getMyQuizResults = () =>
  api.get('/quizzes/my-results');

export const getMyStudentResults = () =>
  api.get('/quizzes/my-student-results');
