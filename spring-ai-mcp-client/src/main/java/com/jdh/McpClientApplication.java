package com.jdh;

import com.alibaba.fastjson.JSON;
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
import reactor.core.publisher.Flux;
import reactor.util.context.Context;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@SpringBootApplication
public class McpClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(McpClientApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }
    @Value("${ai.user.input:今天天气怎么样}")
    private String userInput;


    @Bean
    public CommandLineRunner predefinedQuestions(ChatClient.Builder chatClientBuilder,
                                                 ConfigurableApplicationContext context) {
        //List<ToolCallback> collect = Arrays.stream(tools.getToolCallbacks()).map(item -> new MyToolBack(item)).collect(Collectors.toList());
        return args -> {

            var chatClient = chatClientBuilder
                   // .defaultToolCallbacks(collect)
                    .build();
            System.out.println("\n>>> QUESTION: " + userInput);
            System.out.print("\n>>> ASSISTANT: ");

            Flux.deferContextual(contextView -> {
                HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
                objectObjectHashMap.put("ddd","aaaa");
                return chatClient.prompt()
                        .user(userInput)
                        .advisors(new MyLoggingAdvisor(),new MyLoggingAdvisor2())
                        .stream()
                        .chatResponse()
                        .contextWrite(Context.of(objectObjectHashMap))
                        .doOnNext(content -> {
                            System.out.println("doOnNext:"+content.getResult().getOutput().getText());
                        })
                        .doOnComplete(() -> System.out.println("\n>>> 流式响应完成"))
                        .doOnError(error -> System.err.println("流式调用出错: " + error.getMessage()));
            }) .subscribe();
            // 使用流式调用
            Thread.sleep(200000L);
            context.close();
        };
    }
}