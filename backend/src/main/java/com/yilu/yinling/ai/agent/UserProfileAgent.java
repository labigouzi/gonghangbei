package com.yilu.yinling.ai.agent;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yilu.yinling.ai.agent.model.AgentModels.FinancialProfile;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.entity.ElderlyFinancialPlan;
import com.yilu.yinling.financial.mapper.ElderlyFinancialPlanMapper;
import com.yilu.yinling.profile.service.ProfileService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.regex.Pattern;

@Component
public class UserProfileAgent {
    private final ObjectProvider<ProfileService> profiles; private final ObjectProvider<ElderlyFinancialPlanMapper> plans;
    public UserProfileAgent(ObjectProvider<ProfileService> profiles,ObjectProvider<ElderlyFinancialPlanMapper> plans){this.profiles=profiles;this.plans=plans;}
    public void enrich(AgentContext c){
        Integer age=integer(c.message,"(\\d{2})\\s*岁"); BigDecimal asset=money(c.message,"(?:有|资产|存款)[约大概]?\\s*(\\d+(?:\\.\\d+)?)\\s*(万|元)");
        BigDecimal income=money(c.message,"(?:退休金|养老金|收入)[约大概]?\\s*(?:每月|月)?\\s*(\\d+(?:\\.\\d+)?)\\s*(万|元)?");
        String preference=c.message.contains("激进")?"激进":c.message.contains("平衡")?"平衡":c.message.contains("稳健")?"稳健":null;
        var profile=profiles.getIfAvailable()==null?null:profiles.getIfAvailable().get(c.userId);
        ElderlyFinancialPlan old=null; var mapper=plans.getIfAvailable(); if(mapper!=null){var list=mapper.selectList(new QueryWrapper<ElderlyFinancialPlan>().eq("user_id",c.userId).orderByDesc("created_at").last("LIMIT 1"));if(!list.isEmpty())old=list.get(0);}
        if(age==null&&profile!=null)age=profile.age(); if(age==null&&old!=null)age=old.getAge(); if(age==null){age=68;c.assumptions.add("未提供年龄，演示按68岁计算");}
        if(asset==null&&old!=null)asset=old.getAssetAmount(); if(asset==null){asset=new BigDecimal("500000");c.assumptions.add("未提供资产，演示按50万元计算");}
        if(income==null&&profile!=null)income=parseNumber(profile.monthlyIncome()); if(income==null&&old!=null)income=old.getMonthlyIncome(); if(income==null){income=new BigDecimal("6000");c.assumptions.add("未提供月收入，演示按6000元计算");}
        if(preference==null&&profile!=null)preference=profile.riskPreference(); if(preference==null&&old!=null)preference=old.getRiskPreference(); if(preference==null){preference="稳健";c.assumptions.add("未提供风险偏好，演示按稳健计算");}
        c.request=new FinancialPlanRequest(age,income,asset,preference,"安心养老",null,c.intent!=null&&"TRAVEL_RETIREMENT".equals(c.intent.intent())?"关注适老旅行":"");
        String stage=age>=70?"养老保障阶段":age>=60?"退休初期":"退休准备阶段"; String level=preference.contains("激进")?"HIGH":preference.contains("平衡")?"MEDIUM":"LOW";
        String ability=income.compareTo(new BigDecimal("8000"))>=0?"HIGH":income.compareTo(new BigDecimal("4000"))>=0?"MEDIUM":"LOW";
        var tags=new ArrayList<String>();tags.add(preference);tags.add("养老");if(c.message.contains("健康"))tags.add("健康");if(c.message.contains("旅行")||c.message.contains("旅游"))tags.add("旅居");
        c.profile=new FinancialProfile(stage,level,ability,tags);
    }
    private Integer integer(String s,String regex){var m=Pattern.compile(regex).matcher(s);return m.find()?Integer.valueOf(m.group(1)):null;}
    private BigDecimal money(String s,String regex){var m=Pattern.compile(regex).matcher(s);if(!m.find())return null;var n=new BigDecimal(m.group(1));return "万".equals(m.group(2))?n.multiply(BigDecimal.valueOf(10000)):n;}
    private BigDecimal parseNumber(String s){if(s==null)return null;var m=Pattern.compile("\\d+(?:\\.\\d+)?").matcher(s);return m.find()?new BigDecimal(m.group()):null;}
}
