import api from './axiosConfig';

export const sendMessage = (data) =>
  api.post('/messages/send', data);

export const getConversations = () =>
  api.get('/messages/conversations');

export const getConversationHistory = (contactId) =>
  api.get(`/messages/history/${contactId}`);

export const searchContacts = (query = '') =>
  api.get(`/messages/contacts/search?query=${encodeURIComponent(query)}`);

export const getUnreadCount = () =>
  api.get('/messages/unread-count');
