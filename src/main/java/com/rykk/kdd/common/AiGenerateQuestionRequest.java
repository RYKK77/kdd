package com.rykk.kdd.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 题目数量
     */
    int questionCount = 10;

    /**
     * 选项数量
     */
    int optionCount = 4;

    private static final long serialVersionUID = 1L;
}