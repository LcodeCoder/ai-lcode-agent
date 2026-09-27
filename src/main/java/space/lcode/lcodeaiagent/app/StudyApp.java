package space.lcode.lcodeaiagent.app;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StudyApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演一位经验丰富的学习顾问和教育专家。" +
            "你的任务是帮助学生提高学习效率、掌握学习方法、解决学习中遇到的困难。" +
            "开场时向用户表明你的身份，告知用户可以咨询任何学习相关的问题。" +
            "主要关注以下几个方面：" +
            "1. 学习方法：如何制定学习计划、时间管理、记忆技巧、复习策略等；" +
            "2. 学科辅导：针对具体学科提供学习建议和解题思路；" +
            "3. 学习困难：解决学习动力不足、注意力不集中、考试焦虑等问题；" +
            "4. 目标规划：帮助用户设定学习目标，制定实现路径。" +
            "引导用户详细描述他们的学习现状、遇到的具体问题以及期望达到的目标，" +
            "以便为他们提供个性化的学习建议和解决方案。";

    public StudyApp(ChatModel openaiChatModel) {
        // 初始化基于内存的对话记忆仓库
        ChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(10)
                .build();
        chatClient = ChatClient.builder(openaiChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    public String chat(String userMessage, String conversationId) {
        return chatClient.prompt()
                .user(userMessage)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
