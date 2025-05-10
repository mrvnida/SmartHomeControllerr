package core;

import core.managers.DeviceManager;
import core.managers.UserManager;
import core.services.CommandRouter;
import core.services.NotificationService;
import java.util.List;

public class SmartHomeHub {
    private final DeviceManager deviceManager;
    private final UserManager userManager;
    private final CommandRouter commandRouter;
    private final NotificationService notificationService;

    public SmartHomeHub() {
        this.deviceManager = new DeviceManager();
        this.userManager = new UserManager();
        this.commandRouter = new CommandRouter(deviceManager);
        this.notificationService = new NotificationService(userManager);
    }

    private static class SingletonHelper {
        private static final SmartHomeHub INSTANCE = new SmartHomeHub();
    }

    public static SmartHomeHub getInstance() {
        return SingletonHelper.INSTANCE;
    }

    // Iterator interface
    public interface DeviceIterator {
        boolean hasNext();
        SmartDevice next();
    }

    // Iterator implementation
    private class DeviceIteratorImpl implements DeviceIterator {
        private List<SmartDevice> devicesList;
        private int position;

        public DeviceIteratorImpl(List<SmartDevice> devicesList) {
            this.devicesList = devicesList;
            this.position = 0;
        }

        @Override
        public boolean hasNext() {
            return position < devicesList.size();
        }

        @Override
        public SmartDevice next() {
            if (hasNext()) {
                return devicesList.get(position++);
            }
            return null;
        }
    }

    // Iterator access methods
    public DeviceIterator getDeviceIterator() {
        return new DeviceIteratorImpl(deviceManager.getAllDevices());
    }

    public DeviceIterator getRoomDeviceIterator(String room) {
        return new DeviceIteratorImpl(deviceManager.getDevicesInRoom(room));
    }

    public void registerDevice(SmartDevice device, String room) {
        deviceManager.registerDevice(device, room);
    }

    public void removeDevice(SmartDevice device) {
        deviceManager.removeDevice(device);
    }

    public void registerUser(User user) {
        userManager.registerUser(user);
    }

    public void removeUser(User user) {
        userManager.removeUser(user);
    }

    public void sendCommand(SmartDevice source, String targetDeviceName, String command, String[] params) {
        commandRouter.routeCommand(source, targetDeviceName, command, params);
    }

    public void broadcastNotification(SmartDevice source, String event, String message) {
        notificationService.broadcastNotification(source, event, message);
    }

    public void generateStatusReport() {
        System.out.println("\n===== SMART HOME STATUS REPORT =====");

        deviceManager.getRoomDevices().forEach((room, devices) -> {
            System.out.println("\nRoom: " + room);
            System.out.println("-------------------------");

            devices.forEach(device -> {
                System.out.println("Device: " + device.getName());
                System.out.println(device.getStatus());
            });
        });

        System.out.println("=================================\n");
    }
}
