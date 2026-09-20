package com.yilu.yinling.profile.vo;
public record ProfileView(Long id, Long userId, Integer age, String gender, String retirementStatus, String monthlyIncome,
                          String pensionDemand, String riskPreference, String investmentExperience, String digitalFinanceLevel,
                          String healthStatus, String familyStructure, String profileTags) {}
