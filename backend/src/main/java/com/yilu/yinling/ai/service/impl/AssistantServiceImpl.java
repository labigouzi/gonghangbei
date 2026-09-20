package com.yilu.yinling.ai.service.impl;

import com.yilu.yinling.ai.client.LlmClient;
import com.yilu.yinling.ai.client.LlmProperties;
import com.yilu.yinling.ai.client.LlmRuntimeState;
import com.yilu.yinling.ai.dto.ChatRequest;
import com.yilu.yinling.ai.entity.Conversation;
import com.yilu.yinling.ai.entity.Message;
import com.yilu.yinling.ai.mapper.ConversationMapper;
import com.yilu.yinling.ai.mapper.MessageMapper;
import com.yilu.yinling.ai.prompt.PromptTemplateService;
import com.yilu.yinling.ai.rag.RagService;
import com.yilu.yinling.ai.safety.AiSafetyService;
import com.yilu.yinling.ai.safety.ConfidenceService;
import com.yilu.yinling.ai.service.AssistantService;
import com.yilu.yinling.ai.vo.ChatResponse;
import com.yilu.yinling.profile.service.ProfileService;
import com.yilu.yinling.profile.vo.ProfileView;
import org.springframework.stereotype.Service;
import com.yilu.yinling.common.exception.BusinessException;
import java.util.List;

@Service
@org.springframework.context.annotation.Profile("!demo")
public class AssistantServiceImpl implements AssistantService {
    private final LlmClient llmClient; private final PromptTemplateService promptService; private final RagService ragService;
    private final AiSafetyService safetyService; private final ConversationMapper conversationMapper; private final MessageMapper messageMapper;
    private final ProfileService profileService;
    private final ConfidenceService confidenceService;
    private final LlmProperties llmProperties; private final LlmRuntimeState runtimeState;
    public AssistantServiceImpl(LlmClient l, PromptTemplateService p, RagService r, AiSafetyService s, ConversationMapper c, MessageMapper m, ProfileService profileService, ConfidenceService confidenceService, LlmProperties llmProperties, LlmRuntimeState runtimeState) { llmClient=l; promptService=p; ragService=r; safetyService=s; conversationMapper=c; messageMapper=m; this.profileService=profileService; this.confidenceService=confidenceService; this.llmProperties=llmProperties; this.runtimeState=runtimeState; }
    @Override public ChatResponse chat(Long userId, ChatRequest request) {
        Conversation conversation;
        if (request.getConversationId() == null) { conversation = new Conversation(); conversation.setUserId(userId); conversation.setTitle(request.getQuestion()); conversation.setStatus("ACTIVE"); conversationMapper.insert(conversation); }
        else {
            conversation = conversationMapper.selectById(request.getConversationId());
            if (conversation == null || !userId.equals(conversation.getUserId())) throw new BusinessException("会话不存在或无权访问");
        }
        ProfileView profile = profileService.get(userId);
        List<String> sources = ragService.retrieve(request.getQuestion());
        String context = String.join("\n", sources);
        String answer = safetyService.sanitize(llmClient.chat(promptService.personalizedAssistantPrompt(profile) + "\n参考知识：\n" + context, request.getQuestion()));
        Message userMessage = new Message(); userMessage.setConversationId(conversation.getId()); userMessage.setRole("USER"); userMessage.setContent(request.getQuestion()); messageMapper.insert(userMessage);
        Message aiMessage = new Message(); aiMessage.setConversationId(conversation.getId()); aiMessage.setRole("ASSISTANT"); aiMessage.setContent(answer); messageMapper.insert(aiMessage);
        List<ChatResponse.Source> structuredSources = sources.stream().map(s -> new ChatResponse.Source(sourceTitle(s), sourceCategory(s), s)).toList();
        boolean knowledgeUsed = !sources.isEmpty();
        double confidence = confidenceService.calculate(sources.size(), knowledgeUsed, answer.length());
        String provider = llmProperties.getProvider(); String model = "mock"; if ("deepseek".equals(provider)) model = llmProperties.getDeepseek().getModel(); if ("qwen".equals(provider)) model = llmProperties.getQwen().getModel();
        long latency = runtimeState.getLastMetrics() == null ? runtimeState.getResponseTime() - runtimeState.getRequestTime() : runtimeState.getLastMetrics().latency();
        return new ChatResponse(answer, conversation.getId(), safetyService.notice(), sources, profile != null, profile != null, structuredSources, confidence, knowledgeUsed, answer, provider, model, Math.max(0, latency));
    }
    private String sourceTitle(String content) {
        if (content.contains("诈骗") || content.contains("密码")) return "老年人金融诈骗防范";
        if (content.contains("养老金")) return "个人养老金基础知识";
        return "养老规划指南";
    }
    private String sourceCategory(String content) { return content.contains("诈骗") || content.contains("密码") ? "金融安全" : "养老金融"; }
}
