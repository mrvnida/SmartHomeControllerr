package core;

public interface DeviceObserver {
    void update(SmartDevice device, String event, String message);
}

