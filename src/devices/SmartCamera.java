package devices;

import core.SmartHomeHub;

public class SmartCamera extends BaseSmartDevice {

    private String mode; // NORMAL, AWAY, PET
    private boolean motionDetected;
    private boolean recordingActive;

    public SmartCamera(String name) {
        super(name);
        this.mode = "NORMAL"; // Default mode
        this.motionDetected = false;
        this.recordingActive = false;

        status.setAttribute("mode", mode);
        status.setAttribute("motionDetected", String.valueOf(motionDetected));
        status.setAttribute("recordingActive", String.valueOf(recordingActive));
    }

    public void setMode(String mode) {
        if (mode.equals("NORMAL") || mode.equals("AWAY") || mode.equals("PET")) {
            this.mode = mode;
            status.setAttribute("mode", mode);
            notifyObservers("MODE_CHANGED", "Camera mode set to " + mode);
        }
    }

    public String getMode() {
        return mode;
    }

    public void detectMotion(boolean detected) {
        this.motionDetected = detected;
        status.setAttribute("motionDetected", String.valueOf(motionDetected));

        if (detected) {
            System.out.println(name + ": Motion detected!");

            if (mode.equals("AWAY")) {
                // In AWAY mode, we take a photo and notify users through the hub
                takePhoto();
                startRecording();
                notifyObservers("SECURITY_ALERT", "Motion detected while in AWAY mode!");
            } else if (mode.equals("PET")) {
                // In PET mode, we only take a photo
                takePhoto();
                notifyObservers("PET_ACTIVITY", "Pet activity detected");
            }
        } else {
            System.out.println(name + ": No motion detected");
            if (recordingActive) {
                stopRecording();
            }
        }
    }

    private void takePhoto() {
        System.out.println(name + ": Taking photo...");
        // Simulate photo taking
        System.out.println(name + ": Photo taken");
    }

    private void startRecording() {
        if (!recordingActive) {
            recordingActive = true;
            status.setAttribute("recordingActive", "true");
            System.out.println(name + ": Recording started");
        }
    }

    private void stopRecording() {
        if (recordingActive) {
            recordingActive = false;
            status.setAttribute("recordingActive", "false");
            System.out.println(name + ": Recording stopped");
        }
    }

    @Override
    public void executeAction(String action, String[] params) {
        switch (action) {
            case "SET_MODE":
                if (params != null && params.length > 0) {
                    setMode(params[0]);
                }
                break;
            case "SIMULATE_MOTION":
                if (params != null && params.length > 0) {
                    detectMotion(Boolean.parseBoolean(params[0]));
                }
                break;
            case "TAKE_PHOTO":
                takePhoto();
                break;
            case "START_RECORDING":
                startRecording();
                break;
            case "STOP_RECORDING":
                stopRecording();
                break;
            default:
                super.executeAction(action, params);
                break;
        }
    }
}
