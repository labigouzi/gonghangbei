package com.yilu.yinling;
import com.yilu.yinling.evaluation.*; import org.junit.jupiter.api.Test; import java.util.List; import static org.junit.jupiter.api.Assertions.*;
class EvaluationTest { @Test void reportCalculatesRates(){var report=new AiEvaluationService().report(List.of(new EvaluationResult("q","c","a",true,2,.9,4),new EvaluationResult("q2","c","a",false,0,.5,3)));assertEquals(2,report.total());assertEquals(.5,report.knowledgeHitRate());assertEquals(.7,report.averageConfidence(),.001);} }
