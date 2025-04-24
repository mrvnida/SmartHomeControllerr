package devices;

public class Fan extends BaseSmartDevice {

    private int speed;
    private boolean oscillating;

    public Fan(String name) {
        super(name);
        this.speed = 1; // Default speed (1-5)
        this.oscillating = false; // Not oscillating by default

        status.setAttribute("speed", String.valueOf(speed));
        status.setAttribute("oscillating", String.valueOf(oscillating));
    }

    public void setSpeed(int speed) {
        if (speed >= 0 && speed <= 5) { // 0 is off, 1-5 is speed level
            this.speed = speed;
            status.setAttribute("speed", String.valueOf(speed));

            if (speed == 0 && isOn) {
                turnOff();
            } else if (speed > 0 && !isOn) {
                turnOn();
            }

            notifyObservers("SPEED_CHANGED", "Fan speed set to " + speed);
        }
    }

    public int getSpeed() {
        return speed;
    }

    public void setOscillating(boolean oscillating) {
        this.oscillating = oscillating;
        status.setAttribute("oscillating", String.valueOf(oscillating));
        notifyObservers("OSCILLATION_CHANGED", "Oscillation " + (oscillating ? "enabled" : "disabled"));
    }

    public boolean isOscillating() {
        return oscillating;
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "SET_SPEED":
                if (params != null && params.length > 0) {
                    try {
                        setSpeed(Integer.parseInt(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid speed parameter");
                    }
                }
                break;
            case "SET_OSCILLATE":
                if (params != null && params.length > 0) {
                    setOscillating(Boolean.parseBoolean(params[0]));
                }
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}

