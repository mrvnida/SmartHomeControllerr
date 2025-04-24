package observers;

import core.DeviceObserver;
import core.SmartDevice;
import core.SmartHomeHub;
import core.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SecurityObserver implements DeviceObserver {

    private boolean securityEnabled;
    private List<String> securityEvents;
    private List<String> criticalEventTypes;

    public SecurityObserver() {
        this.securityEnabled = true;
        this.securityEvents = new ArrayList<>();

        // Define critical event types that require immediate attention
        this.criticalEventTypes = new ArrayList<>();
        criticalEventTypes.add("MOTION_DETECTED");
        criticalEventTypes.add("SECURITY_ALERT");
        criticalEventTypes.add("TEMPERATURE_WARNING");
    }

    public void enableSecurity() {
        securityEnabled = true;
        System.out.println("Security monitoring enabled");
    }

    public void disableSecurity() {
        securityEnabled = false;
        System.out.println("Security monitoring disabled");
    }

    @Override
    public void update(SmartDevice device, String event, String message) {
        if (!securityEnabled) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        String securityEntry = timestamp + " - " + device.getName() + " - " + event + " - " + message;
        securityEvents.add(securityEntry);

        // Keep only last 50 events
        if (securityEvents.size() > 50) {
            securityEvents.remove(0);
        }

        // Check if this is a critical event
        if (isCriticalEvent(event)) {
            System.out.println("\n!!! SECURITY ALERT: " + securityEntry + " !!!\n");

            // Notify the hub about critical events
            SmartHomeHub.getInstance().broadcastNotification(
                    device,
                    "CRITICAL_SECURITY_ALERT",
                    "Critical security event detected: " + event + " - " + message
            );
        }
    }

    private boolean isCriticalEvent(String eventType) {
        return criticalEventTypes.contains(eventType);
    }

    public void displaySecurityEvents() {
        System.out.println("\n===== SECURITY EVENT LOG =====");
        for (String entry : securityEvents) {
            System.out.println(entry);
        }
        System.out.println("=============================\n");
    }

    public void addCriticalEventType(String eventType) {
        if (!criticalEventTypes.contains(eventType)) {
            criticalEventTypes.add(eventType);
            System.out.println("Added " + eventType + " to critical events list");
        }
    }

    public void removeCriticalEventType(String eventType) {
        if (criticalEventTypes.remove(eventType)) {
            System.out.println("Removed " + eventType + " from critical events list");
        }
    }
}
