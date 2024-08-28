package com.rykk.kdd;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.image.CreateImageRequest;
import com.zhipu.oapi.service.v4.image.ImageApiResponse;
import com.zhipu.oapi.service.v4.image.ImageResult;
import com.zhipu.oapi.service.v4.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.zhipu.oapi.demo.V4OkHttpClientTest.mapStreamToAccumulator;

@SpringBootTest
public class ZhipuAITest {

    @Resource
    private ClientV4 client;

    @Test
    public void test() {
//        ClientV4 client = new ClientV4.Builder("ec20986597668479a52433c284ea9a0a.31G9pavmcfYf7BFb").build();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "作为一名营销专家，请为智谱开放平台创作一个吸引人的slogan");
        messages.add(chatMessage);
//        String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
//                .requestId(requestId)
                .build();
        ModelApiResponse invokeModelApiResp = client.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:" + invokeModelApiResp.getData().getChoices());
    }
    @Test
    public void testForImg() {
        CreateImageRequest createImageRequest = new CreateImageRequest();
        createImageRequest.setModel(Constants.ModelCogView);
        createImageRequest.setPrompt("小猫游泳");
//        createImageRequest.setRequestId("test11111111111111");
        ImageApiResponse imageApiResponse = client.createImage(createImageRequest);
        int code = imageApiResponse.getCode();
        String msg = imageApiResponse.getMsg();
        boolean success = imageApiResponse.isSuccess();
        ImageResult data = imageApiResponse.getData();
        String string = data.getData().get(0).getUrl();
        System.out.println("code:" + code + "\n msg:" + msg + "\n success:" + success + "\n data:" + JSON.toJSONString(data));
        System.out.println(string);
    }


}
