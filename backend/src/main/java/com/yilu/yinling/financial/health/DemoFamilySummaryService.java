package com.yilu.yinling.financial.health;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("demo")
public class DemoFamilySummaryService implements FamilySummaryService {
    @Override
    public FamilySummary getSummary(Long userId) {
        return new FamilySummary("演示管理员", 68, "已退休 · 健康养老规划中", List.of(
                "家人每月共同核对一次养老资金安排",
                "收到转账、验证码或补贴链接时先联系家人核实",
                "在家庭联系人中保存银行官方客服电话"
        ));
    }
}
