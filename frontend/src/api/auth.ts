import request from './request';
export const login=(data:{username:string,password:string})=>request.post('/auth/login',data);
export const me=()=>request.get('/users/me');
