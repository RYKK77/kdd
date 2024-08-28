package com.rykk.kdd.manager;


import cn.hutool.core.util.StrUtil;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class AiManager {
    @Resource
    private ClientV4 clientV4;

    private static final float STABLE_TEMPERATURE = 0.05f;

    private static final float UNSTABLE_TEMPERATURE = 0.8f;

    public String doSyncStableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, STABLE_TEMPERATURE);
    }

    public String doSyncUnstableRequest(String systemMessage, String userMessage) {
        return doRequest(systemMessage, userMessage, Boolean.FALSE, UNSTABLE_TEMPERATURE);
    }

    /**
     * 通用一点的请求
     *
     * @param systemMessage 系统消息
     * @param userMessage   用户消息
     * @param isStream      是否为流式传输
     * @param temperature   温度
     * @return 消息
     */
    public String doRequest(String systemMessage, String userMessage, Boolean isStream, Float temperature) {
        List<ChatMessage> messages = new ArrayList<>();
        if (StrUtil.isNotEmpty(systemMessage)) {
            messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
        }
        if (StrUtil.isNotEmpty(userMessage)) {
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), userMessage));
        }
        return doRequest(messages, isStream, temperature);

    }

    /**
     * 请求大模型问答
     *
     * @param messages    消息列表
     * @param isStream    是否为流式传输
     * @param temperature 温度
     * @return 消息结果
     */
    public String doRequest(List<ChatMessage> messages, Boolean isStream, Float temperature) {
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(isStream)
//                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
//                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        ChatMessage message = invokeModelApiResp.getData().getChoices().get(0).getMessage();
        return message.getContent().toString();
    }

}
