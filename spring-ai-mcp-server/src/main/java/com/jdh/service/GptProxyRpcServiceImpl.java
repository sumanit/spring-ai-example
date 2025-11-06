package com.jdh.service;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * Project: jdh-kangkang
 * Author: suman6
 * Created: 2025/7/9
 * Description:
 */
@Slf4j
@Service("gptProxyRpcService")
public class GptProxyRpcServiceImpl implements GptProxyRpcService {
    /**
     * webclient客户端
     */
    private static WebClient webClient;

    /**
     * 初始化客户端
     */
    static {
        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        // 连接超时，单位ms
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1 * 1000)
                        // 响应超时，单位s
                        .responseTimeout(Duration.ofSeconds(5L))))
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    // 异常过滤器
                    if (clientResponse.statusCode().isError()) {
                        log.error("WebClient#filter statusCode={}", clientResponse.statusCode());
                        // 处理 WebClientResponseException 异常
                        if (clientResponse.statusCode().is4xxClientError()) {

                            // 处理4xx错误
                            throw new RuntimeException("错误码：" + clientResponse.statusCode());
                        } else if (clientResponse.statusCode().is5xxServerError()) {
                            // 处理5xx错误
                            throw new RuntimeException("错误码：" + clientResponse.statusCode());
                        }
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("错误码：" + clientResponse.statusCode())));
                    }
                    return Mono.just(clientResponse);
                }))
                .exchangeStrategies(strategies -> {
                    strategies.codecs(configurer -> {
                        // 调整内存缓冲区大小
                        configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024);
                    }).build();
                })
                .build();
    }



    @Override
    public Flux<String> webSearch(GptProxyRequestBody requestBody){
        String url = "http://gpt-proxy.jd.com/v1/web-search";

        log.info("GptProxyRpcServiceImpl#postAndReceiveStream url={},requestBody={}",url, JSON.toJSONString(requestBody));
        // 创建  POST请求

        return webClient.post()
                // 设置请求URI
                .uri(url)
                .header("x-ms-client-request-id", requestBody.getUuid())
                .header("Authorization", "e6734039-23d7-46bd-a8f2-96bb4405b0ea")
                // 设置请求Content-Type为application/json
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(JSON.toJSONString(requestBody))
                // 初始化请求
                .retrieve()
                // 将响应体以流的形式转换为Flux<String>
                .bodyToFlux(String.class).timeout(Duration.ofSeconds(40));
    }

}
