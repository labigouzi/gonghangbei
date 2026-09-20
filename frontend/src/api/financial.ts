import request from './request';

export interface FinancialPlanRequest {
  age: number;
  monthlyIncome: number;
  assetAmount: number;
  riskPreference: string;
  retirementGoal: string;
  medicalNeed?: string;
  travelNeed?: string;
}

export interface Allocation { category: string; amount: number; percentage: number; description: string }
export interface Recommendation { productId: number; productName: string; productType: string; recommendationReason: string; riskNotice: string }
export interface FinancialPlanResponse {
  planId: number; riskLevel: string; retirementStage: string; summary: string;
  allocation: Allocation[]; recommendations: Recommendation[]; report: string;
  riskNotice: string; financialChain: string[];
}

export const generateFinancialPlan = (data: FinancialPlanRequest) => request.post('/financial/plan', data);
