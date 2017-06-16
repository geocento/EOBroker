package com.geocento.webapps.eobroker.supplier.server.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.geocento.webapps.eobroker.common.server.websockets.NotificationSocket;
import com.geocento.webapps.eobroker.common.shared.entities.Message;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.WebSocketMessage;
import com.geocento.webapps.eobroker.supplier.shared.dtos.MessageDTO;

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

    public static void sendUserMessage(User user, WebSocketMessage.TYPE type, String destinationId, Message message) throws JsonProcessingException {
        // send message
        WebSocketMessage webSocketMessage = new WebSocketMessage();
        webSocketMessage.setType(type);
        // TODO - replace with a chat ID equivalent, ie each time a user accesses a conversation page create a channel?
        webSocketMessage.setDestination(destinationId);
        webSocketMessage.setMessageDTO(com.geocento.webapps.eobroker.customer.server.utils.MessageHelper.convertToDTO(message));
        NotificationSocket.sendMessage(user.getUsername(), webSocketMessage);
    }

    public static void sendUserConversationMessage(User user, String destinationId, Message message) throws JsonProcessingException {
        sendUserMessage(user, WebSocketMessage.TYPE.conversationMessage, destinationId, message);
    }

    public static void sendUserRequestMessage(User user, String destinationId, Message message) throws JsonProcessingException {
        sendUserMessage(user, WebSocketMessage.TYPE.requestMessage, destinationId, message);
    }
}
