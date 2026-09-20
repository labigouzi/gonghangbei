import request from './request';

export type FraudResult = {
  riskLevel: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  riskScore: number;
  riskTags: string[];
  explanation: string;
  suggestion: string;
};

export const detectText = (content: string) => request.post('/fraud/detect/text', { content });
export const detectImage = (file: File) => { const data = new FormData(); data.append('file', file); return request.post('/fraud/detect/image', data, { headers: { 'Content-Type': 'multipart/form-data' } }); };
export const getFraudRecords = () => request.get('/fraud/records');
