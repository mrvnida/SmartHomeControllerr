package core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Singleton pattern implementation using Bill Pugh's Solution
public class SmartHomeHub {

    private List<SmartDevice> devices;
    private List<User> registeredUsers;
    private Map<String, List<SmartDevice>> roomDevices;

    // Bill Pugh Singleton Implementation
    private SmartHomeHub() {
        devices = new ArrayList<>();
        registeredUsers = new ArrayList<>();
        roomDevices = new HashMap<>();
    }

    private static class SingletonHelper {
        private static final SmartHomeHub INSTANCE = new SmartHomeHub();
    }

    public static SmartHomeHub getInstance() {
        return SingletonHelper.INSTANCE;
    }

    // Device management
    public void registerDevice(SmartDevice device, String room) {
        devices.add(device);

        // Add device to room mapping
        if (!roomDevices.containsKey(room)) {
            roomDevices.put(room, new ArrayList<>());
        }
        roomDevices.get(room).add(device);

        System.out.println("Device " + device.getName() + " registered in " + room);
    }

    public void removeDevice(SmartDevice device) {
        devices.remove(device);

        // Remove from room mapping
        for (List<SmartDevice> roomDeviceList : roomDevices.values()) {
            roomDeviceList.remove(device);
        }

        System.out.println("Device " + device.getName() + " removed");
    }

    // User management
    public void registerUser(User user) {
        registeredUsers.add(user);
        System.out.println("User " + user.getName() + " registered");
    }

    public void removeUser(User user) {
        registeredUsers.remove(user);
        System.out.println("User " + user.getName() + " removed");
    }

    // Mediator functionality
    public void sendCommand(SmartDevice source, String targetDeviceName, String command, String[] params) {
        for (SmartDevice device : devices) {
            if (device.getName().equals(targetDeviceName)) {
                System.out.println("Hub routing command from " + source.getName() + " to " + targetDeviceName + ": " + command);
                device.executeAction(command, params);
                return;
            }
        }
        System.out.println("Target device not found: " + targetDeviceName);
    }

    public void broadcastNotification(SmartDevice source, String event, String message) {
        System.out.println("[HUB BROADCAST] From " + source.getName() + ": " + event + " - " + message);

        for (User user : registeredUsers) {
            user.update(source, event, message);
        }
    }

    // Iterator implementation for devices
    public DeviceIterator getDeviceIterator() {
        return new DeviceIteratorImpl(devices);
    }

    public DeviceIterator getRoomDeviceIterator(String room) {
        if (roomDevices.containsKey(room)) {
            return new DeviceIteratorImpl(roomDevices.get(room));
        } else {
            return new DeviceIteratorImpl(new ArrayList<>());
        }
    }

    // Inner Iterator class
    public interface DeviceIterator {
        boolean hasNext();
        SmartDevice next();
    }

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

    // Status report
    public void generateStatusReport() {
        System.out.println("\n===== SMART HOME STATUS REPORT =====");

        for (String room : roomDevices.keySet()) {
            System.out.println("\nRoom: " + room);
            System.out.println("-------------------------");

            DeviceIterator iterator = getRoomDeviceIterator(room);
            while (iterator.hasNext()) {
                SmartDevice device = iterator.next();
                System.out.println("Device: " + device.getName());
                System.out.println(device.getStatus());
            }
        }

        System.out.println("=================================\n");
    }
}
