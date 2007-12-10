package org.milyn.javabean;

import java.util.List;

/**
 * @author
 */
public class Order {
    private Header header;
    private List<OrderItem> orderItems;
    private OrderItem[] orderItemsArray;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setOrderItems(OrderItem[] orderItems) {
        this.orderItemsArray = orderItems;
    }

    public OrderItem[] getOrderItemsArray() {
        return orderItemsArray;
    }
}
