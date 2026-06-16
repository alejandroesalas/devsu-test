package com.devsu.cuentas.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RabbitConfig {

    @Value("${app.messaging.exchange}")
    private String exchange;

    @Value("${app.messaging.queue}")
    private String queue;

    @Value("${app.messaging.routing-key}")
    private String routingKey;

    @Bean
    public TopicExchange clientesExchange() {
        return new TopicExchange(exchange, true, false);
    }

    @Bean
    public Queue clienteQueue() {
        return QueueBuilder.durable(queue).build();
    }

    @Bean
    public Binding clienteBinding(Queue clienteQueue, TopicExchange clientesExchange) {
        return BindingBuilder.bind(clienteQueue).to(clientesExchange).with(routingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            MessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
