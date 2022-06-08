package ua.mike.micro.inventoryservice.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MessageConverter;
import ua.mike.micro.jms.JmsConsumerActions;
import ua.mike.micro.jms.JmsMessageConverter;

/**
 * Created by mike on 01.06.2022 21:51
 */
@Configuration
@RequiredArgsConstructor
public class JmsConfig {

    private final ObjectMapper mapper;

    @Bean
    public MessageConverter messageConverter() {
        return new JmsMessageConverter(mapper);
    }

    @Bean
    public JmsConsumerActions consumerActions() {
        return new JmsConsumerActions(mapper);
    }
}
