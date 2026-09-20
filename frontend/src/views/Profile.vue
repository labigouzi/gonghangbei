<template>
  <main class="profile-page">
    <el-card class="profile-card">
      <template #header>
        <div class="header"><div><h1>我的养老画像</h1><span>完善信息后，助手会用更适合您的方式回答</span></div><el-button @click="router.push('/dashboard')">返回首页</el-button></div>
      </template>
      <el-form v-loading="loading" :model="form" label-width="150px" size="large">
        <el-form-item label="年龄"><el-input-number v-model="form.age" :min="1" :max="120" /></el-form-item>
        <el-form-item label="退休状态"><el-input v-model="form.retirementStatus" placeholder="例如：已退休" /></el-form-item>
        <el-form-item label="月收入"><el-input v-model="form.monthlyIncome" placeholder="例如：5000-8000" /></el-form-item>
        <el-form-item label="风险偏好"><el-select v-model="form.riskPreference" placeholder="请选择"><el-option label="低风险 / 稳健" value="LOW" /><el-option label="中等风险" value="MEDIUM" /><el-option label="较高风险" value="HIGH" /></el-select></el-form-item>
        <el-form-item label="养老需求"><el-input v-model="form.pensionDemand" type="textarea" :rows="3" placeholder="请描述您最关心的养老问题" /></el-form-item>
        <el-form-item label="数字金融能力"><el-select v-model="form.digitalFinanceLevel" placeholder="请选择"><el-option label="初级" value="BEGINNER" /><el-option label="中级" value="INTERMEDIATE" /><el-option label="熟练" value="ADVANCED" /></el-select></el-form-item>
        <el-form-item><el-button type="primary" :loading="saving" @click="save">保存画像</el-button></el-form-item>
      </el-form>
      <el-divider />
      <el-form label-position="top" @submit.prevent="generate">
        <el-form-item label="让助手从一段话生成画像标签"><el-input v-model="description" type="textarea" :rows="3" placeholder="例如：我今年68岁，退休工资4500，不懂手机银行，希望养老资金安全。" /></el-form-item>
        <el-button type="success" :loading="generating" native-type="submit">AI生成画像</el-button>
      </el-form>
      <el-alert v-if="form.profileTags" class="tags" title="画像标签" :description="form.profileTags" type="info" :closable="false" />
    </el-card>
  </main>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import { getProfile, updateProfile, generateProfile, type Profile } from '../api/profile';

const router = useRouter();
const loading = ref(false); const saving = ref(false); const generating = ref(false); const description = ref('');
const form = reactive<Profile>({});
function fill(data: Profile | null) { if (data) Object.assign(form, data); }
async function load() { loading.value = true; try { const r: any = await getProfile(); fill(r.data.data); } catch { ElMessage.error('画像加载失败，请稍后重试'); } finally { loading.value = false; } }
async function save() { saving.value = true; try { const r: any = await updateProfile(form); fill(r.data.data); ElMessage.success('画像已保存'); } catch { ElMessage.error('保存失败，请稍后重试'); } finally { saving.value = false; } }
async function generate() { if (!description.value.trim()) return; generating.value = true; try { const r: any = await generateProfile(description.value); fill(r.data.data); ElMessage.success('画像标签已生成'); } catch { ElMessage.error('生成失败，请稍后重试'); } finally { generating.value = false; } }
onMounted(load);
</script>

<style scoped>.profile-page{min-height:100vh;background:#f4f7fb;padding:24px}.profile-card{max-width:860px;margin:auto}.header{display:flex;justify-content:space-between;align-items:center}h1{font-size:28px;margin:0 0 6px;color:#1769aa}.header span{font-size:18px;color:#6b7280}.el-select{width:100%}.tags{margin-top:20px}@media(max-width:600px){.profile-page{padding:8px}.header{align-items:flex-start}.header h1{font-size:23px}.el-form-item{margin-bottom:18px}}</style>
