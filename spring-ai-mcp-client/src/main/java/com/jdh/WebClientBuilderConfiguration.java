package com.jdh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.http.server.reactive.AbstractListenerServerHttpResponse;
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author suman
 * @Date 2025/11/30 01:10
 */
@Configuration
public class WebClientBuilderConfiguration {
    private static final int BUFFER_SIZE = 50;
    @Bean
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder()
                .filter(ExchangeFilterFunction.ofRequestProcessor(request->{
                    ClientRequest build = ClientRequest.from(request)
                            .body((outputMessage, context) -> request.body().insert(new BufferingDecorator(outputMessage), context))
                            .build();
                    return Mono.just(build);
                }))
                .filter(ExchangeFilterFunction.ofResponseProcessor(response->{
                    if(!response.request().getURI().toString().contains("model")){
                        return Mono.just(response);
                    }
                    Flux<DataBuffer> clientResponseFlux = response.bodyToFlux(DataBuffer.class).doOnNext(dataBuffer -> {
                        String string = dataBuffer.toString(StandardCharsets.UTF_8);

                        try {
                            String jsonString = string;
                            if (string.startsWith("data: ")) {
                                jsonString = string.substring(6); // 移除"data: "前缀
                            }
                            JSONObject jsonObject = JSON.parseObject(jsonString);
                            String content = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("delta").getString("content");
                            System.out.println("filter:" + content);
                       } catch (Exception e) {
                          // e.printStackTrace();
                       }
                    });
                    return Mono.just(ClientResponse.from(response).body(clientResponseFlux).build());
                }));
    }

    private static final class BufferingDecorator extends ClientHttpRequestDecorator {
        private BufferingDecorator(ClientHttpRequest delegate) {
            super(delegate);
        }

        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            return DataBufferUtils.join(body).flatMap(buffer -> {
                getHeaders().setContentLength(buffer.readableByteCount());
                String string = buffer.toString(StandardCharsets.UTF_8);
                JSONObject jsonObject = JSON.parseObject(string);
                if("tools/call".equals(jsonObject.getString("method"))) {
                    JSONObject param = jsonObject.getJSONObject("params");
                    JSONObject arguments = param.getJSONObject("arguments");
                    arguments.put("query","aaaa");
                }else {
                    return super.writeWith(Mono.just(buffer));
                }

                String newBody = jsonObject.toString();
                DataBuffer wrap = buffer.factory().wrap(newBody.getBytes(StandardCharsets.UTF_8));
                return super.writeWith(Mono.just(wrap));
            });
        }
    }
}
