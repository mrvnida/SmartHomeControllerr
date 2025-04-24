package core;

public interface SmartDevice {
    String getName();
    void turnOn();
    void turnOff();
    boolean isOn();
    void executeAction(String action, String[] params);
    void addObserver(DeviceObserver observer);
    void removeObserver(DeviceObserver observer);
    void notifyObservers(String event, String message);
    DeviceStatus getStatus();
}