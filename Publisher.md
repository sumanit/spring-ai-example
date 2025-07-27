Publisher 是响应式编程（Reactive Programming）的核心接口，它定义了数据流的 ‌发布（Publish）‌ 能力，允许订阅者（Subscriber）异步接收数据
# Publisher 的核心作用‌
## 1. 数据流抽象‌
- 代表‌异步数据源‌（如数据库查询、HTTP 请求、事件流等），可以按需生成数据（Push 模型）‌
- 与传统的同步 Iterator 不同，Publisher 支持‌背压（Backpressure）‌，即订阅者可以控制数据流的速率‌34
## 2. 响应式编程基础‌
 - Reactor‌（如 Flux 和 Mono）和 RxJava 等响应式框架的基础‌
 - 开发者可以基于 Publisher 构建复杂的数据流处理链（如 map、filter、flatMap 等操作符）
---
# Publisher 的关键方法‌

| 方法                  | 作用                                                         |
| --------------------- | ------------------------------------------------------------ |
| subscribe(Subscriber) | 订阅数据流，Subscriber 定义如何接收数据（onNext、onComplete、onError） |
---
# Publisher 与 Reactor 流的关系‌
- Flux‌（0-N 个元素）：Flux 是 Publisher 的子类，专门处理‌多个数据‌的流‌
- Mono‌（0-1 个元素）：Mono 也是 Publisher，用于处理‌单个数据‌或空数据‌
