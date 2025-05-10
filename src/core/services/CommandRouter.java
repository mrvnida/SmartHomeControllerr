package core.services;

import core.SmartDevice;
import core.managers.DeviceManager;

public class CommandRouter {
    private final DeviceManager deviceManager;

    public CommandRouter(DeviceManager deviceManager) {
        this.deviceManager = deviceManager;
    }

    public void routeCommand(SmartDevice source, String targetDeviceName, String command, String[] params) {
        SmartDevice targetDevice = deviceManager.findDeviceByName(targetDeviceName);
        
        if (targetDevice != null) {
            System.out.println("Routing command from " + source.getName() + " to " + targetDeviceName + ": " + command);
            targetDevice.executeAction(command, params);
        } else {
            System.out.println("Target device not found: " + targetDeviceName);
        }
    }
} 