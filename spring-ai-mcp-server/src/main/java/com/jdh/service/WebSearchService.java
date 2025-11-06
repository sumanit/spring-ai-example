package com.jdh.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class WebSearchService {

    @Autowired
    private GptProxyRpcService gptProxyRpcService;

    @Tool(description = "我是一个网络搜索接口，可以搜索网络信息",returnDirect = true)
    public String webSearch(String query) {
        if(StringUtils.isBlank(query)) {
            return null;
        }
        System.out.println(query);
        GptProxyRequestBody gptProxyRequestBody = new GptProxyRequestBody();
        gptProxyRequestBody.setStream(false);
        //gptProxyRequestBody.setPage(1);
        //gptProxyRequestBody.setPageSize(1);
        if(gptProxyRequestBody.getModel() == null) {
            gptProxyRequestBody.setModel("search_bocha");
        }
        if(CollectionUtils.isEmpty(gptProxyRequestBody.getMessages())) {

            GptProxyMessageRpcParam gptProxyMessageRpcParam = new GptProxyMessageRpcParam();
            gptProxyMessageRpcParam.setRole("user");
            gptProxyMessageRpcParam.setContent(query);
            gptProxyRequestBody.setMessages(Lists.newArrayList(gptProxyMessageRpcParam));
        }

        Flux<String> stringFlux = gptProxyRpcService.webSearch(gptProxyRequestBody);
        String result =  Mono.from(stringFlux).block(Duration.ofSeconds(5));
        result =buildRagResult(result);
        return  result;
    }

    protected String buildRagResult(String result) {
        if(StringUtils.isBlank(result)) {
            return null;
        }
        JSONObject webSearchResult = JSON.parseObject(result);
        StringBuilder sb = new StringBuilder();
        JSONArray ragItems =  null;
        if(CollectionUtils.isNotEmpty(ragItems = webSearchResult.getJSONArray("pageItems"))) {
            for (int i = 0; i < ragItems.size(); i++) {
                JSONObject item = ragItems.getJSONObject(i);
                String title = item.getString("title");
                String summary = item.getString("summary");
                String url = item.getString("url");
                String siteName = item.getString("siteName");
                if(StringUtils.isNotBlank(title)) {
                    sb.append("标题: ").append(title).append("\n");
                }
                if(StringUtils.isNotBlank(summary)) {
                    sb.append("内容: ").append(summary).append("\n");
                }
                if(StringUtils.isNotBlank(url)) {
                    sb.append("链接: ").append(url).append("\n");
                }
                if(StringUtils.isNotBlank(siteName)) {
                    sb.append("站点: ").append(siteName).append("\n");
                }
            }
        } else if(CollectionUtils.isNotEmpty(ragItems = webSearchResult.getJSONArray("search_result"))) {
            for (int i = 0; i < ragItems.size(); i++) {
                JSONObject item = ragItems.getJSONObject(i);
                String title = item.getString("title");
                String content = item.getString("content");
                if(StringUtils.isNotBlank(title)) {
                    sb.append("标题: ").append(title).append("\n");
                }
                if(StringUtils.isNotBlank(content)) {
                    sb.append("内容: ").append(content).append("\n");
                }
            }

        } else if(CollectionUtils.isNotEmpty(ragItems = webSearchResult.getJSONArray("choices"))) {
            JSONObject choice = ragItems.getJSONObject(0);
            if(choice != null) {
                JSONObject message = choice.getJSONObject("message");
                if(message != null) {
                    JSONArray toolCalls = message.getJSONArray("tool_calls");
                    if(toolCalls != null) {
                        for (int i = 0; i < toolCalls.size(); i++) {
                            JSONObject toolCall = toolCalls.getJSONObject(i);
                            JSONArray searchResult = toolCall.getJSONArray("search_result");
                            if (searchResult != null) {
                                ragItems = searchResult;
                                break;
                            }
                        }
                        if (CollectionUtils.isNotEmpty(ragItems)) {
                            for (int i = 0; i < ragItems.size(); i++) {
                                JSONObject item = ragItems.getJSONObject(i);
                                String title = item.getString("title");
                                String content = item.getString("content");
                                String link = item.getString("link");
                                String media = item.getString("media");
                                if (StringUtils.isNotBlank(title)) {
                                    sb.append("标题: ").append(title).append("\n");
                                }
                                if (StringUtils.isNotBlank(content)) {
                                    sb.append("内容: ").append(content).append("\n");
                                }
                                if(StringUtils.isNotBlank(link)) {
                                    sb.append("链接: ").append(link).append("\n");
                                }
                                if(StringUtils.isNotBlank(media)) {
                                    sb.append("站点: ").append(media).append("\n");
                                }
                            }
                        }
                    }
                }
            }
        }
       return sb.toString();
    }
}