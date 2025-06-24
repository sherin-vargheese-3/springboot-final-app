package com.example.demo.service;

import com.example.demo.contract.MessageDTO;
import com.example.demo.kafka.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class MessageServiceTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessMessage_ShouldSendMessage() {
        // Arrange
        MessageDTO dto = new MessageDTO();
        dto.setContent("Test Kafka Message");

        // Act
        messageService.processMessage(dto);

        // Assert
        verify(kafkaProducer, times(1)).sendMessage("Test Kafka Message");
    }

    @Test
    void testProcessUserMessage_ShouldSendMessage() {
        // Arrange
        String msg = "User action: created";

        // Act
        messageService.processUserMessage(msg);

        // Assert
        verify(kafkaProducer, times(1)).sendMessage("User action: created");
    }
}
