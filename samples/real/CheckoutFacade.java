// Expected score: 90-100
// Reason: Clean facade pattern with clear methods.
public class CheckoutFacade {
    private PaymentService pay;
    private InventoryService inv;
    
    public boolean processOrder(Order order) {
        if (inv.isAvailable(order)) {
            return pay.charge(order.getAmount());
        }
        return false;
    }
}
