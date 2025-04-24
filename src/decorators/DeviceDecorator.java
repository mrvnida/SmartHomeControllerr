package decorators;

import core.DeviceObserver;
import core.DeviceStatus;
import core.SmartDevice;

public abstract class DeviceDecorator implements SmartDevice {

    protected SmartDevice decoratedDevice;

    public DeviceDecorator(SmartDevice device) {
        this.decoratedDevice = device;
    }

    @Override
    public String getName() {
        return decoratedDevice.getName();
    }

    @Override
    public void turnOn() {
        decoratedDevice.turnOn();
    }

    @Override
    public void turnOff() {
        decoratedDevice.turnOff();
    }

    @Override
    public boolean isOn() {
        return decoratedDevice.isOn();
    }

    @Override
    public void executeAction(String action, String[] params) {
        decoratedDevice.executeAction(action, params);
    }

    @Override
    public void addObserver(DeviceObserver observer) {
        decoratedDevice.addObserver(observer);
    }

    @Override
    public void removeObserver(DeviceObserver observer) {
        decoratedDevice.removeObserver(observer);
    }

    @Override
    public void notifyObservers(String event, String message) {
        decoratedDevice.notifyObservers(event, message);
    }

    @Override
    public DeviceStatus getStatus() {
        return decoratedDevice.getStatus();
    }

    // Method to detach and return the original device
    public SmartDevice detach() {
        return decoratedDevice;
    }
}
