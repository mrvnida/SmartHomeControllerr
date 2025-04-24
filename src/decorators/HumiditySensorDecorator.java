package decorators;

import core.DeviceStatus;
import core.SmartDevice;

import java.util.Random;

public class HumiditySensorDecorator extends DeviceDecorator {

    private double currentHumidity;
    private double warningThresholdHigh;
    private double warningThresholdLow;
    private Random random;

    public HumiditySensorDecorator(SmartDevice device) {
        super(device);
        this.random = new Random();
        this.currentHumidity = 50.0; // Default starting humidity (%)
        this.warningThresholdHigh = 70.0; // Default high warning threshold
        this.warningThresholdLow = 30.0; // Default low warning threshold

        // Add humidity to status
        decoratedDevice.getStatus().setAttribute("humiditySensor", String.valueOf(currentHumidity));
        decoratedDevice.getStatus().setAttribute("humidityWarningThresholdHigh", String.valueOf(warningThresholdHigh));
        decoratedDevice.getStatus().setAttribute("humidityWarningThresholdLow", String.valueOf(warningThresholdLow));
    }

    public double getCurrentHumidity() {
        return currentHumidity;
    }

    public void setWarningThresholds(double lowThreshold, double highThreshold) {
        this.warningThresholdLow = lowThreshold;
        this.warningThresholdHigh = highThreshold;

        decoratedDevice.getStatus().setAttribute("humidityWarningThresholdLow", String.valueOf(warningThresholdLow));
        decoratedDevice.getStatus().setAttribute("humidityWarningThresholdHigh", String.valueOf(warningThresholdHigh));
    }

    // Simulate a humidity reading
    public void readHumidity() {
        // Simulate a humidity reading with small random changes
        double change = (random.nextDouble() - 0.5) * 4.0; // Change between -2 and +2
        currentHumidity += change;

        // Keep humidity between 0 and 100
        currentHumidity = Math.max(0, Math.min(100, currentHumidity));
        currentHumidity = Math.round(currentHumidity * 10.0) / 10.0; // Round to 1 decimal place

        decoratedDevice.getStatus().setAttribute("humiditySensor", String.valueOf(currentHumidity));

        System.out.println(decoratedDevice.getName() + " humidity sensor reading: " + currentHumidity + "%");

        // Check if humidity is outside warning thresholds
        if (currentHumidity > warningThresholdHigh) {
            decoratedDevice.notifyObservers("HUMIDITY_WARNING",
                    "Humidity is too high: " + currentHumidity + "% (Threshold: " + warningThresholdHigh + "%)");
        } else if (currentHumidity < warningThresholdLow) {
            decoratedDevice.notifyObservers("HUMIDITY_WARNING",
                    "Humidity is too low: " + currentHumidity + "% (Threshold: " + warningThresholdLow + "%)");
        }
    }

    // Simulate a specific humidity for testing
    public void setHumidity(double humidity) {
        if (humidity >= 0 && humidity <= 100) {
            this.currentHumidity = humidity;
            decoratedDevice.getStatus().setAttribute("humiditySensor", String.valueOf(currentHumidity));

            System.out.println(decoratedDevice.getName() + " humidity set to: " + currentHumidity + "%");

            // Check if humidity is outside warning thresholds
            if (currentHumidity > warningThresholdHigh) {
                decoratedDevice.notifyObservers("HUMIDITY_WARNING",
                        "Humidity is too high: " + currentHumidity + "% (Threshold: " + warningThresholdHigh + "%)");
            } else if (currentHumidity < warningThresholdLow) {
                decoratedDevice.notifyObservers("HUMIDITY_WARNING",
                        "Humidity is too low: " + currentHumidity + "% (Threshold: " + warningThresholdLow + "%)");
            }
        }
    }

    @Override
    public DeviceStatus getStatus() {
        DeviceStatus status = super.getStatus();
        // Ensure humidity is in the status
        status.setAttribute("humiditySensor", String.valueOf(currentHumidity));
        return status;
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "READ_HUMIDITY":
                readHumidity();
                break;
            case "SET_HUMIDITY":
                if (params != null && params.length > 0) {
                    try {
                        setHumidity(Double.parseDouble(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid humidity parameter");
                    }
                }
                break;
            case "SET_HUMIDITY_THRESHOLDS":
                if (params != null && params.length > 1) {
                    try {
                        double low = Double.parseDouble(params[0]);
                        double high = Double.parseDouble(params[1]);
                        setWarningThresholds(low, high);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid threshold parameters");
                    }
                }
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}
