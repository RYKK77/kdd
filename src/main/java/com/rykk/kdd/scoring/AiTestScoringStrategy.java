package com.rykk.kdd.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.rykk.kdd.manager.AiManager;
import com.rykk.kdd.model.dto.question.QuestionAnswerDTO;
import com.rykk.kdd.model.dto.question.QuestionContentDTO;
import com.rykk.kdd.model.entity.App;
import com.rykk.kdd.model.entity.Question;
import com.rykk.kdd.model.entity.ScoringResult;
import com.rykk.kdd.model.entity.UserAnswer;
import com.rykk.kdd.model.enums.AppScoringStrategyEnum;
import com.rykk.kdd.model.enums.AppTypeEnum;
import com.rykk.kdd.model.vo.QuestionVO;
import com.rykk.kdd.service.QuestionService;
import com.rykk.kdd.service.ScoringResultService;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI测评类应用评分策略
 */
@ScoringStrategyConfig(appType = AppTypeEnum.TEST, scoringStrategy = AppScoringStrategyEnum.AI)
public class AiTestScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private AiManager aiManager;

    @Resource
    private RedissonClient redissonClient;

    // 分布式锁的KEY
    public static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    // 定义一个缓存对象，用于存储答案
    private final Cache<String, String> answerCacheMap =
            // 使用Caffeine创建缓存对象
            Caffeine.newBuilder()
                    // 设置初始容量为1024
                    .initialCapacity(1024)
                    // 设置缓存项在最后一次被访问后5分钟过期
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    // 构建缓存对象
                    .build();

    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE ="你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n"+
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n"+
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";

    /**
     * AI评分消息封装
     * @param app 应用
     * @param questionContentDTOList 题目列表
     * @param choices 选项
     * @return
     */
    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        Question questions = questionService.getById(app.getId());
        String questionContentStr = questions.getQuestionContent();
//        List<QuestionContentDTO> questionContent = JSONUtil.toList(JSONUtil.parseArray(questionContentStr), QuestionContentDTO.class);

        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();

            // 设置题目标题
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());

            // 获取用户的选择（比如 'A', 'B', 'C', 'D'）
            String userChoice = choices.get(i);

            // 查找用户选择对应的选项内容
            QuestionContentDTO questionContentDTO = questionContentDTOList.get(i);
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                // 假设 'A', 'B', 'C', 'D' 存储在 Option 的 key 字段中
                if (option.getKey().equals(userChoice)) {  // 比较用户选择的选项
                    questionAnswerDTO.setUserAnswer(option.getValue());  // 设置选项的实际内容
                    break;  // 找到匹配项后即可退出循环
                }
            }
            // 将每个 QuestionAnswerDTO 加入列表
            questionAnswerDTOList.add(questionAnswerDTO);
        }


        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }


    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        String jsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, jsonStr);
        String ifPresent = answerCacheMap.getIfPresent(cacheKey);
        // 如果有缓存， 就直接返回
        if (ifPresent != null) {
            // 直接获得缓存的返回值，填充答案对象的属性并返回
            UserAnswer userAnswer = JSONUtil.toBean(ifPresent, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(JSONUtil.toJsonStr(choices));
            return userAnswer;
        }

        // 定义锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + ":" + cacheKey);
        boolean isLocked = lock.tryLock(15, 15, TimeUnit.SECONDS); // 5秒等待，15秒自动释放

        if (!isLocked) {
            throw new RuntimeException("系统繁忙，请稍后重试");
        }

        try {
            ifPresent = answerCacheMap.getIfPresent(cacheKey);
            // 如果有缓存， 就直接返回
            if (ifPresent != null) {
                // 直接获得缓存的返回值，填充答案对象的属性并返回
                UserAnswer userAnswer = JSONUtil.toBean(ifPresent, UserAnswer.class);
                userAnswer.setAppId(appId);
                userAnswer.setAppType(app.getAppType());
                userAnswer.setScoringStrategy(app.getScoringStrategy());
                userAnswer.setChoices(JSONUtil.toJsonStr(choices));
                return userAnswer;
            }
            // 1. 根据 id 查询到题目
            Question question = questionService.getOne(
                    Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
            );
            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
            // 2. 调用 AI 获取结果
            // 封装 Prompt
            String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
            // AI 生成
            String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, userMessage);
            // 结果处理
            int start = result.indexOf("{");
            int end = result.lastIndexOf("}");
            String json = result.substring(start, end + 1);
            answerCacheMap.put(cacheKey, json);
            // 3. 构造返回值，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(json, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(JSONUtil.toJsonStr(choices));
            return userAnswer;
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 构建缓存key
     * @param appId appId
     * @param choicesStr 选项列表
     * @return
     */
    private String buildCacheKey(Long appId, String choicesStr) {
        return DigestUtils.md5Hex(appId + ":" + choicesStr);
    }


}
