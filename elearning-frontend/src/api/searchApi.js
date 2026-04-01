import api from './axiosConfig';

export const searchCourses = (query) =>
  api.post('/search', { query });

export const getSearchHistory = () =>
  api.get('/search/history');

export const getRecommendations = () =>
  api.get('/recommendations');

export const markRecommendationClicked = (id) =>
  api.post(`/recommendations/${id}/click`);
