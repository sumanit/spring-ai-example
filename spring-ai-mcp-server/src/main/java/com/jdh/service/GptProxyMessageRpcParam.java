package com.jdh.service;

import lombok.Data;

/**
 * Project: jdh-kangkang
 * Author: suman6
 * Created: 2025/7/9
 * Description:
 */
@Data
public class GptProxyMessageRpcParam {

    /**
     * 角色
     */
    private String role;
    /**
     * 内容
     */
    private Object content;

}

