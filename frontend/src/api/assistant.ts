import request from './request';
export const chat=(data:{question:string,conversationId?:number})=>request.post('/assistant/chat',data);
