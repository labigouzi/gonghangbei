<template>
  <main class="health-page">
    <header class="topbar"><div><h1>银龄金融健康分析</h1><p>从资产安全、养老准备、风险控制和医疗保障四个方面了解当前状态</p></div><el-button @click="router.push('/dashboard')">返回首页</el-button></header>
    <el-skeleton v-if="loading" :rows="8" animated />
    <el-alert v-else-if="error" :title="error" type="error" show-icon :closable="false" />
    <template v-else>
      <section class="score-grid">
        <article class="score-card"><span class="eyebrow">金融健康指数</span><strong>{{ score?.totalScore }}<small> / 100</small></strong><p>这是面向养老规划的展示性评分，不代表投资评级。</p></article>
        <article class="chart-card"><h2>四维健康雷达</h2><div ref="radarEl" class="radar"></div></article>
      </section>
      <section class="content-grid">
        <article class="panel"><h2>健康维度</h2><div v-for="item in score?.dimensions" :key="item.name" class="dimension"><div><span>{{ item.name }}</span><b>{{ item.score }}分</b></div><el-progress :percentage="item.score" :show-text="false" :stroke-width="12" /></div><el-divider /><h3>风险提示</h3><ul><li v-for="warning in score?.riskWarnings" :key="warning">{{ warning }}</li></ul></article>
        <article class="panel family"><h2>家庭协同摘要</h2><div class="elder"><div class="avatar">{{ family?.elderName?.slice(0, 1) }}</div><div><strong>{{ family?.elderName }}</strong><span>{{ family?.age }}岁 · {{ family?.retirementStatus }}</span></div></div><h3>家人可以一起关注</h3><ul><li v-for="item in family?.riskReminders" :key="item">{{ item }}</li></ul><el-alert title="陪伴提示" type="info" description="建议家人和老人一起核实重要金融信息，共同保管紧急联系人信息。" :closable="false" /></article>
      </section>
      <p class="disclaimer">{{ score?.disclaimer }}</p>
    </template>
  </main>
</template>

<script setup lang="ts">
import { nextTick, onBeforeUnmount, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';
import { getFamilySummary, getHealthScore, type FamilySummary, type HealthScore } from '../api/health';
const router = useRouter(); const loading = ref(true), error = ref(''), score = ref<HealthScore>(), family = ref<FamilySummary>(); const radarEl = ref<HTMLElement>(); let radar: echarts.ECharts | undefined;
async function load() { loading.value = true; error.value = ''; try { const [health, familyResponse]: any[] = await Promise.all([getHealthScore(), getFamilySummary()]); score.value = health.data.data; family.value = familyResponse.data.data; loading.value = false; await nextTick(); drawRadar(); } catch { error.value = '金融健康数据暂时无法加载，请确认已登录后重试'; ElMessage.error(error.value); loading.value = false; } }
function drawRadar() { if (!radarEl.value || !score.value) return; radar?.dispose(); radar = echarts.init(radarEl.value); radar.setOption({ radar: { indicator: score.value.dimensions.map(item => ({ name: item.name, max: 100 })), radius: '64%', axisName: { color: '#38515b', fontSize: 14 }, splitArea: { areaStyle: { color: ['#f7fbfa', '#eef6f4'] } } }, series: [{ type: 'radar', data: [{ value: score.value.dimensions.map(item => item.score), areaStyle: { color: 'rgba(44,122,85,.22)' }, lineStyle: { color: '#2c7a55', width: 3 }, itemStyle: { color: '#2c7a55' } }] }] }); }
onMounted(load); onBeforeUnmount(() => radar?.dispose());
</script>

<style scoped>
.health-page{min-height:100vh;background:#f3f6f7;color:#23323a;padding:26px}.topbar,.score-grid,.content-grid,.disclaimer{max-width:1200px;margin:0 auto 22px}.topbar{display:flex;justify-content:space-between;align-items:center}.topbar h1{margin:0;color:#125f78;font-size:30px}.topbar p{margin:6px 0;color:#63727a;font-size:18px}.score-grid{display:grid;grid-template-columns:.8fr 1.2fr;gap:22px}.score-card,.chart-card,.panel{background:#fff;border:1px solid #dce5e7;border-radius:8px;padding:24px}.score-card{display:flex;flex-direction:column;justify-content:center;min-height:300px}.eyebrow{color:#63727a;font-size:17px}.score-card strong{color:#2c7a55;font-size:86px;line-height:1.1;margin:18px 0}.score-card strong small{font-size:28px;color:#63727a}.score-card p{color:#63727a;font-size:16px;line-height:1.6}.chart-card h2,.panel h2{margin:0 0 12px;font-size:22px}.radar{height:270px}.content-grid{display:grid;grid-template-columns:1fr 1fr;gap:22px}.dimension{margin:20px 0}.dimension>div{display:flex;justify-content:space-between;margin-bottom:8px;font-size:17px}.dimension b{color:#2c7a55}.panel h3{font-size:18px}.panel ul{padding-left:22px;line-height:2;color:#76551d}.elder{display:flex;align-items:center;gap:15px;padding:16px;background:#f2f8f7}.avatar{width:54px;height:54px;border-radius:50%;display:grid;place-items:center;background:#2c7a55;color:#fff;font-size:28px}.elder strong,.elder span{display:block}.elder span{color:#63727a;margin-top:5px}.disclaimer{text-align:center;color:#63727a;font-size:14px}@media(max-width:800px){.health-page{padding:12px}.topbar{align-items:flex-start}.topbar h1{font-size:24px}.score-grid,.content-grid{grid-template-columns:1fr}.score-card{min-height:220px}.score-card strong{font-size:64px}}
</style>
