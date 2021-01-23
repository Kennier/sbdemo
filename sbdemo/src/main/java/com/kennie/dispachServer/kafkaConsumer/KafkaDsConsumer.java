package com.kennie.dispachServer.kafkaConsumer;

import com.kennie.dispachServer.dispatch.Dispatch;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaDsConsumer {

    @Autowired
    private Dispatch dispatch;

    // 消费监听
    @KafkaListener(topics = {"topic1"})
    public void onMessage1(ConsumerRecord<?, ?> record){
        // 消费的哪个topic、partition的消息,打印出消息内容
        System.out.println("简单消费："+record.topic()+"-"+record.partition()+"-"+record.value());
    }

    // 批量消费监听
//    @KafkaListener(groupId = "consumerGroup2",topics = "${kafka.consume.dsTopic.p2p}")
    @KafkaListener(topics = "${kafka.consume.dsTopic.bind}")
    public void onBindMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("DS消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
                dispatch.handleBindOrOffline(consumerRecord.value());
            }
        }
    }

    // 批量消费监听
//    @KafkaListener(groupId = "consumerGroup2",topics = "${kafka.consume.dsTopic.p2p}")
    @KafkaListener(topics = "${kafka.consume.dsTopic.offline}")
    public void onOfflineMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("DS消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
                dispatch.handleBindOrOffline(consumerRecord.value());
            }
        }
    }

    // 批量消费监听
//    @KafkaListener(groupId = "consumerGroup2",topics = "${kafka.consume.dsTopic.p2p}")
    @KafkaListener(topics = "${kafka.consume.dsTopic.p2p}")
    public void onP2PMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("DS消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
                dispatch.handleP2P(consumerRecord.value());
            }
        }
    }

    // 批量消费监听
//    @KafkaListener(groupId = "consumerGroup2",topics = "${kafka.consume.dsTopic.channel}")
    @KafkaListener(topics = "${kafka.consume.dsTopic.channel}")
    public void onChannelMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("DS消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
                dispatch.handleChannelOrChannelEnter(consumerRecord.value());
            }
        }
    }

    // 批量消费监听
//    @KafkaListener(groupId = "consumerGroup2",topics = "${kafka.consume.dsTopic.channelEnter}")
    @KafkaListener(topics = "${kafka.consume.dsTopic.channelEnter}")
    public void onChannelEnterMessage(ConsumerRecords<Object,String> consumerRecords) {
        for(TopicPartition topicPartition:consumerRecords.partitions()){
            for (ConsumerRecord<Object,String> consumerRecord:consumerRecords.records(topicPartition)){
                System.out.println("DS消费时间："+System.currentTimeMillis()+"  "+consumerRecord.value());
                dispatch.handleChannelOrChannelEnter(consumerRecord.value());
            }
        }
    }

}

