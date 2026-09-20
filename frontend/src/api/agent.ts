import request from './request';

export interface AgentStep { name:string; status:string; summary:string; durationMs:number }
export interface KnowledgeSource { title:string; chunk:string; similarity:number }
export interface AgentResponse {
  answer:string; intent:string; intentConfidence:number; agentChain:AgentStep[];
  financialAnalysis:{
    userProfile:{ageStage:string;riskLevel:string;financialAbility:string;tags:string[]};
    riskAssessment:{score:number;level:string;warnings:string[]};
    allocation:{category:string;amount:number;percentage:number;description:string}[];
    recommendations:{productId:number;productName:string;productType:string;recommendationReason:string;riskNotice:string}[];
    assumptions:string[];
  };
  knowledgeSources:KnowledgeSource[];
}
export const runFinancialAgent=(message:string)=>request.post('/financial/agent/chat',{message});
