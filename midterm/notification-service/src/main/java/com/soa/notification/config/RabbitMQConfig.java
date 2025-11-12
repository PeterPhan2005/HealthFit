package com.soa.notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ Configuration
 * 
 * GIẢI THÍCH:
 * - Queue: Nơi chứa messages (như inbox)
 * - Exchange: Nơi nhận messages từ producer (như bưu điện)
 * - Binding: Kết nối Exchange → Queue (như địa chỉ giao hàng)
 * - MessageConverter: Convert Java Object ↔ JSON
 */
@Configuration
public class RabbitMQConfig {

    // Đọc từ application.properties
    @Value("${rabbitmq.queue.payment}")
    private String paymentQueue;

    @Value("${rabbitmq.exchange.payment}")
    private String paymentExchange;

    @Value("${rabbitmq.routing.key.payment}")
    private String paymentRoutingKey;

    /**
     * TẠO QUEUE
     * 
     * Queue.durable(name):
     * - durable = true: Queue sẽ tồn tại ngay cả khi RabbitMQ restart
     * - Nếu false: RabbitMQ restart → queue mất
     * 
     * VÍ DỤ:
     * Queue tên: "payment.completed"
     * Durable: true (không mất khi restart)
     */
    @Bean
    public Queue paymentQueue() {
        return QueueBuilder.durable(paymentQueue).build();
    }

    /**
     * TẠO EXCHANGE
     * 
     * TopicExchange:
     * - Type: topic (có thể dùng wildcard pattern)
     * - Durable: true (không mất khi restart)
     * 
     * VÍ DỤ:
     * Exchange tên: "payment.exchange"
     * Type: topic
     */
    @Bean
    public TopicExchange paymentExchange() {
        return new TopicExchange(paymentExchange);
    }

    /**
     * TẠO BINDING (Kết nối Exchange → Queue)
     * 
     * Binding.bind(queue).to(exchange).with(routingKey)
     * 
     * NGHĨA LÀ:
     * Khi có message gửi đến "payment.exchange" với routing key "payment.completed"
     * → Message sẽ được chuyển vào queue "payment.completed"
     * 
     * LUỒNG:
     * PAYMENT-SERVICE 
     *   → publish to "payment.exchange" with key "payment.completed"
     *   → RabbitMQ route to queue "payment.completed"
     *   → NOTIFICATION-SERVICE consume from queue
     */
    @Bean
    public Binding paymentBinding(Queue paymentQueue, TopicExchange paymentExchange) {
        return BindingBuilder
                .bind(paymentQueue)
                .to(paymentExchange)
                .with(paymentRoutingKey);
    }

    /**
     * MESSAGE CONVERTER
     * 
     * Jackson2JsonMessageConverter:
     * - Convert Java Object → JSON (khi publish)
     * - Convert JSON → Java Object (khi consume)
     * 
     * VÍ DỤ:
     * PaymentEvent object → {"customerId":1,"amount":3500000,...}
     * {"customerId":1,...} → PaymentEvent object
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RABBIT TEMPLATE (Không dùng trong NOTIFICATION-SERVICE)
     * 
     * Nhưng tạo sẵn để có thể test publish message
     * 
     * VÍ DỤ SỬ DỤNG (trong test):
     * rabbitTemplate.convertAndSend(exchange, routingKey, message);
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
