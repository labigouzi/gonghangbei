<template>
  <main class="agent-page">
    <header class="topbar"><div><h1>AI银龄金融智能体</h1><p>多智能体协作完成养老金融分析</p></div><el-button @click="router.push('/dashboard')">返回首页</el-button></header>
    <section class="question-band">
      <el-input v-model="message" type="textarea" :rows="3" maxlength="300" show-word-limit placeholder="例如：我68岁，有50万存款，每月退休金6000元，退休后如何养老？" />
      <el-button type="primary" size="large" :loading="loading" @click="submit">运行AI金融智能体</el-button>
    </section>

    <section v-if="loading" class="chain-band">
      <div class="section-title"><div><h2>Agent处理链</h2><p>正在协同分析您的养老金融需求</p></div><el-tag size="large">执行中</el-tag></div>
      <AgentExecutionFlow :nodes="flowNodes" :active-index="flowActiveIndex" :running="true" />
    </section>

    <template v-if="result">
      <section class="chain-band">
        <div class="section-title"><div><h2>Agent处理链</h2><p>意图置信度 {{ Math.round(result.intentConfidence*100) }}% · {{ intentText }}</p></div><el-tag type="success" size="large">全部完成</el-tag></div>
        <AgentExecutionFlow :nodes="flowNodes" :active-index="flowActiveIndex" :running="loading" />
      </section>

      <section class="analysis-band">
        <div class="risk-panel"><h2>风险评估</h2><div ref="gaugeEl" class="gauge"></div><div class="warnings"><p v-for="warning in result.financialAnalysis.riskAssessment.warnings" :key="warning">{{ warning }}</p></div></div>
        <div class="profile-panel"><h2>金融画像</h2><dl><div><dt>养老阶段</dt><dd>{{ result.financialAnalysis.userProfile.ageStage }}</dd></div><div><dt>风险等级</dt><dd>{{ riskText }}</dd></div><div><dt>金融能力</dt><dd>{{ abilityText }}</dd></div></dl><div class="tags"><el-tag v-for="tag in result.financialAnalysis.userProfile.tags" :key="tag">{{ tag }}</el-tag></div><el-alert v-if="result.financialAnalysis.assumptions.length" title="本次分析采用了演示假设" type="info" :description="result.financialAnalysis.assumptions.join('；')" :closable="false" /></div>
      </section>

      <section class="allocation-band"><h2>资金规划</h2><div class="allocation-grid"><div v-for="item in result.financialAnalysis.allocation" :key="item.category"><strong>{{ item.percentage }}%</strong><span>{{ item.category }}</span><p>¥ {{ money(item.amount) }}</p><small>{{ item.description }}</small></div></div></section>

      <section class="products-band"><h2>产品匹配</h2><div class="product-grid"><article v-for="item in result.financialAnalysis.recommendations" :key="item.productId"><header><strong>{{ item.productName }}</strong><el-tag type="info">{{ item.productType }}</el-tag></header><p>{{ item.recommendationReason }}</p><small>{{ item.riskNotice }}</small></article></div></section>

      <section class="report-band"><h2>智能报告</h2><p>{{ result.answer }}</p></section>
      <section class="knowledge-band"><h2>RAG知识来源</h2><div class="source-list"><article v-for="source in result.knowledgeSources" :key="source.title+source.chunk"><header><strong>{{ source.title }}</strong><span>匹配度 {{ Math.round(source.similarity*100) }}%</span></header><p>{{ source.chunk }}</p></article></div></section>
    </template>
  </main>
</template>

<script setup lang="ts">
import {computed,nextTick,onBeforeUnmount,ref} from 'vue';import {useRouter} from 'vue-router';import {ElMessage} from 'element-plus';import {Search,User,WarningFilled,Wallet,OfficeBuilding,Document} from '@element-plus/icons-vue';import * as echarts from 'echarts';import {runFinancialAgent,type AgentResponse} from '../api/agent';import AgentExecutionFlow from '../components/AgentExecutionFlow.vue';
const router=useRouter(),message=ref('我68岁，有50万存款，每月退休金6000元，退休后如何养老？'),loading=ref(false),result=ref<AgentResponse>(),gaugeEl=ref<HTMLElement>();let gauge:echarts.ECharts|undefined;
const nodeIcons=[Search,User,WarningFilled,Wallet,OfficeBuilding,Document],nodeTitles=['意图理解','用户画像','风险评估','资金规划','产品匹配','智能报告'];const flowActiveIndex=ref(6);const flowNodes=computed(()=>nodeTitles.map((title,index)=>({title,icon:nodeIcons[index],summary:result.value?.agentChain[index]?.summary||'等待运行'})));
const intentText=computed(()=>({RETIREMENT_PLAN:'养老规划',FINANCIAL_CONSULTATION:'金融咨询',RISK_CONSULTATION:'风险咨询',FRAUD_DETECTION:'诈骗识别',TRAVEL_RETIREMENT:'旅游养老'}[result.value?.intent||'']||result.value?.intent));
const riskText=computed(()=>({LOW:'低风险',MEDIUM:'稳健风险',HIGH:'较高风险'}[result.value?.financialAnalysis.riskAssessment.level||'LOW']));
const abilityText=computed(()=>({LOW:'基础',MEDIUM:'中等',HIGH:'较强'}[result.value?.financialAnalysis.userProfile.financialAbility||'MEDIUM']));
async function submit(){if(!message.value.trim())return ElMessage.warning('请输入您的问题');loading.value=true;result.value=undefined;flowActiveIndex.value=0;const timer=window.setInterval(()=>{if(flowActiveIndex.value<5)flowActiveIndex.value++},260);try{const response:any=await runFinancialAgent(message.value);result.value=response.data.data;flowActiveIndex.value=6;await nextTick();drawGauge()}catch{flowActiveIndex.value=0;ElMessage.error('智能体暂时无法完成分析，请稍后重试')}finally{window.clearInterval(timer);loading.value=false}}
function drawGauge(){if(!gaugeEl.value||!result.value)return;gauge?.dispose();gauge=echarts.init(gaugeEl.value);gauge.setOption({series:[{type:'gauge',min:0,max:100,startAngle:210,endAngle:-30,progress:{show:true,width:16,itemStyle:{color:'#2c7a55'}},axisLine:{lineStyle:{width:16,color:[[.3,'#2c7a55'],[.6,'#c58b22'],[1,'#b6423c']]}},axisTick:{show:false},splitLine:{length:10,distance:2},axisLabel:{distance:22,fontSize:12},pointer:{width:5,length:'58%'},detail:{valueAnimation:true,fontSize:30,formatter:'{value}分',offsetCenter:[0,'68%']},data:[{value:result.value.financialAnalysis.riskAssessment.score}]}]});}
function money(value:number){return new Intl.NumberFormat('zh-CN',{maximumFractionDigits:0}).format(value)}const resize=()=>gauge?.resize();window.addEventListener('resize',resize);onBeforeUnmount(()=>{window.removeEventListener('resize',resize);gauge?.dispose()});
</script>

<style scoped>
.agent-page{min-height:100vh;background:#f3f6f7;color:#23323a;padding:26px}.topbar,.question-band,.chain-band,.analysis-band,.allocation-band,.products-band,.report-band,.knowledge-band{max-width:1200px;margin:0 auto 22px}.topbar{display:flex;justify-content:space-between;align-items:center}.topbar h1{margin:0;color:#125f78;font-size:30px}.topbar p{margin:6px 0;color:#63727a;font-size:18px}.question-band{display:grid;grid-template-columns:1fr 220px;gap:16px;align-items:end;background:#fff;border:1px solid #dce5e7;border-radius:8px;padding:20px}.question-band .el-button{height:48px;font-size:17px}.chain-band,.allocation-band,.products-band,.report-band,.knowledge-band{background:#fff;border:1px solid #dce5e7;padding:22px}.section-title{display:flex;justify-content:space-between;align-items:center}.section-title h2,h2{font-size:22px;margin:0 0 8px}.section-title p{margin:0;color:#63727a}.agent-chain{display:flex;align-items:stretch;margin-top:22px;overflow-x:auto;padding-bottom:8px}.agent-node{width:154px;min-width:154px;min-height:150px;border:1px solid #cbdadd;border-top:4px solid #2c7a55;padding:15px 12px;display:flex;flex-direction:column;align-items:center;text-align:center;animation:node-in .38s both}.agent-node .icon{width:38px;height:38px;color:#146c85}.agent-node .icon :deep(svg){width:100%;height:100%}.agent-node strong{margin-top:8px;font-size:17px}.agent-node span{font-size:13px;color:#63727a;line-height:1.4;margin-top:7px}.agent-node small{margin-top:auto;color:#2c7a55}.connector{display:grid;place-items:center;width:28px;min-width:28px;font-size:22px;color:#72909a}.analysis-band{display:grid;grid-template-columns:1fr 1fr;gap:22px}.risk-panel,.profile-panel{background:#fff;border:1px solid #dce5e7;padding:22px}.gauge{height:290px}.warnings p{margin:8px 0;color:#8d5611}.profile-panel dl{display:grid;gap:0;margin:18px 0}.profile-panel dl div{display:flex;justify-content:space-between;padding:13px 0;border-bottom:1px solid #e4eaec}.profile-panel dt{color:#667780}.profile-panel dd{font-weight:700;margin:0}.tags{display:flex;gap:8px;flex-wrap:wrap;margin-bottom:18px}.allocation-grid,.product-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:14px;margin-top:18px}.allocation-grid>div{border-left:5px solid #167692;background:#f3f8f9;padding:16px}.allocation-grid strong{font-size:28px;color:#126782;display:block}.allocation-grid span{font-size:18px;font-weight:700}.allocation-grid p{margin:8px 0}.allocation-grid small{line-height:1.5;color:#63727a}.product-grid article{border:1px solid #dce5e7;padding:16px}.product-grid header,.source-list header{display:flex;justify-content:space-between;gap:10px}.product-grid p,.source-list p{line-height:1.6}.product-grid small{color:#8d5611;line-height:1.5}.report-band{border-left:5px solid #2c7a55}.report-band>p{white-space:pre-line;font-size:18px;line-height:1.9}.source-list{display:grid;gap:12px;margin-top:16px}.source-list article{background:#f4f7f8;padding:15px}.source-list header span{color:#2c7a55;font-weight:700}.source-list p{margin:8px 0 0}@keyframes node-in{from{opacity:0;transform:translateY(10px)}to{opacity:1;transform:none}}@media(max-width:800px){.agent-page{padding:12px}.topbar{align-items:flex-start}.topbar h1{font-size:24px}.question-band{grid-template-columns:1fr}.analysis-band,.allocation-grid,.product-grid{grid-template-columns:1fr}.agent-chain{flex-direction:column}.agent-node{width:auto;min-width:0}.connector{height:28px;width:100%;transform:rotate(90deg)}}
</style>
