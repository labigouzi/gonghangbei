package com.yilu.yinling.admin.controller;

import com.yilu.yinling.admin.service.AdminService;
import com.yilu.yinling.admin.vo.*;
import com.yilu.yinling.common.response.Result;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@org.springframework.context.annotation.Profile("!demo")
@RequestMapping("/api/v1/admin")
public class AdminDashboardController {
    private final AdminService service;
    public AdminDashboardController(AdminService service) { this.service = service; }
    @GetMapping("/dashboard/overview") public Result<OverviewStats> overview(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) { return Result.ok(startDate == null && endDate == null ? service.overview() : service.overview(startDate, endDate)); }
    @GetMapping("/fraud/statistics") public Result<RiskStatistics> fraudStatistics() { return Result.ok(service.fraudStatistics()); }
    @GetMapping("/profile/statistics") public Result<ProfileStatistics> profileStatistics() { return Result.ok(service.profileStatistics()); }
    @GetMapping("/fraud/trend") public Result<List<TrendVO>> fraudTrend(@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) LocalDate endDate) { return Result.ok(service.fraudTrend(startDate, endDate)); }
}
