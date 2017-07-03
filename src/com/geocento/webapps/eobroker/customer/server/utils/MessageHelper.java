package com.geocento.webapps.eobroker.customer.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.geocento.webapps.eobroker.supplier.server.websockets.SupplierNotificationSocket;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Message;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierWebSocketMessage;

import java.util.List;

/**
 * Created by thomas on 13/09/2016.
 */
public class MessageHelper {

    public static List<MessageDTO> convertToDTO(List<Message> messages) {
        return ListUtil.mutate(messages, new ListUtil.Mutate<Message, MessageDTO>() {
            @Override
            public MessageDTO mutate(Message message) {
                return convertToDTO(message);
            }
        });
    }

    public static MessageDTO convertToDTO(Message message) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setId(message.getId());
        messageDTO.setFrom(message.getFrom().getUsername());
        messageDTO.setMessage(message.getMessage());
        messageDTO.setCreationDate(message.getCreationDate());
        return messageDTO;
    }

    public static void sendCompanyMessage(Company company, SupplierWebSocketMessage.TYPE type, String destinationId, Message message) throws JsonProcessingException {
        // send message to supplier
        SupplierWebSocketMessage webSocketMessage = new SupplierWebSocketMessage();
        webSocketMessage.setType(type);
        webSocketMessage.setDestination(destinationId);
        webSocketMessage.setMessageDTO(com.geocento.webapps.eobroker.supplier.server.util.MessageHelper.convertToDTO(message));
        SupplierNotificationSocket.sendMessage(company.getId(), webSocketMessage);
    }

    public static void sendCompanyConversationMessage(Company company, String destinationId, Message message) throws JsonProcessingException {
        sendCompanyMessage(company, SupplierWebSocketMessage.TYPE.conversationMessage, destinationId, message);
    }

    public static void sendCompanyRequestMessage(Company company, String destinationId, Message message) throws JsonProcessingException {
        sendCompanyMessage(company, SupplierWebSocketMessage.TYPE.requestMessage, destinationId, message);
    }
}
