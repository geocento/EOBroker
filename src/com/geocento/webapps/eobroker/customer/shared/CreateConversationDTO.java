package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 20/09/2016.
 */
public class CreateConversationDTO {

    Long companyId;
    String topic;

    public CreateConversationDTO() {
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
