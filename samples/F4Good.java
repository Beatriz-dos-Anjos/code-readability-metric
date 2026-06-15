// F4 — caso bom: aninhamento raso
// Nenhum método ultrapassa profundidade 2. Sem linhas com 3+ operadores.
public class F4Good {

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

    public void printOrderSummary(Order[] orders) {
        if (orders == null) {
            return;
        }
        for (Order order : orders) {
            System.out.println(order.getId() + ": " + order.getStatus());
        }
    }

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

    public boolean validateOrder(Order order) {
        if (order == null) {
            return false;
        }
        String id = order.getId();
        boolean hasId = id != null;
        boolean nonEmpty = hasId && !id.isEmpty();
        if (!nonEmpty) {
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