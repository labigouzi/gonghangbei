package com.yilu.yinling;
import com.yilu.yinling.ai.rag.*; import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class RagIntegrationTest { @Test void mockRetrievalReturnsTopKWithMetadata(){var r=new RagService(new MockEmbeddingService(),new MockVectorStore()).retrieveDetailed("个人养老金领取条件是什么",3);assertEquals(3,r.size());assertTrue(r.get(0).similarity()>0);assertFalse(r.get(0).title().isBlank());} }
