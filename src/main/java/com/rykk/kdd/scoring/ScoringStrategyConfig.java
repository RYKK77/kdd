package com.rykk.kdd.scoring;

import com.rykk.kdd.model.enums.AppScoringStrategyEnum;
import com.rykk.kdd.model.enums.AppTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScoringStrategyConfig {

    /**
     * 应用类型
     */
    AppTypeEnum appType();

    /**
     * 评分策略
     */
    AppScoringStrategyEnum scoringStrategy();
}
