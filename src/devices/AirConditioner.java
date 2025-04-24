package devices;

public class AirConditioner extends BaseSmartDevice {

    private int temperature;
    private String mode; // COOL, HEAT, FAN
    private int fanSpeed;

    public AirConditioner(String name) {
        super(name);
        this.temperature = 24; // Default temperature in Celsius
        this.mode = "COOL"; // Default mode
        this.fanSpeed = 2; // Default fan speed (1-5)

        status.setAttribute("temperature", String.valueOf(temperature));
        status.setAttribute("mode", mode);
        status.setAttribute("fanSpeed", String.valueOf(fanSpeed));
    }

    public void setTemperature(int temperature) {
        if (temperature >= 16 && temperature <= 30) {
            this.temperature = temperature;
            status.setAttribute("temperature", String.valueOf(temperature));
            notifyObservers("TEMPERATURE_CHANGED", "Temperature set to " + temperature + "°C");
        }
    }

    public int getTemperature() {
        return temperature;
    }

    public void setMode(String mode) {
        if (mode.equals("COOL") || mode.equals("HEAT") || mode.equals("FAN")) {
            this.mode = mode;
            status.setAttribute("mode", mode);
            notifyObservers("MODE_CHANGED", "Mode set to " + mode);
        }
    }

    public String getMode() {
        return mode;
    }

    public void setFanSpeed(int fanSpeed) {
        if (fanSpeed >= 1 && fanSpeed <= 5) {
            this.fanSpeed = fanSpeed;
            status.setAttribute("fanSpeed", String.valueOf(fanSpeed));
            notifyObservers("FAN_SPEED_CHANGED", "Fan speed set to " + fanSpeed);
        }
    }

    public int getFanSpeed() {
        return fanSpeed;
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "SET_TEMP":
                if (params != null && params.length > 0) {
                    try {
                        setTemperature(Integer.parseInt(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid temperature parameter");
                    }
                }
                break;
            case "SET_MODE":
                if (params != null && params.length > 0) {
                    setMode(params[0]);
                }
                break;
            case "SET_FAN":
                if (params != null && params.length > 0) {
                    try {
                        setFanSpeed(Integer.parseInt(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid fan speed parameter");
                    }
                }
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}
