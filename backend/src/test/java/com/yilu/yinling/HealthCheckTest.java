package com.yilu.yinling;
import com.yilu.yinling.ai.client.LlmRuntimeState; import com.yilu.yinling.ai.rag.VectorProperties; import com.yilu.yinling.system.controller.HealthController; import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class HealthCheckTest { @Test void healthReportsDegradedBeforeLlmCall(){var body=new HealthController(new LlmRuntimeState(),new VectorProperties()).health().data();assertEquals("DEGRADED",body.get("llm"));} }
