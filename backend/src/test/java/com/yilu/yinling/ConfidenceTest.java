package com.yilu.yinling;
import com.yilu.yinling.ai.safety.ConfidenceService; import org.junit.jupiter.api.Test; import static org.junit.jupiter.api.Assertions.*;
class ConfidenceTest { @Test void knowledgeHitIsHigherThanNoKnowledge(){var s=new ConfidenceService(); assertTrue(s.calculate(3,true,100)>s.calculate(0,false,100)); assertEquals(0.5,s.calculate(0,false,100));} }
