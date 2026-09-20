import request from './request';
export const getAiStatus = () => request.get('/ai/status');
