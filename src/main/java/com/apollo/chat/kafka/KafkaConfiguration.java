package com.apollo.chat.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.internals.ConsumerFactory;
import reactor.kafka.receiver.internals.DefaultKafkaReceiver;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.internals.DefaultKafkaSender;
import reactor.kafka.sender.internals.ProducerFactory;

import java.util.Properties;

@Configuration
public class KafkaConfiguration {

    @Value("${chat.kafka.topic}")
    private String chatTopicName;
    @Value("${chat.kafka.partitions}")
    private Integer chatPartition;
    @Value("${chat.kafka.replicas}")
    private Short chatReplicas;
    @Value("${chat.kafka.retention}")
    private String chatRetentionPeriod;
    @Value("${chat.kafka.server}")
    private String bootstrapServer;
    @Value("${chat.kafka.acks}")
    private String acks;
    @Value("${chat.kafka.retries}")
    private String numberOfRetries;
    @Value("${chat.kafka.requestimeout}")
    private String requestTimeout;
    @Value("${chat.kafka.batch}")
    private String batchSize;
    @Value("${chat.kafka.linger}")
    private String linger;
    @Value("${chat.kafka.max-in-flight}")
    private String maxInFlight;
    @Value("${chat.kafka.client-id}")
    private String clientId;
    @Value("${chat.kafka.group-id}")
    private String groupId;
    @Value("${chat.kafka.offset}")
    private String offsetConfig;

    @Bean
    NewTopic chatTopic() {
        return TopicBuilder
                .name(this.chatTopicName)
                .partitions(this.chatPartition)
                .replicas(this.chatReplicas)
                .config(TopicConfig.RETENTION_MS_CONFIG , this.chatRetentionPeriod)
                .build();
    }

    @Bean
    KafkaSender chatKafkaSender() {
        final Properties chatProperties = new Properties();
        chatProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        chatProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG , StringSerializer.class);
        chatProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG , JsonSerializer.class);
        chatProperties.put(ProducerConfig.ACKS_CONFIG , this.acks);

        chatProperties.put(ProducerConfig.RETRIES_CONFIG , this.numberOfRetries);
        chatProperties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG , this.requestTimeout);
        chatProperties.put(ProducerConfig.BATCH_SIZE_CONFIG , this.batchSize);
        chatProperties.put(ProducerConfig.LINGER_MS_CONFIG , this.linger);
        chatProperties.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION , this.maxInFlight);

        return new DefaultKafkaSender(ProducerFactory.INSTANCE , SenderOptions.create(chatProperties));
    }

    @Bean
    KafkaReceiver chatKafkaReceiver() {
        final Properties chatProperties = new Properties();
        chatProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG , this.bootstrapServer);
        chatProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG , StringDeserializer.class);
        chatProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG , JsonDeserializer.class);
        chatProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG , true);
        chatProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG , this.offsetConfig);

        return new DefaultKafkaReceiver(ConsumerFactory.INSTANCE , ReceiverOptions.create(chatProperties));
    }

}
