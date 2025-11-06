package com.jdh.flux;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Project: spring-ai-example
 * Author: suman6
 * Created: 2025/10/24
 * Description: Spliterator使用示例
 */
public class ListTest {
    public static void main(String[] args) {
        // 1. 创建Spliterator
        Spliterator<Integer> spliterator = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).spliterator();

        // 打印Spliterator的特性
        System.out.println("Spliterator特性值: " + spliterator.characteristics());
        System.out.println("是否有ORDERED特性: " +
                spliterator.hasCharacteristics(Spliterator.ORDERED));
        System.out.println("是否有SIZED特性: " +
                spliterator.hasCharacteristics(Spliterator.SIZED));
        System.out.println("预估大小: " + spliterator.estimateSize());
        System.out.println();

        // 2. 使用tryAdvance()方法顺序处理元素
        System.out.println("使用tryAdvance()处理前3个元素:");
        for (int i = 0; i < 3; i++) {
            spliterator.tryAdvance(item -> System.out.println("处理元素: " + item));
        }
        System.out.println();

        // 3. 使用trySplit()方法分割Spliterator
        System.out.println("分割Spliterator:");
        Spliterator<Integer> spliterator2 = spliterator.trySplit();
        System.out.println("原Spliterator预估大小: " + spliterator.estimateSize());
        System.out.println("新Spliterator预估大小: " + (spliterator2 != null ? spliterator2.estimateSize() : "null"));
        System.out.println();

        // 4. 使用forEachRemaining()方法批量处理剩余元素
        System.out.println("使用forEachRemaining()处理第一部分的剩余元素:");
        AtomicInteger sum = new AtomicInteger(0);
        spliterator.forEachRemaining(item -> {
            System.out.println("处理元素: " + item);
            sum.addAndGet(item);
        });
        System.out.println("第一部分元素和: " + sum.get());
        System.out.println();

        // 5. 处理第二部分的元素
        if (spliterator2 != null) {
            System.out.println("处理第二部分的元素:");
            AtomicInteger sum2 = new AtomicInteger(0);
            spliterator2.forEachRemaining(item -> {
                System.out.println("处理元素: " + item);
                sum2.addAndGet(item);
            });
            System.out.println("第二部分元素和: " + sum2.get());
        }
        System.out.println();

        // 6. 并行处理示例
        System.out.println("并行处理示例:");
        List<Integer> largeList = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            largeList.add(i);
        }

        // 创建自定义Consumer来模拟处理
        class CountingConsumer implements Consumer<Integer> {
            private final String name;
            private int count = 0;

            CountingConsumer(String name) {
                this.name = name;
            }

            @Override
            public void accept(Integer t) {
                count++;
                // 为了演示，只打印前几个元素
                if (count <= 3) {
                    System.out.println(name + " 处理: " + t);
                }
            }

            public int getCount() {
                return count;
            }
        }

        // 获取主Spliterator
        Spliterator<Integer> mainSpliterator = largeList.spliterator();

        // 分割成多个Spliterator
        Spliterator<Integer> split1 = mainSpliterator.trySplit();
        Spliterator<Integer> split2 = mainSpliterator.trySplit();
        Spliterator<Integer> split3 = split1.trySplit();

        // 创建多个Consumer
        CountingConsumer consumer1 = new CountingConsumer("消费者1");
        CountingConsumer consumer2 = new CountingConsumer("消费者2");
        CountingConsumer consumer3 = new CountingConsumer("消费者3");
        CountingConsumer consumer4 = new CountingConsumer("消费者4");

        // 模拟并行处理
        mainSpliterator.forEachRemaining(consumer1);
        split1.forEachRemaining(consumer2);
        split2.forEachRemaining(consumer3);
        split3.forEachRemaining(consumer4);

        // 打印处理结果
        System.out.println("消费者1处理了 " + consumer1.getCount() + " 个元素");
        System.out.println("消费者2处理了 " + consumer2.getCount() + " 个元素");
        System.out.println("消费者3处理了 " + consumer3.getCount() + " 个元素");
        System.out.println("消费者4处理了 " + consumer4.getCount() + " 个元素");
        System.out.println("总共处理了 " +
                (consumer1.getCount() + consumer2.getCount() +
                 consumer3.getCount() + consumer4.getCount()) + " 个元素");
    }
}
