package strategies;

import core.ControlStrategy;
import core.SmartDevice;

public class ManualControlStrategy implements ControlStrategy {

    @Override
    public String getName() {
        return "Manual Control";
    }

    @Override
    public void control(SmartDevice device, String[] params) {
        if (params != null && params.length > 0) {
            String command = params[0];

            switch (command) {
                case "ON":
                    device.turnOn();
                    break;
                case "OFF":
                    device.turnOff();
                    break;
                default:
                    System.out.println("Unknown manual command: " + command);
                    break;
            }
        }
    }
}
