package com.yilu.yinling;

import com.yilu.yinling.ai.client.MockLlmClient;
import com.yilu.yinling.ai.embedding.EmbeddingProperties;
import com.yilu.yinling.ai.rag.MockEmbeddingService;
import com.yilu.yinling.ai.rag.MockVectorStore;
import com.yilu.yinling.knowledge.parser.MarkdownParser;
import com.yilu.yinling.knowledge.parser.TxtParser;
import com.yilu.yinling.knowledge.splitter.TextSplitter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import java.nio.charset.StandardCharsets;
import static org.junit.jupiter.api.Assertions.*;

class Sprint7ComponentTest {
    @Test void mockLlmAndEmbeddingRemainAvailable() {
        assertFalse(new MockLlmClient().chat("system", "养老规划").isBlank());
        assertEquals(1536, new MockEmbeddingService().embed("test").size());
    }
    @Test void supportedDocumentsAreParsedAndSplit() {
        var file = new MockMultipartFile("file", "guide.md", "text/markdown", "# 个人养老金\n安全规划".getBytes(StandardCharsets.UTF_8));
        var parser = new MarkdownParser();
        assertTrue(parser.supports(file.getOriginalFilename(), file.getContentType()));
        assertFalse(new TextSplitter().split(parser.parse(file)).isEmpty());
        assertTrue(new TxtParser().supports("a.txt", "text/plain"));
    }
    @Test void mockVectorStoreReturnsTopK() {
        assertEquals(2, new MockVectorStore().search("养老", 2).size());
    }
}
