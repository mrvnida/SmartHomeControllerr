package core.managers;

import core.SmartDevice;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeviceManager {
    private final List<SmartDevice> devices;
    private final Map<String, List<SmartDevice>> roomDevices;

    public DeviceManager() {
        this.devices = new ArrayList<>();
        this.roomDevices = new HashMap<>();
    }

    public void registerDevice(SmartDevice device, String room) {
        devices.add(device);
        roomDevices.computeIfAbsent(room, k -> new ArrayList<>()).add(device);
        System.out.println("Device " + device.getName() + " registered in " + room);
    }

    public void removeDevice(SmartDevice device) {
        devices.remove(device);
        roomDevices.values().forEach(roomDeviceList -> roomDeviceList.remove(device));
        System.out.println("Device " + device.getName() + " removed");
    }

    public List<SmartDevice> getDevicesInRoom(String room) {
        return roomDevices.getOrDefault(room, new ArrayList<>());
    }

    public List<SmartDevice> getAllDevices() {
        return new ArrayList<>(devices);
    }

    public Map<String, List<SmartDevice>> getRoomDevices() {
        return new HashMap<>(roomDevices);
    }

    public SmartDevice findDeviceByName(String deviceName) {
        return devices.stream()
                .filter(device -> device.getName().equals(deviceName))
                .findFirst()
                .orElse(null);
    }
} 