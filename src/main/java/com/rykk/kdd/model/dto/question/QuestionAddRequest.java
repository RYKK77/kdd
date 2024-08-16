package com.rykk.kdd.model.dto.question;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建题目请求
 *
 */
//@AllArgsConstructor
@Data
public class QuestionAddRequest implements Serializable {
    /**
     * 题目内容（json格式）
     */
    private QuestionContentDTO questionContent;

    /**
     * 应用 id
     */
    private Long appId;


    private static final long serialVersionUID = 1L;
}