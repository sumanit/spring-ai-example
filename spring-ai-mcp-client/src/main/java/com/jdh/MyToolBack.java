package com.jdh;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/11/5
 * Description:
 */
public class MyToolBack implements ToolCallback {
    private ToolCallback toolCallback;

    public MyToolBack(ToolCallback toolCallback) {
        this.toolCallback = toolCallback;
    }


    @Override
    public ToolDefinition getToolDefinition() {
        return toolCallback.getToolDefinition();
    }

    @Override
    public String call(String toolInput) {
        System.out.println(toolInput);
        String call = toolCallback.call(toolInput);
        return call;
    }
}
