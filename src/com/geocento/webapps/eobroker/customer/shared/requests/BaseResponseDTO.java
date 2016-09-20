package com.geocento.webapps.eobroker.customer.shared.requests;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 16/09/2016.
 */
public class BaseResponseDTO {

    CompanyDTO company;
    String serviceName;
    String response;
    Date responseDate;
    List<MessageDTO> messages;

    public BaseResponseDTO() {
    }

    public CompanyDTO getCompany() {
        return company;
    }

    public void setCompany(CompanyDTO company) {
        this.company = company;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
