package com.geocento.webapps.eobroker.customer.shared.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Message;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;

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
}
