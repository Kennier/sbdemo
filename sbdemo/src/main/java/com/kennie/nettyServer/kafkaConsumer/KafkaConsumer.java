package com.kennie.nettyServer.kafkaConsumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Component
public class KafkaConsumer {

    // 消费监听
    @KafkaListener(topics = {"topic1"})
    public void onMessage1(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
    }

    // 批量消费监听
    @KafkaListener(groupId = "consumerGroup",topics = "${kafka.consume.topic.p2p}")
    public void onP2PMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
            }
        }
    }

    // 批量消费监听
    @KafkaListener(groupId = "consumerGroup",topics = "${kafka.consume.topic.channel}")
    public void onChannelMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
            }
        }
    }

    // 批量消费监听
    @KafkaListener(groupId = "consumerGroup",topics = "${kafka.consume.topic.channelEnter}")
    public void onChannelEnterMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
            }
        }
    }

}

