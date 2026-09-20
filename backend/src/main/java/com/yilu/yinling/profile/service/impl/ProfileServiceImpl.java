package com.yilu.yinling.profile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yilu.yinling.profile.dto.ProfileRequest;
import com.yilu.yinling.profile.entity.ElderlyProfile;
import com.yilu.yinling.profile.mapper.ElderlyProfileMapper;
import com.yilu.yinling.profile.service.ProfileService;
import com.yilu.yinling.profile.vo.ProfileView;
import com.yilu.yinling.ai.service.ProfileAnalysisService;
import org.springframework.stereotype.Service;

@Service
@org.springframework.context.annotation.Profile("!demo")
public class ProfileServiceImpl implements ProfileService {
    private final ElderlyProfileMapper mapper;
    private final ProfileAnalysisService analysisService;
    public ProfileServiceImpl(ElderlyProfileMapper mapper, ProfileAnalysisService analysisService) { this.mapper = mapper; this.analysisService = analysisService; }
    private ElderlyProfile find(Long userId) { return mapper.selectOne(new QueryWrapper<ElderlyProfile>().eq("user_id", userId)); }
    private ProfileView view(ElderlyProfile p) { return p == null ? null : new ProfileView(p.getId(), p.getUserId(), p.getAge(), p.getGender(), p.getRetirementStatus(), p.getMonthlyIncome(), p.getPensionDemand(), p.getRiskPreference(), p.getInvestmentExperience(), p.getDigitalFinanceLevel(), p.getHealthStatus(), p.getFamilyStructure(), p.getProfileTags()); }
    @Override public ProfileView get(Long userId) { return view(find(userId)); }
    @Override public ProfileView update(Long userId, ProfileRequest r) {
        ElderlyProfile p = find(userId); if (p == null) { p = new ElderlyProfile(); p.setUserId(userId); }
        p.setAge(r.age()); p.setGender(r.gender()); p.setRetirementStatus(r.retirementStatus()); p.setMonthlyIncome(r.monthlyIncome()); p.setPensionDemand(r.pensionDemand()); p.setRiskPreference(r.riskPreference()); p.setInvestmentExperience(r.investmentExperience()); p.setDigitalFinanceLevel(r.digitalFinanceLevel()); p.setHealthStatus(r.healthStatus()); p.setFamilyStructure(r.familyStructure()); p.setProfileTags(r.profileTags());
        if (p.getId() == null) mapper.insert(p); else mapper.updateById(p); return view(p);
    }
    @Override public ProfileView generate(Long userId, String description) {
        return update(userId, analysisService.analyze(description));
    }
}
