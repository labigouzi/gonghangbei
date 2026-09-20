package com.yilu.yinling.ai.safety;

import org.springframework.stereotype.Service;

@Service
public class AiSafetyService {
    public String notice() { return "温馨提示：本服务仅提供金融知识普及，不构成投资建议，也不会执行任何资金交易。涉及资金操作请通过银行官方渠道核实。"; }
    public String sanitize(String answer) { return answer == null ? "暂时无法回答，请稍后再试。" : answer; }
}
