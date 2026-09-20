import axios from 'axios';
const request=axios.create({baseURL:'/api/v1',timeout:10000});
request.interceptors.request.use(c=>{const t=localStorage.getItem('token');if(t)c.headers.Authorization=`Bearer ${t}`;return c});
export default request;
