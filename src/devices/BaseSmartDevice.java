package devices;

import core.DeviceObserver;
import core.DeviceStatus;
import core.SmartDevice;
import core.SmartHomeHub;
import strategies.ManualControlStrategy;
import core.ControlStrategy;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSmartDevice implements SmartDevice {

    protected String name;
    protected boolean isOn;
    protected DeviceStatus status;
    protected List<DeviceObserver> observers;
    protected ControlStrategy controlStrategy;

    public BaseSmartDevice(String name) {
        this.name = name;
        this.isOn = true;
        this.status = new DeviceStatus(false);
        this.observers = new ArrayList<>();
        this.controlStrategy = new ManualControlStrategy(); // Default strategy
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void turnOn() {
        if (!isOn) {
            isOn = true;
            status.setOn(true);
            notifyObservers("STATE_CHANGED", "Device turned ON");
        }
    }

    @Override
    public void turnOff() {
        if (isOn) {
            isOn = false;
            status.setOn(false);
            notifyObservers("STATE_CHANGED", "Device turned OFF");
        }
    }

    @Override
    public boolean isOn() {
        return isOn;
    }

    @Override
    public void executeAction(String action, String[] params) {
        controlStrategy.control(this, params);
    }

    public void setControlStrategy(ControlStrategy strategy) {
        this.controlStrategy = strategy;
        System.out.println(name + " control strategy changed to: " + strategy.getName());
    }

    @Override
    public void addObserver(DeviceObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(DeviceObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String event, String message) {
        for (DeviceObserver observer : observers) {
            observer.update(this, event, message);
        }

        // Also notify the hub for system-wide notifications
        SmartHomeHub.getInstance().broadcastNotification(this, event, message);
    }

    @Override
    public DeviceStatus getStatus() {
        return status;
    }
}

