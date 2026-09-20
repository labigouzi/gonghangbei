package com.yilu.yinling.demo;

import com.yilu.yinling.ai.dto.ChatRequest;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.ai.vo.ChatResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@Profile("demo")
public class DemoAssistantService implements AssistantService {
    @Override public ChatResponse chat(Long userId, ChatRequest request) {
        long start = System.currentTimeMillis(); String text = request.getQuestion(); String reply;
        if (text.contains("行程") || text.contains("规划")) reply = "【简单解释】我可以陪您规划轻松、安全的银龄行程。\n【具体建议】1. 选择交通方便的目的地；2. 每天只安排一到两个主要活动；3. 提前确认住宿、电梯和医疗点。\n【风险提醒】请通过正规旅行机构预订，不向陌生账户转账。";
        else if (text.contains("产品")) reply = "【简单解释】银龄旅行产品更重视节奏舒缓、健康保障和陪伴服务。\n【具体建议】重点查看交通、住宿、随队服务和退改规则。\n【风险提醒】不承诺收益，不代替您选择具体产品。";
        else if (text.contains("服务") || text.contains("介绍")) reply = "忆路银龄提供银龄旅行咨询、适老行程建议、养老金融知识陪伴和安全提醒。现场演示不会发生真实交易。";
        else reply = "您好，我是忆路银龄智能助手。您可以问我银龄旅行、服务介绍、产品介绍或行程规划。";
        long latency = System.currentTimeMillis() - start;
        var sources = List.of(new ChatResponse.Source("忆路银龄演示知识", "银龄旅行", "适老旅行应兼顾安全、节奏、健康和陪伴。"));
        return new ChatResponse(reply, 1L, "请通过正规机构核实重要信息", List.of("忆路银龄演示知识"), true, true, sources, 0.95, true, reply, "demo", "keyword-fallback", latency);
    }
}
