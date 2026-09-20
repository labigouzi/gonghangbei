package com.yilu.yinling.evaluation;
public record EvaluationResult(String question,String category,String answer,boolean knowledgeUsed,int sourceCount,double confidence,int manualScore) {}
