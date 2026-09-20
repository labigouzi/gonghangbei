import request from './request';

export type Profile = {
  id?: number;
  userId?: number;
  age?: number;
  gender?: string;
  retirementStatus?: string;
  monthlyIncome?: string;
  pensionDemand?: string;
  riskPreference?: string;
  investmentExperience?: string;
  digitalFinanceLevel?: string;
  healthStatus?: string;
  familyStructure?: string;
  profileTags?: string;
};

export const getProfile = () => request.get('/profile/me');
export const updateProfile = (data: Profile) => request.put('/profile/me', data);
export const generateProfile = (description: string) => request.post('/profile/generate', { description });
