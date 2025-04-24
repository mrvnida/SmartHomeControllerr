package devices;

public class Light extends BaseSmartDevice {

    private int brightness;
    private String color;

    public Light(String name) {
        super(name);
        this.brightness = 100; // Default brightness
        this.color = "White"; // Default color
        status.setAttribute("brightness", String.valueOf(brightness));
        status.setAttribute("color", color);
    }

    public void setBrightness(int brightness) {
        if (brightness >= 0 && brightness <= 100) {
            this.brightness = brightness;
            status.setAttribute("brightness", String.valueOf(brightness));
            notifyObservers("BRIGHTNESS_CHANGED", "Brightness set to " + brightness + "%");
        }
    }

    public int getBrightness() {
        return brightness;
    }

    public void setColor(String color) {
        this.color = color;
        status.setAttribute("color", color);
        notifyObservers("COLOR_CHANGED", "Color set to " + color);
    }

    public String getColor() {
        return color;
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "DIM":
                if (params != null && params.length > 0) {
                    try {
                        setBrightness(Integer.parseInt(params[0]));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid brightness parameter");
                    }
                }
                break;
            case "COLOR":
                if (params != null && params.length > 0) {
                    setColor(params[0]);
                }
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}

