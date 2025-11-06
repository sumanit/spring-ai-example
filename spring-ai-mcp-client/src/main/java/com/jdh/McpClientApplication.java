package com.jdh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication
public class McpClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(McpClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }
    @Value("${ai.user.input:请你分析一下 大模型 近期进展，进行深度搜索进行判断}")
    private String userInput;


    @Bean
    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder, ToolCallbackProvider tools,
                                                 ConfigurableApplicationContext context) {
        List<ToolCallback> collect = Arrays.stream(tools.getToolCallbacks()).map(item -> new MyToolBack(item)).collect(Collectors.toList());
        return args -> {

            var chatClient = chatClientBuilder
                    .defaultToolCallbacks(collect)
                    .build();
            System.out.println("\n>>> QUESTION: " + userInput);
            System.out.print("\n>>> ASSISTANT: ");

            // 使用流式调用
            chatClient.prompt()
                    .system("你是一个专业助手，你有搜索mcp工具可以调用。请在回复中进行调用，不要一次性在前面调用\n" +
                            "请你在回答用户问题的时候，按照如下规则进行使用。\n" +
                            "第一步：首先请先通过联网搜索mcp搜索一次，以便你对知识又基本的认知。\n" +
                            "第二步：然后生成3个不同的研究探索方案，对这三个方向分布进行再次搜索，获取更多的知识。\n" +
                            "第三步：你这时候可以构思结果，但请把有疑问的部分再进行搜索确认，用于核实自己的想法。\n" +
                            "最后输出结果。")
                    .user(userInput)
                    .stream()
                    .chatResponse()
                    .doOnNext(content -> {
                        System.out.println(content.getResult());
                    })
                    .doOnComplete(() -> System.out.println("\n>>> 流式响应完成"))
                    .doOnError(error -> System.err.println("流式调用出错: " + error.getMessage())).subscribe();
            Thread.sleep(200000L);
            context.close();
        };
    }
}