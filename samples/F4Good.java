// F4 — caso bom: aninhamento raso. Score esperado: 100.
// Nenhum método ultrapassa profundidade 2 (corpo = 1, um bloco interno = 2).
// Nenhum bloco dentro de outro bloco — sem for{if{}}, sem if{if{}}.
public class F4Good {

    // depth: body=1, if=2 → max=2 ✅
    public String getOrderStatus(Order order) {
        if (order == null) {
            return "INVALID";
        }
        if (!order.isPaid()) {
            return "PENDING_PAYMENT";
        }
        if (!order.isShipped()) {
            return "PROCESSING";
        }
        return "SHIPPED";
    }

    // depth: body=1, if=2 → max=2 ✅
    public double calculateDiscount(Order order) {
        if (order == null) {
            return 0.0;
        }
        if (order.getTotalValue() >= 500.0) {
            return order.getTotalValue() * 0.15;
        }
        if (order.getTotalValue() >= 200.0) {
            return order.getTotalValue() * 0.10;
        }
        return 0.0;
    }

    // depth: body=1, if=2 → max=2 ✅
    public boolean isEligibleForExpress(Order order) {
        if (order == null) {
            return false;
        }
        if (!order.isPaid()) {
            return false;
        }
        if (order.getWeightKg() > 30.0) {
            return false;
        }
        if (!order.getDestination().equals("CAPITAL")) {
            return false;
        }
        return true;
    }

    // depth: body=1, for=2 → max=2 ✅ (no if inside for)
    public void printOrderSummary(Order[] orders) {
        if (orders == null) {
            return;
        }
        for (Order order : orders) {
            System.out.println(order.getId() + ": " + order.getStatus());
        }
    }

    // depth: body=1, for=2 → max=2 ✅ (continue avoids if-inside-for)
    public int countPendingOrders(Order[] orders) {
        if (orders == null) {
            return 0;
        }
        int count = 0;
        for (Order order : orders) {
            count += "PENDING".equals(order.getStatus()) ? 1 : 0;
        }
        return count;
    }

    // depth: body=1, if=2 → max=2 ✅
    public boolean validateOrder(Order order) {
        if (order == null) {
            return false;
        }
        boolean hasId = order.getId() != null && !order.getId().isEmpty();
        if (!hasId) {
            return false;
        }
        if (order.getTotalValue() <= 0) {
            return false;
        }
        return order.getDestination() != null;
    }
}

class Order {
    private String id;
    private String status;
    private boolean paid;
    private boolean shipped;
    private double totalValue;
    private double weightKg;
    private String destination;

    public String getId()          { return id; }
    public String getStatus()      { return status; }
    public boolean isPaid()        { return paid; }
    public boolean isShipped()     { return shipped; }
    public double getTotalValue()  { return totalValue; }
    public double getWeightKg()    { return weightKg; }
    public String getDestination() { return destination; }
}