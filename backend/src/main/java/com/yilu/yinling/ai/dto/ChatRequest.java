package com.yilu.yinling.ai.dto;
import jakarta.validation.constraints.NotBlank;

/** Supports both the legacy question field and the simple message field. */
public class ChatRequest {
    @NotBlank(message = "问题不能为空") private String question;
    private Long conversationId;
    public ChatRequest() {}
    public ChatRequest(String question, Long conversationId) { this.question = question; this.conversationId = conversationId; }
    public ChatRequest(String message) { this.question = message; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getMessage() { return question; }
    public void setMessage(String message) { this.question = message; }
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}
