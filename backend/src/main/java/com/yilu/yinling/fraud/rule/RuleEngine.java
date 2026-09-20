package com.yilu.yinling.fraud.rule;

import com.yilu.yinling.fraud.detector.RuleResult;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

@Component
public class RuleEngine {
    private record Rule(String keyword, double score, String tag, String reason) { }
    private final List<Rule> rules = List.of(
            new Rule("验证码", 30, "索要验证码", "要求提供验证码可能导致账户被盗用"),
            new Rule("转账", 30, "诱导转账", "出现要求转账或汇款的指令"),
            new Rule("银行卡", 15, "索取银行卡信息", "涉及银行卡等敏感金融信息"),
            new Rule("密码", 20, "索取密码", "正规机构不会通过陌生消息索取密码"),
            new Rule("点击链接", 30, "诱导点击", "诱导点击链接可能跳转至仿冒页面"),
            new Rule("链接", 15, "诱导点击", "陌生链接存在仿冒或恶意跳转风险"),
            new Rule("公检法", 30, "冒充公检法", "冒充公检法施压是常见诈骗手法"),
            new Rule("养老补贴", 25, "养老补贴诈骗", "以养老补贴为名索取信息或费用"),
            new Rule("养老金", 20, "养老诈骗", "涉及养老金异常或领取话术，需要谨慎核实"),
            new Rule("高收益", 20, "高收益诱导", "承诺高收益可能隐含投资诈骗风险"),
            new Rule("投资返利", 25, "投资返利诈骗", "投资返利承诺常用于诱导缴费"),
            new Rule("中奖", 20, "中奖诈骗", "陌生中奖通知通常会诱导提供信息或缴费")
    );

    public RuleResult evaluate(String content) {
        String text = content == null ? "" : content;
        double score = 0;
        List<String> tags = new ArrayList<>();
        List<String> reasons = new ArrayList<>();
        for (Rule rule : rules) {
            if (text.contains(rule.keyword())) {
                score += rule.score();
                if (!tags.contains(rule.tag())) tags.add(rule.tag());
                if (!reasons.contains(rule.reason())) reasons.add(rule.reason());
            }
        }
        return new RuleResult(Math.min(score, 100), tags, reasons);
    }
}
