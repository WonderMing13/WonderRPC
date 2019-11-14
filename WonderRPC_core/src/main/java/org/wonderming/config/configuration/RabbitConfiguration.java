package org.wonderming.config.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author wangdeming
 * @date 2019-11-12 11:37
 **/
@Configuration
@ConditionalOnProperty(prefix = "spring.rabbitmq",name = {"host","port","username","password"})
public class RabbitConfiguration {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Bean
    @ConditionalOnMissingBean
    public RabbitTransactionManager rabbitTransactionManager(ConnectionFactory connectionFactory){
        return new RabbitTransactionManager(connectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @PostConstruct
    public void init(){
        //创建Exchange
        rabbitAdmin.declareExchange(new DirectExchange("exchange.direct"));
        //创建Queue
        rabbitAdmin.declareQueue(new Queue("direct.queue",true));
        //绑定Queue
        rabbitAdmin.declareBinding( new Binding( "direct.queue" , Binding.DestinationType.QUEUE , "exchange.direct" , "direct.queue" , null ) );
    }
}
