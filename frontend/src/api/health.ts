import request from './request';

export interface HealthDimension { name: string; score: number }
export interface HealthScore { totalScore: number; dimensions: HealthDimension[]; riskWarnings: string[]; disclaimer: string }
export interface FamilySummary { elderName: string; age: number; retirementStatus: string; riskReminders: string[] }

export const getHealthScore = () => request.get<{ data: HealthScore }>('/financial/health-score');
export const getFamilySummary = () => request.get<{ data: FamilySummary }>('/financial/family-summary');
