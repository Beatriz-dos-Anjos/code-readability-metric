public class F4Bad {

    public void processAllOrders(OrderBatch batch) {
        if (batch != null) {
            if (batch.orders != null) {
                for (OrderItem order : batch.orders) {
                    if (order.active) {
                        System.out.println("Processando: " + order.id);
                    }
                }
            }
        }
    }

    public double calculateTotalRevenue(OrderBatch[] batches) {
        double total = 0.0;
        if (batches != null) {
            for (OrderBatch batch : batches) {
                if (batch != null) {
                    for (OrderItem order : batch.orders) {
                        total += order.value;
                    }
                }
            }
        }
        return total;
    }

    public void applyDiscounts(OrderBatch[] batches) {
        for (OrderBatch batch : batches) {
            if (batch != null) {
                for (OrderItem order : batch.orders) {
                    if (order.value > 100.0) {
                        order.value *= 0.90;
                    }
                }
            }
        }
    }

    public int countVipOrdersInRegion(OrderBatch[] batches, String region) {
        int count = 0;
        if (batches != null) {
            if (region != null) {
                for (OrderBatch batch : batches) {
                    if (region.equals(batch.region)) {
                        count += batch.orders.length;
                    }
                }
            }
        }
        return count;
    }

    public void flagLateOrders(OrderBatch batch, int deadlineDays) {
        if (batch != null) {
            for (OrderItem order : batch.orders) {
                if (!order.delivered) {
                    if (order.daysElapsed > deadlineDays) {
                        System.out.println("Pedido atrasado: " + order.id);
                    }
                }
            }
        }
    }

    public String findFirstExpiredOrder(OrderBatch[] batches) {
        if (batches != null) {
            for (OrderBatch batch : batches) {
                if (batch.orders != null) {
                    for (OrderItem order : batch.orders) {
                        if (order.expired) {
                            return order.id;
                        }
                    }
                }
            }
        }
        return null;
    }
}

class OrderItem {
    public String id;
    public double value;
    public boolean active;
    public boolean delivered;
    public boolean expired;
    public int daysElapsed;
}

class OrderBatch {
    public String region;
    public OrderItem[] orders;
}