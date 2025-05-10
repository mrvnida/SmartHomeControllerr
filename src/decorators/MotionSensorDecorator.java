package decorators;

import core.DeviceStatus;
import core.SmartDevice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MotionSensorDecorator extends DeviceDecorator {

    private boolean motionDetected;
    private int sensitivityLevel; // 1-5, where 5 is most sensitive
    private List<String> motionEvents;
    private Random random;

    public MotionSensorDecorator(SmartDevice device) {
        super(device);
        this.motionDetected = false;
        this.sensitivityLevel = 3; // Default medium sensitivity
        this.motionEvents = new ArrayList<>();
        this.random = new Random();

        // Add motion sensor data to status
        decoratedDevice.getStatus().setAttribute("motionSensor", String.valueOf(motionDetected));
        decoratedDevice.getStatus().setAttribute("motionSensitivity", String.valueOf(sensitivityLevel));
    }

    public SmartDevice getDecoratedDevice() {
        return this.decoratedDevice;
    }

        public boolean isMotionDetected() {
        return motionDetected;
    }

    public void setSensitivityLevel(int level) {
        if (level >= 1 && level <= 5) {
            this.sensitivityLevel = level;
            decoratedDevice.getStatus().setAttribute("motionSensitivity", String.valueOf(sensitivityLevel));
            System.out.println(decoratedDevice.getName() + " motion sensor sensitivity set to " + level);
        }
    }

    public void detectMotion() {
        // Simulate motion detection based on sensitivity
        int detectionThreshold = 6 - sensitivityLevel; // Invert so higher sensitivity means lower threshold
        boolean detected = random.nextInt(10) >= detectionThreshold;

        if (detected) {
            if (!motionDetected) { // Only notify on state change from no motion to motion
                motionDetected = true;
                recordMotionEvent("Motion detected");
                decoratedDevice.getStatus().setAttribute("motionSensor", "true");
                decoratedDevice.notifyObservers("MOTION_DETECTED", "Motion detected by " + decoratedDevice.getName());
            }
        } else {
            if (motionDetected) { // Only notify on state change from motion to no motion
                motionDetected = false;
                decoratedDevice.getStatus().setAttribute("motionSensor", "false");
                decoratedDevice.notifyObservers("MOTION_STOPPED", "Motion stopped at " + decoratedDevice.getName());
            }
        }
    }

    public void simulateMotion(boolean detected) {
        if (motionDetected != detected) {
            motionDetected = detected;
            decoratedDevice.getStatus().setAttribute("motionSensor", String.valueOf(detected));

            if (detected) {
                recordMotionEvent("Motion simulated");
                decoratedDevice.notifyObservers("MOTION_DETECTED", "Motion detected by " + decoratedDevice.getName());
            } else {
                decoratedDevice.notifyObservers("MOTION_STOPPED", "Motion stopped at " + decoratedDevice.getName());
            }
        }
    }

    private void recordMotionEvent(String event) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        String logEntry = timestamp + " - " + event;
        motionEvents.add(logEntry);

        // Keep only the last 20 events
        if (motionEvents.size() > 20) {
            motionEvents.remove(0);
        }
    }

    public List<String> getMotionEvents() {
        return new ArrayList<>(motionEvents);
    }

    @Override
    public DeviceStatus getStatus() {
        DeviceStatus status = super.getStatus();
        status.setAttribute("motionSensor", String.valueOf(motionDetected));
        status.setAttribute("motionEventsCount", String.valueOf(motionEvents.size()));
        return status;
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "DETECT_MOTION":
                detectMotion();
                break;
            case "SIMULATE_MOTION":
                if (params != null && params.length > 0) {
                    simulateMotion(Boolean.parseBoolean(params[0]));
                }
                break;
            case "SET_SENSITIVITY":
                if (params != null && params.length > 0) {
                    try {
                        setSensitivityLevel(Integer.parseInt(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid sensitivity parameter");
                    }
                }
                break;
            case "GET_MOTION_EVENTS":
                List<String> events = getMotionEvents();
                System.out.println("\nMotion events for " + decoratedDevice.getName() + ":");
                for (String event : events) {
                    System.out.println("  " + event);
                }
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}
