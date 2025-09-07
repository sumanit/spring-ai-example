package com.jdh;

/**
 * Project: Default (Template) Project
 * Author: suman6
 * Created: 2025/6/9
 * Description:
 */

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.http.client.ReactorClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.Scanner;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    //@Bean
    CommandLineRunner cli(ChatClient.Builder builder) {
        return args -> {
            ChatClient  chat = builder.build();
            Scanner scanner = new Scanner(System.in);
            System.out.println("\nLet's chat!");
            while (true) {
                System.out.print("\nUSER: ");
                System.out.println("ASSISTANT: " +
                        chat.prompt(scanner.nextLine()).call().content());
            }
        };
    }

    @Bean
    public RestClient.Builder ollamaRestClientBuilder() {
        // 设置 HTTP 客户端
        HttpClient client = HttpClient.create();
        client.compress(true).responseTimeout(Duration.ofMinutes(3));
        ReactorClientHttpRequestFactory factory = new ReactorClientHttpRequestFactory(client);
        RestClient.Builder builder = RestClient.builder()
                .requestFactory(factory);
        return builder;
    }
}