package observers;

import core.DeviceObserver;
import core.SmartDevice;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LoggingObserver implements DeviceObserver {

    private String logFilePath;
    private List<String> inMemoryLog;
    private int maxInMemoryEntries;

    public LoggingObserver() {
        this.logFilePath = null; // In-memory only
        this.inMemoryLog = new ArrayList<>();
        this.maxInMemoryEntries = 100; // Default max entries
    }

    public LoggingObserver(String logFilePath) {
        this.logFilePath = logFilePath;
        this.inMemoryLog = new ArrayList<>();
        this.maxInMemoryEntries = 100; // Default max entries
    }

    @Override
    public void update(SmartDevice device, String event, String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String timestamp = now.format(formatter);

        String logEntry = timestamp + " - " + device.getName() + " - " + event + " - " + message;

        // Add to in-memory log
        inMemoryLog.add(logEntry);
        if (inMemoryLog.size() > maxInMemoryEntries) {
            inMemoryLog.remove(0); // Remove oldest entry
        }

        // Write to file if configured
        if (logFilePath != null) {
            try (PrintWriter out = new PrintWriter(new FileWriter(logFilePath, true))) {
                out.println(logEntry);
            } catch (IOException e) {
                System.err.println("Error writing to log file: " + e.getMessage());
            }
        }
    }

    public void displayLog() {
        System.out.println("\n===== DEVICE EVENT LOG =====");
        for (String entry : inMemoryLog) {
            System.out.println(entry);
        }
        System.out.println("============================\n");
    }

    public void clearLog() {
        inMemoryLog.clear();
        System.out.println("Log cleared");
    }
}
