package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 * Created by thomas on 08/03/2016.
 */
public class CreateOrderResponse extends StatusResponse {

    String orderId;

    public CreateOrderResponse() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
