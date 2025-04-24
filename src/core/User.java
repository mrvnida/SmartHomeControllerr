package core;

public class User implements DeviceObserver {
    private String name;
    private String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public void update(SmartDevice device, String event, String message) {
        System.out.println("[NOTIFICATION to " + name + " (" + email + ")] - " + device.getName() + ": " + event + " - " + message);
    }
}
