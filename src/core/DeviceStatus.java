package core;

import java.util.HashMap;
import java.util.Map;

public class DeviceStatus {
    private boolean isOn;
    private Map<String, String> attributes;

    public DeviceStatus(boolean isOn) {
        this.isOn = isOn;
        this.attributes = new HashMap<>();
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean isOn) {
        this.isOn = isOn;
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

    public String getAttribute(String key) {
        return attributes.getOrDefault(key, null);
    }

    public Map<String, String> getAllAttributes() {
        return new HashMap<>(attributes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Status: ").append(isOn ? "ON" : "OFF").append("\n");

        if (!attributes.isEmpty()) {
            sb.append("Attributes:\n");
            for (Map.Entry<String, String> entry : attributes.entrySet()) {
                sb.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
            }
        }

        return sb.toString();
    }
}
