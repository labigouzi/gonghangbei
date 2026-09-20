package com.yilu.yinling.financial.health;

import com.yilu.yinling.profile.service.ProfileService;
import com.yilu.yinling.profile.vo.ProfileView;
import com.yilu.yinling.user.service.UserService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("!demo")
public class DefaultFamilySummaryService implements FamilySummaryService {
    private final UserService users;
    private final ProfileService profiles;

    public DefaultFamilySummaryService(UserService users, ProfileService profiles) {
        this.users = users;
        this.profiles = profiles;
    }

    @Override
    public FamilySummary getSummary(Long userId) {
        var user = users.current(userId);
        ProfileView profile = profiles.get(userId);
        String name = user.realName() == null || user.realName().isBlank() ? user.username() : user.realName();
        int age = profile == null || profile.age() == null ? 65 : profile.age();
        String status = profile == null || profile.retirementStatus() == null ? "养老规划待完善" : profile.retirementStatus();
        return new FamilySummary(name, age, status, List.of(
                "建议由家人与老人共同确认大额资金安排",
                "遇到陌生链接、验证码或转账要求时先通过官方渠道核实",
                "定期更新紧急联系人和医疗保障信息"
        ));
    }
}
