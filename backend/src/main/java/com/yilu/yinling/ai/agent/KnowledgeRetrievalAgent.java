package com.yilu.yinling.ai.agent;
import com.yilu.yinling.ai.agent.model.AgentModels.KnowledgeSource; import com.yilu.yinling.ai.rag.RagService; import org.springframework.stereotype.Component;
@Component public class KnowledgeRetrievalAgent { private final RagService rag; public KnowledgeRetrievalAgent(RagService rag){this.rag=rag;} public void retrieve(AgentContext c){try{c.knowledge=rag.retrieveDetailed(c.message,3).stream().map(x->new KnowledgeSource(x.title(),x.content(),x.similarity())).toList();}catch(RuntimeException e){c.knowledge=java.util.List.of();}} }
