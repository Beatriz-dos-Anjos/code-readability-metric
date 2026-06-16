// Expected score: 90-100
// Reason: Simple interface or class for publishing events.
public class EventPublisher {
    private List<Listener> listeners = new ArrayList<>();
    public void subscribe(Listener l) {
        if (l != null) {
            listeners.add(l);
        }
    }
    public void publish(Event e) {
        for (Listener l : listeners) {
            l.onEvent(e);
        }
    }
}
