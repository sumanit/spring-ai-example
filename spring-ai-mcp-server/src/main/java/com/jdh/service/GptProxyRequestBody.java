package com.jdh.service;

import lombok.Data;

import java.util.List;

/**
 * Project: jdh-kangkang
 * Author: suman6
 * Created: 2025/7/9
 * Description:
 */
@Data
public class GptProxyRequestBody {

    /**
     * 非必填
     * 24年开始网关调用费用计入apikey负责人所在部门，此处仅是为了记录发起请求的erp，如未填写则自动计为key负责人。
     * （注意：如果填写了erp，一定是人资系统中存在的erp，否则会调用失败）
     */
    private String erp;

    /**
     * 非必填
     * 使用同步调用时，此参数应当设置为 fasle 或者省略。表示模型生成完所有内容后一次性返回所有内容。
     * 如果设置为 true，模型将通过标准 Event Stream ，逐块返回模型生成内容。Event Stream 结束时会返回一条data: [DONE]消息。
     */
    private Boolean stream;

    /**
     * 非必填
     * 传模式（仅google、aws 生效）
     * 参考google文档
     * 参考Aws文档
     */
    private Boolean transparent;

    /**
     * 必填
     * 所要调用的模型编码
     */
    private String model;

    /**
     * 必填
     * 调用语言模型时，将当前对话信息列表作为提示输入给模型
     */
    private List<GptProxyMessageRpcParam> messages;
    /**
     * 非必填
     * do_sample 为 true 时启用采样策略，do_sample 为 false 时采样策略 temperature、top_p 将不生效
     */
    private Boolean do_sample;
    /**
     * 非必填
     * 采样温度，控制输出的随机性，必须为正数
     * 取值范围是：(0.0, 1.0)，不能等于 0，默认值为 0.95，值越大，会使输出更随机，更具创造性；值越小，输出会更加稳定或确定
     * 建议您根据应用场景调整top_p或temperature参数，但不要同时调整两个参数
     */
    private Double temperature;
    /**
     * 非必填
     * 用温度取样的另一种方法，称为核取样
     * 取值范围是：(0.0, 1.0)开区间，不能等于 0 或 1，默认值为 0.7
     * 模型考虑具有top_p概率质量 tokens 的结果
     * 例如：0.1 意味着模型解码器只考虑从前 10% 的概率的候选集中取 tokens
     * 建议您根据应用场景调整top_p或temperature参数，但不要同时调整两个参数
     */
    private Double top_p;
    /**
     * 非必填
     * 模型输出最大 tokens
     */
    private Integer max_tokens;
    /**
     * 非必填
     * 模型在遇到stop所制定的字符时将停止生成，目前仅支持单个停止词，格式为["stop_word1"]
     */
    private List<String> stop;
    /**
     * 非必填
     * 可供模型调用的工具列表，tools 字段会计算 tokens ，同样受到 tokens 长度的限制
     */
    private List<Object> tools;
    /**
     * 非必填
     * 用于控制模型是如何选择要调用的函数，仅当工具类型为function时补充。默认为auto
     */
    private String tool_choice;

    private String uuid;
}

