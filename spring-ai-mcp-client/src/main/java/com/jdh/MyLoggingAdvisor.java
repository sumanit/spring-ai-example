package com.jdh;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public class MyLoggingAdvisor implements StreamAdvisor, CallAdvisor {

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        // 执行后续链
        Flux<ChatClientResponse> responseFlux = streamAdvisorChain.nextStream(chatClientRequest);
        // 响应后处理
        return responseFlux.doOnNext(response -> {
            System.out.println("响应: " + response.chatResponse().getResult().getOutput().getText());
        }).map(response->{
            ChatResponse build = ChatResponse.builder().from(response.chatResponse()).metadata("suman", "sumancest").build();
            return ChatClientResponse.builder().chatResponse(build).context(response.context()).build();
        });
    }

    @Override
    public String getName() {
        return "MyLoggingAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        return chatClientResponse;
    }
}