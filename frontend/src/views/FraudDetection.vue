<template>
  <main class="fraud-page">
    <el-card class="fraud-card">
      <template #header><div class="header"><div><h1>AI金融诈骗风险检测</h1><span>把可疑短信粘贴进来，先停一步再判断</span></div><el-button @click="router.push('/dashboard')">返回首页</el-button></div></template>
      <el-tabs v-model="activeTab" class="tabs">
        <el-tab-pane label="文本检测" name="text"><el-form @submit.prevent="detect"><el-form-item><el-input v-model="content" type="textarea" :rows="6" maxlength="500" show-word-limit placeholder="例如：您的养老金账户异常，请点击链接认证" /></el-form-item><el-button type="primary" size="large" native-type="submit" :loading="loading">开始检测</el-button></el-form></el-tab-pane>
        <el-tab-pane label="图片检测" name="image"><el-upload drag :auto-upload="false" :limit="1" accept="image/*" :on-change="onFileChange" :on-exceed="onExceed"><el-icon class="el-icon--upload"><UploadFilled /></el-icon><div class="el-upload__text">拖入图片，或点击选择图片</div></el-upload><el-button class="image-button" type="primary" size="large" :loading="imageLoading" :disabled="!imageFile" @click="detectImageRisk">识别并检测</el-button><el-alert v-if="ocrContent" class="ocr" title="OCR识别文本" :description="ocrContent" type="info" :closable="false" /></el-tab-pane>
        <el-tab-pane label="检测历史" name="history"><el-empty v-if="!records.length" description="暂无检测记录" /><el-timeline v-else><el-timeline-item v-for="record in records" :key="record.id" :timestamp="record.createdAt"><strong>{{ levelText(record.riskLevel) }} · {{ record.riskScore }}分</strong><p>{{ record.originalContent }}</p></el-timeline-item></el-timeline></el-tab-pane>
      </el-tabs>
      <section v-if="result" class="result" :class="result.riskLevel.toLowerCase()">
        <div class="score-row"><div><span class="label">风险等级</span><strong>{{ levelText(result.riskLevel) }}</strong></div><div><span class="label">风险分数</span><strong>{{ result.riskScore }}</strong><small> / 100</small></div></div>
        <el-divider />
        <h2>风险标签</h2><div class="tags"><el-tag v-for="tag in result.riskTags" :key="tag" size="large" effect="dark">{{ tag }}</el-tag><span v-if="!result.riskTags.length">暂未发现明显标签</span></div>
        <h2>风险原因</h2><p>{{ result.explanation }}</p><h2>安全建议</h2><p>{{ result.suggestion }}</p>
      </section>
      <el-alert class="notice" title="本功能仅作风险提示，不会执行转账或其他资金操作。" type="info" :closable="false" />
    </el-card>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'; import { useRouter } from 'vue-router'; import { ElMessage } from 'element-plus'; import { UploadFilled } from '@element-plus/icons-vue'; import { detectText, detectImage, getFraudRecords, type FraudResult } from '../api/fraud';
const router = useRouter(); const activeTab = ref('text'); const content = ref(''); const loading = ref(false); const imageLoading = ref(false); const imageFile = ref<File>(); const ocrContent = ref(''); const result = ref<FraudResult>(); const records = ref<any[]>([]);
const levelText = (level: FraudResult['riskLevel']) => ({ LOW: '低风险', MEDIUM: '中风险', HIGH: '高风险', CRITICAL: '极高风险' }[level]);
async function detect() { if (!content.value.trim()) { ElMessage.warning('请输入需要检测的文本'); return; } loading.value = true; try { const r: any = await detectText(content.value); result.value = r.data.data; } catch { ElMessage.error('检测失败，请稍后重试'); } finally { loading.value = false; } }
function onFileChange(uploadFile: any) { imageFile.value = uploadFile.raw; result.value = undefined; ocrContent.value = ''; }
function onExceed() { ElMessage.warning('一次只能选择一张图片'); }
async function detectImageRisk() { if (!imageFile.value) return; imageLoading.value = true; try { const r: any = await detectImage(imageFile.value); const data = r.data.data; ocrContent.value = data.ocrContent; result.value = data.result; } catch { ElMessage.error('图片检测失败，请稍后重试'); } finally { imageLoading.value = false; } }
async function loadRecords() { try { const r: any = await getFraudRecords(); records.value = r.data.data || []; } catch { /* 登录失效时由全局请求处理 */ } }
onMounted(loadRecords);
</script>

<style scoped>.fraud-page{min-height:100vh;background:#f4f7fb;padding:24px}.fraud-card{max-width:860px;margin:auto}.header{display:flex;justify-content:space-between;align-items:center}h1{font-size:28px;margin:0 0 6px;color:#1769aa}.header span{font-size:18px;color:#6b7280}.image-button{margin-top:16px}.ocr{margin-top:18px}.result{margin-top:24px;padding:22px;border:2px solid #dbe5ef;border-radius:8px;font-size:20px}.result.high,.result.critical{border-color:#f56c6c;background:#fff7f7}.result.medium{border-color:#e6a23c;background:#fffbf2}.result.low{border-color:#67c23a;background:#f5fff5}.score-row{display:flex;justify-content:space-between;align-items:end}.score-row>div{display:flex;align-items:baseline;gap:10px}.label{color:#667085}.score-row strong{font-size:34px}.result h2{font-size:22px;margin:20px 0 8px}.result p{line-height:1.7;margin:0}.tags{display:flex;gap:8px;flex-wrap:wrap}.notice{margin-top:20px}@media(max-width:600px){.fraud-page{padding:8px}.header{align-items:flex-start}.header h1{font-size:23px}.score-row{align-items:flex-start;gap:16px;flex-direction:column}.score-row strong{font-size:30px}}</style>
