// F4 — caso ruim: arrow code. Score esperado: próximo de 0.
// 6 métodos com profundidade >= 3 (todos são violações).
// Classes auxiliares com campos públicos — sem getters — para não
// gerar métodos extras que diluiriam o score.
public class F4Bad {

    // corpo=0 → if=1 → if=2 → for=3 (VIOLAÇÃO)
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

    // corpo=0 → if=1 → for=2 → if=3 (VIOLAÇÃO)
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

    // corpo=0 → for=1 → if=2 → for=3 (VIOLAÇÃO)
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

    // corpo=0 → if=1 → if=2 → for=3 (VIOLAÇÃO)
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

    // corpo=0 → if=1 → for=2 → if=3 → if=4 (VIOLAÇÃO)
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

    // corpo=0 → if=1 → for=2 → if=3 → for=4 → if=5 (VIOLAÇÃO)
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

// Campos públicos intencionalmente: sem getters = sem métodos extras contados pelo F4.
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