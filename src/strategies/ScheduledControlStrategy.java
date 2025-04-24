package strategies;

import core.ControlStrategy;
import core.SmartDevice;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ScheduledControlStrategy implements ControlStrategy {

    private LocalTime onTime;
    private LocalTime offTime;

    public ScheduledControlStrategy() {
        // Default schedule: on at 8 AM, off at 10 PM
        this.onTime = LocalTime.of(8, 0);
        this.offTime = LocalTime.of(22, 0);
    }

    public ScheduledControlStrategy(LocalTime onTime, LocalTime offTime) {
        this.onTime = onTime;
        this.offTime = offTime;
    }

    @Override
    public String getName() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return "Scheduled Control (ON: " + onTime.format(formatter) + ", OFF: " + offTime.format(formatter) + ")";
    }

    public void setOnTime(LocalTime onTime) {
        this.onTime = onTime;
    }

    public void setOffTime(LocalTime offTime) {
        this.offTime = offTime;
    }

    public void checkAndUpdateDeviceState(SmartDevice device) {
        LocalTime now = LocalTime.now();

        if (isTimeBetween(now, onTime, offTime)) {
            if (!device.isOn()) {
                device.turnOn();
            }
        } else {
            if (device.isOn()) {
                device.turnOff();
            }
        }
    }

    private boolean isTimeBetween(LocalTime time, LocalTime start, LocalTime end) {
        if (start.isBefore(end)) {
            return !time.isBefore(start) && !time.isAfter(end);
        } else {
            // Handle overnight schedules (e.g., 22:00 to 06:00)
            return !time.isBefore(start) || !time.isAfter(end);
        }
    }

    @Override
    public void control(SmartDevice device, String[] params) {
        if (params != null && params.length > 0) {
            String command = params[0];

            switch (command) {
                case "CHECK":
                    checkAndUpdateDeviceState(device);
                    break;
                case "SET_ON_TIME":
                    if (params.length > 1) {
                        try {
                            LocalTime newOnTime = LocalTime.parse(params[1]);
                            setOnTime(newOnTime);
                            System.out.println("ON time set to " + newOnTime);
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid time format. Use HH:mm");
                        }
                    }
                    break;
                case "SET_OFF_TIME":
                    if (params.length > 1) {
                        try {
                            LocalTime newOffTime = LocalTime.parse(params[1]);
                            setOffTime(newOffTime);
                            System.out.println("OFF time set to " + newOffTime);
                        } catch (DateTimeParseException e) {
                            System.out.println("Invalid time format. Use HH:mm");
                        }
                    }
                    break;
                default:
                    // For other commands, use manual control
                    new ManualControlStrategy().control(device, params);
                    break;
            }
        }
    }
}
