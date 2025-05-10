package core.services;

import core.SmartDevice;
import core.managers.UserManager;

public class NotificationService {
    private final UserManager userManager;

    public NotificationService(UserManager userManager) {
        this.userManager = userManager;
    }

    public void broadcastNotification(SmartDevice source, String event, String message) {
        System.out.println("[NOTIFICATION SERVICE] From " + source.getName() + ": " + event + " - " + message);
        
        userManager.getRegisteredUsers().forEach(user -> 
            user.update(source, event, message)
        );
    }
} 