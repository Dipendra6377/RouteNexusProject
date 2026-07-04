package com.routing.config;

import com.routing.pubsub.RoutingEventSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
@RequiredArgsConstructor
public class RedisPubSubConfig {

    @Bean
    RedisMessageListenerContainer container(
            RedisConnectionFactory factory,
            MessageListenerAdapter adapter) {

        RedisMessageListenerContainer container =
                new RedisMessageListenerContainer();

        container.setConnectionFactory(factory);

        container.addMessageListener(
                adapter,
                new PatternTopic("routing-events"));

        return container;
    }

    @Bean
    MessageListenerAdapter adapter(
            RoutingEventSubscriber subscriber) {

        return new MessageListenerAdapter(
                subscriber,
                "receiveMessage");

    }

}