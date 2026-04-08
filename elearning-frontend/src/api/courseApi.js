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

// ========== Chapter API ==========

export const getChapters = (courseId) =>
  api.get(`/chapters/course/${courseId}`);

export const addChapter = (courseId, formData) =>
  api.post(`/chapters/course/${courseId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const updateChapter = (chapterId, formData) =>
  api.put(`/chapters/${chapterId}`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });

export const deleteChapter = (chapterId) =>
  api.delete(`/chapters/${chapterId}`);

export const downloadChapterFile = (chapterId) =>
  api.get(`/chapters/${chapterId}/download`, { responseType: 'blob' });

export const markChapterComplete = (chapterId) =>
  api.put(`/chapters/${chapterId}/complete`);

export const getCourseChapterProgress = (courseId) =>
  api.get(`/chapters/course/${courseId}/progress`);
