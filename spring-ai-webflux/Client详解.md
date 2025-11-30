# Client组件
## ExchangeFunction 核心交换执行器
- 直接负责执行ClientRequest并返回Mono<ClientResponse>
- 每个WebClient实例底层都有一个ExchangeFunction实现
- 处理HTTP协议细节，如连接管理、编解码等

## ExchangeFilterFunction