package com.sunxu.rocketmq.domain;

import java.util.ArrayList;
import java.util.List;

public class ProductOrder {
    private long orderId;
    private String type;

    public ProductOrder() {
    }

    public ProductOrder(long orderId, String type) {
        this.orderId = orderId;
        this.type = type;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static List<ProductOrder> getOrderList() {
        List<ProductOrder> list = new ArrayList<>();
        list.add(new ProductOrder(1L, "创建订单"));
        list.add(new ProductOrder(2L, "创建订单"));
        list.add(new ProductOrder(2L, "支付订单"));
        list.add(new ProductOrder(1L, "支付订单"));
        list.add(new ProductOrder(2L, "完成订单"));
        list.add(new ProductOrder(1L, "完成订单"));

        return list;
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "orderId=" + orderId +
                ", type='" + type + '\'' +
                '}';
    }
}
