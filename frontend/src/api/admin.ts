import request from './request';
export const getOverview = (params?: { startDate?: string; endDate?: string }) => request.get('/admin/dashboard/overview', { params });
export const getFraudStatistics = () => request.get('/admin/fraud/statistics');
export const getProfileStatistics = () => request.get('/admin/profile/statistics');
export const getFraudTrend = (params?: { startDate?: string; endDate?: string }) => request.get('/admin/fraud/trend', { params });
