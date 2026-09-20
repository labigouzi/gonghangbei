package com.yilu.yinling.financial.service.impl;
import com.yilu.yinling.financial.entity.FinancialProduct; import com.yilu.yinling.financial.service.ProductRecommendationService; import com.yilu.yinling.financial.vo.FinancialPlanResponse; import org.springframework.context.annotation.Profile; import org.springframework.stereotype.Service; import java.util.List;
@Service @Profile("demo") public class DemoProductRecommendationService implements ProductRecommendationService {
 private final InMemoryProductRecommendationService delegate=new InMemoryProductRecommendationService(List.of(product(1,"个人养老金","养老储蓄","LOW"),product(2,"养老储蓄产品","储蓄","LOW"),product(3,"养老保险","保险","LOW"),product(4,"稳健型养老理财","理财","MEDIUM_LOW")));
 public List<FinancialPlanResponse.ProductRecommendation> recommend(int age,String risk,String goal){return delegate.recommend(age,risk,goal);} private static FinancialProduct product(long id,String n,String t,String r){var p=new FinancialProduct();p.setId(id);p.setProductName(n);p.setProductType(t);p.setRiskLevel(r);p.setMinAge(40);p.setMaxAge(100);return p;}
}
