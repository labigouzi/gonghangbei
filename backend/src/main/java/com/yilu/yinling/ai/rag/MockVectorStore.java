package com.yilu.yinling.ai.rag;

import org.springframework.stereotype.Component;
import java.util.List;

@Component
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="vector.provider", havingValue="mock", matchIfMissing=true)
public class MockVectorStore implements VectorStore {
    @Override public List<String> search(String query) {
        return List.of(
                "个人养老金是我国养老保险体系的重要补充，应结合自身收入和养老需求理性了解。",
                "老年人办理金融业务时，应通过银行官方渠道核实信息，不向陌生人提供密码和验证码。",
                "养老规划应先保障基本生活，再根据风险承受能力安排长期资金。"
        );
    }
    @Override public List<String> search(String query, int topK) { return search(query).stream().limit(topK).toList(); }
}
