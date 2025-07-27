
# 创建一个FLux 
示例见 CreateFlux.java

- 固定数据创建 `Flux.just(1,2,3)`
- 从数组创建 `Flux.fromArray(new Integer[]{1,2,3})`
- 从Stream创建 `Flux.fromStream(Stream.of(1,2,3))`
- 从streamSupplier创建 `Flux.fromStream(()->Stream.of(1,2,3))`
- 从Iterable创建 `Flux.fromIterable(Lists.newArrayList(1, 2, 3))`
- 从Publisher创建 `Flux.from(new MyPublisher())`
- 从数字范围创建 `Flux.range(1, 10)`
- 定时间隔 `Flux.interval(Duration.ofSeconds(1))`
- 定时间隔，开始时间为1秒，间隔2秒 `Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2))`/
- 定时间隔 `Flux.interval(Duration.ofSeconds(1),Schedulers.parallel())`
- 定时间隔，开始时间为1秒，间隔2秒 `Flux.interval(Duration.ofSeconds(1), Duration.ofSeconds(2), Schedulers.parallel())`

# 连接两个Flux
