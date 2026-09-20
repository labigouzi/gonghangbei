package com.yilu.yinling.financial.service;
import com.yilu.yinling.financial.dto.FinancialPlanRequest;
import com.yilu.yinling.financial.vo.FinancialPlanResponse;
public interface FinancialPlanningService { FinancialPlanResponse generate(Long userId, FinancialPlanRequest request); }
