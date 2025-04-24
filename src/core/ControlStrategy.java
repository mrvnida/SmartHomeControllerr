package core;

public interface ControlStrategy {
    String getName();
    void control(SmartDevice device, String[] params);
}
