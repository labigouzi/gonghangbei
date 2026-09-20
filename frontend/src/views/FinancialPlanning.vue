<template>
  <main class="planning-page">
    <header class="topbar">
      <div><h1>AI养老金融规划</h1><p>先保障生活，再从容安排养老资金</p></div>
      <el-button @click="router.push('/dashboard')">返回首页</el-button>
    </header>

    <section class="workspace">
      <div class="form-panel">
        <h2>填写您的情况</h2>
        <el-form label-position="top" size="large" @submit.prevent="submit">
          <div class="form-grid">
            <el-form-item label="年龄"><el-input-number v-model="form.age" :min="40" :max="100" controls-position="right" /></el-form-item>
            <el-form-item label="每月退休收入（元）"><el-input-number v-model="form.monthlyIncome" :min="0" :step="500" controls-position="right" /></el-form-item>
            <el-form-item label="可规划资产（元）"><el-input-number v-model="form.assetAmount" :min="0" :step="10000" controls-position="right" /></el-form-item>
            <el-form-item label="您希望资金更安全，还是接受一定波动">
              <el-segmented v-model="form.riskPreference" :options="['稳健','平衡','激进']" />
            </el-form-item>
          </div>
          <el-form-item label="养老目标"><el-input v-model="form.retirementGoal" placeholder="例如：健康养老、保障日常生活" /></el-form-item>
          <div class="form-grid optional">
            <el-form-item label="医疗需求（选填）"><el-input v-model="form.medicalNeed" placeholder="例如：慢病复诊便利" /></el-form-item>
            <el-form-item label="旅行需求（选填）"><el-input v-model="form.travelNeed" placeholder="例如：每年旅行2次" /></el-form-item>
          </div>
          <el-button class="submit" type="primary" native-type="submit" :loading="loading">生成养老金融规划</el-button>
        </el-form>
      </div>

      <div v-if="result" class="result-panel">
        <section class="overview">
          <div><span>养老阶段</span><strong>{{ result.retirementStage }}</strong></div>
          <div><span>风险等级</span><strong :class="riskClass">{{ riskText }}</strong></div>
          <p>{{ result.summary }}</p>
        </section>

        <section class="chain"><h2>AI顾问工作链</h2><el-steps :active="5" finish-status="success" align-center><el-step v-for="step in result.financialChain" :key="step" :title="step" /></el-steps></section>

        <section class="allocation-section">
          <div ref="chartEl" class="chart" aria-label="养老资金分配图"></div>
          <div class="allocation-list"><h2>资金用途建议</h2><div v-for="item in result.allocation" :key="item.category"><span class="swatch"></span><div><strong>{{ item.category }} · {{ item.percentage }}%</strong><p>¥ {{ money(item.amount) }} · {{ item.description }}</p></div></div></div>
        </section>

        <section class="report"><h2>AI养老规划报告</h2><p>{{ result.report }}</p></section>

        <section class="products"><h2>模拟金融产品建议</h2><div class="product-grid"><article v-for="item in result.recommendations" :key="item.productId"><div><strong>{{ item.productName }}</strong><el-tag type="info">{{ item.productType }}</el-tag></div><p>{{ item.recommendationReason }}</p><small>{{ item.riskNotice }}</small></article></div></section>
        <el-alert :title="result.riskNotice" type="warning" show-icon :closable="false" />
      </div>
    </section>
  </main>
</template>

<script setup lang="ts">
import {computed, nextTick, onBeforeUnmount, reactive, ref} from 'vue';
import {useRouter} from 'vue-router';
import {ElMessage} from 'element-plus';
import * as echarts from 'echarts';
import {generateFinancialPlan, type FinancialPlanResponse} from '../api/financial';

const router=useRouter(),loading=ref(false),result=ref<FinancialPlanResponse>(),chartEl=ref<HTMLElement>();
const form=reactive({age:68,monthlyIncome:6000,assetAmount:500000,riskPreference:'稳健',retirementGoal:'健康养老',medicalNeed:'',travelNeed:''});
let chart:echarts.ECharts|undefined;
const riskText=computed(()=>({LOW:'低风险',MEDIUM:'中等风险',HIGH:'高风险'}[result.value?.riskLevel||'LOW']));
const riskClass=computed(()=>`risk-${(result.value?.riskLevel||'LOW').toLowerCase()}`);
async function submit(){loading.value=true;try{const response:any=await generateFinancialPlan({...form,medicalNeed:form.medicalNeed||undefined,travelNeed:form.travelNeed||undefined});result.value=response.data.data;await nextTick();drawChart()}catch{ElMessage.error('规划暂时无法生成，请稍后重试')}finally{loading.value=false}}
function drawChart(){if(!chartEl.value||!result.value)return;chart?.dispose();chart=echarts.init(chartEl.value);chart.setOption({tooltip:{trigger:'item',formatter:'{b}<br/>¥ {c}（{d}%）'},legend:{bottom:4},series:[{type:'pie',radius:['45%','70%'],center:['50%','43%'],label:{fontSize:15,formatter:'{b}\n{d}%'},data:result.value.allocation.map((item,index)=>({name:item.category,value:item.amount,itemStyle:{color:['#1677a3','#2f7d4a','#d18b24'][index]}}))}]});}
function money(value:number){return new Intl.NumberFormat('zh-CN',{maximumFractionDigits:2}).format(value)}
const resize=()=>chart?.resize();window.addEventListener('resize',resize);onBeforeUnmount(()=>{window.removeEventListener('resize',resize);chart?.dispose()});
</script>

<style scoped>
.planning-page{min-height:100vh;background:#f3f6f7;color:#24313a;padding:28px}.topbar,.workspace{max-width:1180px;margin:auto}.topbar{display:flex;align-items:center;justify-content:space-between;margin-bottom:22px}.topbar h1{font-size:30px;margin:0;color:#125f78}.topbar p{font-size:18px;margin:6px 0 0;color:#62717b}.workspace{display:grid;gap:24px}.form-panel,.result-panel{background:#fff;border:1px solid #dce4e7;border-radius:8px;padding:24px}.form-panel h2,.result-panel h2{font-size:22px;margin:0 0 18px}.form-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:0 24px}.form-panel :deep(.el-input-number),.form-panel :deep(.el-segmented){width:100%}.submit{width:100%;height:48px;font-size:18px}.result-panel{display:grid;gap:28px}.overview{display:grid;grid-template-columns:180px 180px 1fr;gap:16px;align-items:center;border-left:5px solid #1677a3;padding:14px 18px;background:#f1f8fa}.overview div{display:grid;gap:4px}.overview span{color:#64747e}.overview strong{font-size:21px}.overview p{font-size:17px;line-height:1.6;margin:0}.risk-low{color:#287a45}.risk-medium{color:#a16400}.risk-high{color:#b42318}.chain{overflow-x:auto;padding-bottom:8px}.allocation-section{display:grid;grid-template-columns:minmax(360px,1fr) 1fr;gap:28px;align-items:center}.chart{height:340px;min-width:0}.allocation-list>div{display:flex;gap:12px;margin:18px 0}.allocation-list p{margin:5px 0;color:#63727c;line-height:1.5}.swatch{width:11px;height:36px;background:#1677a3}.allocation-list>div:nth-of-type(3) .swatch{background:#2f7d4a}.allocation-list>div:nth-of-type(4) .swatch{background:#d18b24}.report{border-top:1px solid #dce4e7;padding-top:24px}.report p{white-space:pre-line;font-size:18px;line-height:1.9}.product-grid{display:grid;grid-template-columns:repeat(2,minmax(0,1fr));gap:14px}.product-grid article{border:1px solid #dce4e7;border-radius:6px;padding:16px}.product-grid article>div{display:flex;justify-content:space-between;gap:12px}.product-grid strong{font-size:18px}.product-grid p{line-height:1.6}.product-grid small{color:#9a5b08;line-height:1.5}.result-panel :deep(.el-step__title){font-size:16px}@media(max-width:760px){.planning-page{padding:12px}.topbar{align-items:flex-start}.topbar h1{font-size:24px}.form-grid,.allocation-section,.product-grid{grid-template-columns:1fr}.overview{grid-template-columns:1fr 1fr}.overview p{grid-column:1/-1}.chart{height:300px}.form-panel,.result-panel{padding:16px}}
</style>
