// F4 — caso bom: aninhamento raso. Score esperado: 100.
// Todos os métodos usam early return para evitar blocos aninhados.
// Nenhum método ultrapassa profundidade 2 (corpo do método = 1, um nível de lógica = 2).
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
        return order.getDestination().equals("CAPITAL");
    }

    public void printOrderSummary(Order[] orders) {
        if (orders == null) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }
        for (Order order : orders) {
            System.out.println("Pedido: " + order.getId() + " | Status: " + order.getStatus());
        }
    }

    public int countPendingOrders(Order[] orders) {
        if (orders == null) {
            return 0;
        }
        int count = 0;
        for (Order order : orders) {
            if ("PENDING".equals(order.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public boolean validateOrder(Order order) {
        if (order == null) {
            return false;
        }
        if (order.getId() == null || order.getId().isEmpty()) {
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

    public String getId() { return id; }
    public String getStatus() { return status; }
    public boolean isPaid() { return paid; }
    public boolean isShipped() { return shipped; }
    public double getTotalValue() { return totalValue; }
    public double getWeightKg() { return weightKg; }
    public String getDestination() { return destination; }
}