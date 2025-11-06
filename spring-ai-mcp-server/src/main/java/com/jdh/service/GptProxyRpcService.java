package com.jdh.service;

import reactor.core.publisher.Flux;

/**
 * @description : 京东大模型网关rpc服务
 * @author : fanpeng21
 * @date : 2024/5/23
 */
public interface GptProxyRpcService {


    Flux<String> webSearch(GptProxyRequestBody requestBody);
}
