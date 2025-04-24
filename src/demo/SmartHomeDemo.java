// com.smarthome.demo/SmartHomeDemo.java
package demo;

import core.*;
import devices.*;
import decorators.*;
import strategies.*;
import observers.*;

import java.time.LocalTime;
import java.util.Scanner;

public class SmartHomeDemo {

    private SmartHomeHub hub;
    private LoggingObserver loggingObserver;
    private SecurityObserver securityObserver;
    private Scanner scanner;

    public SmartHomeDemo() {
        hub = SmartHomeHub.getInstance();
        loggingObserver = new LoggingObserver();
        securityObserver = new SecurityObserver();
        scanner = new Scanner(System.in);
    }

    public void initialize() {
        System.out.println("Initializing Smart Home System...");

        // Register users
        User alice = new User("Alice", "alice@home.com");
        User bob = new User("Bob", "bob@home.com");
        hub.registerUser(alice);
        hub.registerUser(bob);

        // Create and register devices
        Light livingRoomLight = new Light("Living Room Light");
        livingRoomLight.addObserver(loggingObserver);
        livingRoomLight.addObserver(securityObserver);
        hub.registerDevice(livingRoomLight, "Living Room");

        AirConditioner livingRoomAC = new AirConditioner("Living Room AC");
        livingRoomAC.addObserver(loggingObserver);
        SmartDevice livingRoomACWithTemp = new TemperatureSensorDecorator(livingRoomAC);
        hub.registerDevice(livingRoomACWithTemp, "Living Room");

        Fan kitchenFan = new Fan("Kitchen Fan");
        kitchenFan.addObserver(loggingObserver);
        SmartDevice kitchenFanWithHumidity = new HumiditySensorDecorator(kitchenFan);
        hub.registerDevice(kitchenFanWithHumidity, "Kitchen");

        SmartCamera entryCamera = new SmartCamera("Entry Camera");
        entryCamera.addObserver(loggingObserver);
        entryCamera.addObserver(securityObserver);
        SmartDevice entryCameraWithMotion = new MotionSensorDecorator(entryCamera);
        hub.registerDevice(entryCameraWithMotion, "Entry Hall");

        // Apply control strategies
        livingRoomAC.setControlStrategy(new TemperatureBasedControlStrategy(26, true));
        livingRoomLight.setControlStrategy(new ScheduledControlStrategy(
                LocalTime.of(19, 0), // On at 7 PM
                LocalTime.of(23, 0)  // Off at 11 PM
        ));

        System.out.println("Smart Home System initialized successfully.");
    }

    public void run() {
        boolean running = true;

        while (running) {
            System.out.println("\n==== SMART HOME CONTROL SYSTEM ====");
            System.out.println("1. Show all devices");
            System.out.println("2. Control a device");
            System.out.println("3. Generate status report");
            System.out.println("4. View event logs");
            System.out.println("5. View security events");
            System.out.println("6. Simulate events");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1: showAllDevices(); break;
                case 2: controlDevice(); break;
                case 3: hub.generateStatusReport(); break;
                case 4: loggingObserver.displayLog(); break;
                case 5: securityObserver.displaySecurityEvents(); break;
                case 6: simulateEvents(); break;
                case 0:
                    running = false;
                    System.out.println("Shutting down Smart Home System...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
        scanner.close();
    }

    private void showAllDevices() {
        System.out.println("\n=== Devices by Room ===");

        String[] rooms = {"Living Room", "Kitchen", "Bedroom", "Entry Hall"};
        for (String room : rooms) {
            System.out.println("\nRoom: " + room);
            System.out.println("-------------------------");

            SmartHomeHub.DeviceIterator roomIterator = hub.getRoomDeviceIterator(room);
            boolean hasDevices = false;

            while (roomIterator.hasNext()) {
                hasDevices = true;
                SmartDevice device = roomIterator.next();
                System.out.println("- " + device.getName() + " (" + (device.isOn() ? "ON" : "OFF") + ")");
            }

            if (!hasDevices) {
                System.out.println("No devices in this room.");
            }
        }
    }

    private void controlDevice() {
        showAllDevices();
        System.out.print("\nEnter device name to control: ");
        String deviceName = scanner.nextLine();

        SmartHomeHub.DeviceIterator iterator = hub.getDeviceIterator();
        SmartDevice targetDevice = null;

        while (iterator.hasNext()) {
            SmartDevice device = iterator.next();
            if (device.getName().equalsIgnoreCase(deviceName)) {
                targetDevice = device;
                break;
            }
        }

        if (targetDevice == null) {
            System.out.println("Device not found.");
            return;
        }

        System.out.println("\nDevice: " + targetDevice.getName());
        System.out.println("Status: " + (targetDevice.isOn() ? "ON" : "OFF"));
        System.out.println("\nActions:");
        System.out.println("1. Turn ON");
        System.out.println("2. Turn OFF");
        System.out.println("3. Execute custom command");

        System.out.print("Enter action number: ");
        int actionChoice;
        try {
            actionChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        switch (actionChoice) {
            case 1: targetDevice.turnOn(); break;
            case 2: targetDevice.turnOff(); break;
            case 3:
                System.out.print("Enter command: ");
                String command = scanner.nextLine();
                targetDevice.executeAction(command, new String[]{});
                break;
            default:
                System.out.println("Invalid choice.");
                break;
        }
    }

    private void simulateEvents() {
        System.out.println("\nSimulating Events...");
        SmartHomeHub.DeviceIterator iterator = hub.getDeviceIterator();

        while (iterator.hasNext()) {
            SmartDevice device = iterator.next();
            if (device instanceof TemperatureSensorDecorator) {
                ((TemperatureSensorDecorator) device).readTemperature();
            } else if (device instanceof HumiditySensorDecorator) {
                ((HumiditySensorDecorator) device).readHumidity();
            } else if (device instanceof MotionSensorDecorator) {
                ((MotionSensorDecorator) device).detectMotion();
            }
        }
    }

    public static void main(String[] args) {
        SmartHomeDemo demo = new SmartHomeDemo();
        demo.initialize();
        demo.run();
    }
}