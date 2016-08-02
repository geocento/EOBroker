package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * NOT SUPPORTED YET - the response when creating an order
 */
public class CreateOrderResponse extends StatusResponse {

    String orderId;

    public CreateOrderResponse() {
    }

    /**
     * the id of the order
     *
     * @return id
     */
    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
