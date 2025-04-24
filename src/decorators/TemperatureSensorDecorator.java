package decorators;

import core.DeviceStatus;
import core.SmartDevice;

import java.util.Random;

public class TemperatureSensorDecorator extends DeviceDecorator {

    private double currentTemperature;
    private double warningThreshold;
    private Random random;

    public TemperatureSensorDecorator(SmartDevice device) {
        super(device);
        this.random = new Random();
        this.currentTemperature = 22.0; // Default starting temperature
        this.warningThreshold = 40.0; // Default warning threshold

        // Add temperature to status
        decoratedDevice.getStatus().setAttribute("temperatureSensor", String.valueOf(currentTemperature));
        decoratedDevice.getStatus().setAttribute("temperatureWarningThreshold", String.valueOf(warningThreshold));
    }

    public double getCurrentTemperature() {
        return currentTemperature;
    }

    public void setWarningThreshold(double threshold) {
        this.warningThreshold = threshold;
        decoratedDevice.getStatus().setAttribute("temperatureWarningThreshold", String.valueOf(warningThreshold));
    }

    // Simulate a temperature reading
    public void readTemperature() {
        // Simulate a temperature reading with small random changes
        double change = (random.nextDouble() - 0.5) * 2.0; // Change between -1 and +1
        currentTemperature += change;
        currentTemperature = Math.round(currentTemperature * 10.0) / 10.0; // Round to 1 decimal place

        decoratedDevice.getStatus().setAttribute("temperatureSensor", String.valueOf(currentTemperature));

        System.out.println(decoratedDevice.getName() + " temperature sensor reading: " + currentTemperature + "°C");

        // Check if temperature exceeds warning threshold
        if (currentTemperature > warningThreshold) {
            decoratedDevice.notifyObservers("TEMPERATURE_WARNING",
                    "Temperature is too high: " + currentTemperature + "°C (Threshold: " + warningThreshold + "°C)");
        }
    }

    // Simulate a specific temperature for testing
    public void setTemperature(double temperature) {
        this.currentTemperature = temperature;
        decoratedDevice.getStatus().setAttribute("temperatureSensor", String.valueOf(currentTemperature));

        System.out.println(decoratedDevice.getName() + " temperature set to: " + currentTemperature + "°C");

        // Check if temperature exceeds warning threshold
        if (currentTemperature > warningThreshold) {
            decoratedDevice.notifyObservers("TEMPERATURE_WARNING",
                    "Temperature is too high: " + currentTemperature + "°C (Threshold: " + warningThreshold + "°C)");
        }
    }

    @Override
    public DeviceStatus getStatus() {
        DeviceStatus status = super.getStatus();
        // Ensure temperature is in the status
        status.setAttribute("temperatureSensor", String.valueOf(currentTemperature));
        return status;
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "READ_TEMPERATURE":
                readTemperature();
                break;
            case "SET_TEMPERATURE":
                if (params != null && params.length > 0) {
                    try {
                        setTemperature(Double.parseDouble(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid temperature parameter");
                    }
                }
                break;
            case "SET_THRESHOLD":
                if (params != null && params.length > 0) {
                    try {
                        setWarningThreshold(Double.parseDouble(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid threshold parameter");
                    }
                }
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}
