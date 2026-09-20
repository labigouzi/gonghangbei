package com.yilu.yinling.admin.service;
import com.yilu.yinling.admin.vo.*;
import java.time.LocalDate;
import java.util.List;
public interface AdminService { OverviewStats overview(); OverviewStats overview(LocalDate start, LocalDate end); RiskStatistics fraudStatistics(); ProfileStatistics profileStatistics(); List<TrendVO> fraudTrend(LocalDate start, LocalDate end); }
