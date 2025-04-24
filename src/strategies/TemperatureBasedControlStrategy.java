package strategies;

import core.ControlStrategy;
import core.SmartDevice;

public class TemperatureBasedControlStrategy implements ControlStrategy {

    private double temperatureThreshold;
    private boolean turnOnWhenAboveThreshold;

    public TemperatureBasedControlStrategy(double temperatureThreshold, boolean turnOnWhenAboveThreshold) {
        this.temperatureThreshold = temperatureThreshold;
        this.turnOnWhenAboveThreshold = turnOnWhenAboveThreshold;
    }

    @Override
    public String getName() {
        String condition = turnOnWhenAboveThreshold ? "above" : "below";
        return "Temperature-based Control (Turn ON when " + condition + " " + temperatureThreshold + "°C)";
    }

    public void setTemperatureThreshold(double temperatureThreshold) {
        this.temperatureThreshold = temperatureThreshold;
    }

    public void setTurnOnWhenAboveThreshold(boolean turnOnWhenAboveThreshold) {
        this.turnOnWhenAboveThreshold = turnOnWhenAboveThreshold;
    }

    public void checkAndUpdateDeviceState(SmartDevice device, double currentTemperature) {
        boolean shouldBeOn;

        if (turnOnWhenAboveThreshold) {
            shouldBeOn = currentTemperature > temperatureThreshold;
        } else {
            shouldBeOn = currentTemperature < temperatureThreshold;
        }

        if (shouldBeOn && !device.isOn()) {
            device.turnOn();
        } else if (!shouldBeOn && device.isOn()) {
            device.turnOff();
        }
    }

    @Override
    public void control(SmartDevice device, String[] params) {
        if (params != null && params.length > 0) {
            String command = params[0];

            switch (command) {
                case "CHECK":
                    if (params.length > 1) {
                        try {
                            double currentTemperature = Double.parseDouble(params[1]);
                            checkAndUpdateDeviceState(device, currentTemperature);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid temperature parameter");
                        }
                    }
                    break;
                case "SET_THRESHOLD":
                    if (params.length > 1) {
                        try {
                            double threshold = Double.parseDouble(params[1]);
                            setTemperatureThreshold(threshold);
                            System.out.println("Temperature threshold set to " + threshold + "°C");
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid threshold parameter");
                        }
                    }
                    break;
                case "SET_MODE":
                    if (params.length > 1) {
                        boolean mode = params[1].equalsIgnoreCase("above");
                        setTurnOnWhenAboveThreshold(mode);
                        System.out.println("Mode set to turn ON when temperature is " +
                                (mode ? "above" : "below") + " threshold");
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
