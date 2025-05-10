// com.smarthome.demo/SmartHomeDemo.java
package demo;

import core.*;
import devices.*;
import decorators.*;
import strategies.*;
import observers.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
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
            System.out.println("2. Control devices");
            System.out.println("3. Set Security Mode (Away/Pet/Normal)");
            System.out.println("4. Generate status report");
            System.out.println("5. View event logs");
            System.out.println("6. View security events");
            System.out.println("7. Simulate events");
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
                case 3: simulateCameraModeChange(); break;
                case 4: hub.generateStatusReport(); break;
                case 5: loggingObserver.displayLog(); break;
                case 6: securityObserver.displaySecurityEvents(); break;
                case 7: simulateEvents(); break;
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

        System.out.print("Enter action number: ");
        int actionChoice;
        try {
            actionChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        switch (actionChoice) {
            case 1:
                if (targetDevice.isOn()) {
                    System.out.println("Warning: Device is already on!");
                    controlDevice();
                } else {
                    targetDevice.turnOn();
                }
                break;
            case 2:
                if (!targetDevice.isOn()) {
                    System.out.println("Warning: Device is already off!");
                    controlDevice();
                } else {
                    targetDevice.turnOff();
                }
                break;
        }

    }

    private void simulateEvents() {
        System.out.println("\nSimulating Events...");
        SmartHomeHub.DeviceIterator iterator = hub.getDeviceIterator();

        while (iterator.hasNext()) {
            SmartDevice device = iterator.next();
            if (device instanceof TemperatureSensorDecorator) {
                if(device.getStatus().isOn()) {
                    ((TemperatureSensorDecorator) device).readTemperature();
                }
            } else if (device instanceof HumiditySensorDecorator) {
                if (device.getStatus().isOn()) {
                    ((HumiditySensorDecorator) device).readHumidity();
                }
            } else if (device instanceof MotionSensorDecorator) {
                if(device.getStatus().isOn()) {
                    // Random motion detection
                    boolean motionDetected = Math.random() > 0.5; // 50% chance of motion detection
                    ((MotionSensorDecorator) device).simulateMotion(motionDetected);

                    // Create log
                    SmartDevice innerDevice = ((MotionSensorDecorator) device).getDecoratedDevice();
                    String deviceName = innerDevice.getName();
                    System.out.println(deviceName + " - Motion status: " +
                            (motionDetected ? "Motion Detected" : "No Motion"));
                }
            }
        }

        System.out.println("Events successfully simulated.");
    }

    private void simulateCameraModeChange() {
        System.out.println("\nCamera Mode Simulation:");
        System.out.println("1. Leaving Home (Set all cameras to AWAY mode)");
        System.out.println("2. Coming Home (Set all cameras to NORMAL mode)");
        System.out.println("3. Pet Care Mode (Set all cameras to PET mode)");

        System.out.print("Your choice: ");
        int modeChoice;
        try {
            modeChoice = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return;
        }

        if (modeChoice >= 1 && modeChoice <= 3) {
            // Change modes for all cameras at once
            String newMode;
            switch (modeChoice) {
                case 1: newMode = "AWAY"; break;
                case 2: newMode = "NORMAL"; break;
                case 3: newMode = "PET"; break;
                default: return;
            }

            changeAllCameraModes(newMode);
        }
    }

    // Change modes for all cameras
    private void changeAllCameraModes(String newMode) {
        int changedCount = 0;
        SmartHomeHub.DeviceIterator iterator = hub.getDeviceIterator();

        while (iterator.hasNext()) {
            SmartDevice device = iterator.next();
            SmartCamera camera = null;

            if (device instanceof SmartCamera) {
                camera = (SmartCamera) device;
            } else if (device instanceof MotionSensorDecorator) {
                SmartDevice decoratedDevice = ((MotionSensorDecorator) device).getDecoratedDevice();
                if (decoratedDevice instanceof SmartCamera) {
                    camera = (SmartCamera) decoratedDevice;
                }
            }

            if (camera != null) {
                camera.setMode(newMode);
                changedCount++;
            }
        }

        if (changedCount > 0) {
            System.out.println(changedCount + " cameras set to " + newMode + " mode.");
        } else {
            System.out.println("No cameras found in the system.");
        }
    }


    public static void main(String[] args) {
        SmartHomeDemo demo = new SmartHomeDemo();
        demo.initialize();
        demo.run();
    }
}
