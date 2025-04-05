import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class BatteryNotifier {
    private static final String ACCESS_TOKEN = ""; // Replace with your Pushbullet token
    private static final int HIGH_BATTERY_THRESHOLD = 80;
    private static final int LOW_BATTERY_THRESHOLD = 35;
    private static boolean notifiedHigh = false;
    private static boolean notifiedLow = false;

    public static void main(String[] args) {
        System.out.println("Program is running...");

        while (true) {
            BatteryStatus status = getBatteryStatus();
            if (status == null) {
                System.out.println("Could not retrieve battery status.");
            } else {
                System.out.println("Battery: " + status.percentage + "% | Charging: " + status.isCharging);

                if (status.isCharging && status.percentage >= HIGH_BATTERY_THRESHOLD && !notifiedHigh) {
                    sendPushNotification("Unplug Charger",
                            "Battery is at " + status.percentage + "%. Unplug the charger to protect your battery health.");
                    notifiedHigh = true;
                    notifiedLow = false; 
                } else if (!status.isCharging && status.percentage <= LOW_BATTERY_THRESHOLD && !notifiedLow) {
                    sendPushNotification("Plug In Charger",
                            "Battery is low (" + status.percentage + "%). Please connect your charger.");
                    notifiedLow = true;
                    notifiedHigh = false; 
                }

                
                if (status.percentage < HIGH_BATTERY_THRESHOLD && status.percentage > LOW_BATTERY_THRESHOLD) {
                    notifiedHigh = false;
                    notifiedLow = false;
                }
            }

            try {
                Thread.sleep(2 * 60 * 1000); 
            } catch (InterruptedException e) {
                System.out.println("Interrupted. Exiting...");
                break;
            }
        }
    }

    private static class BatteryStatus {
        int percentage;
        boolean isCharging;

        BatteryStatus(int percentage, boolean isCharging) {
            this.percentage = percentage;
            this.isCharging = isCharging;
        }
    }

    private static BatteryStatus getBatteryStatus() {
        try {
            Process process = Runtime.getRuntime().exec("pmset -g batt");
            Scanner scanner = new Scanner(process.getInputStream());
            int percentage = -1;
            boolean isCharging = false;

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.contains("InternalBattery")) {
                    if (line.contains("charging") || line.contains("AC")) {
                        isCharging = true;
                    }
                    String[] parts = line.split("\\s+");
                    for (String part : parts) {
                        if (part.contains("%")) {
                            percentage = Integer.parseInt(part.replaceAll("[^0-9]", ""));
                            break;
                        }
                    }
                }
            }
            scanner.close();
            if (percentage != -1) {
                return new BatteryStatus(percentage, isCharging);
            }
        } catch (Exception e) {
            System.err.println("Error reading battery status: " + e.getMessage());
        }
        return null;
    }

    private static void sendPushNotification(String title, String message) {
        try {
            URL url = new URL("https://api.pushbullet.com/v2/pushes");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Access-Token", ACCESS_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonPayload = String.format("{\"type\": \"note\", \"title\": \"%s\", \"body\": \"%s\"}", title, message);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonPayload.getBytes());
                os.flush();
            }

            int responseCode = conn.getResponseCode();
            System.out.println("Notification sent. Response code: " + responseCode);
        } catch (Exception e) {
            System.err.println("Error sending notification: " + e.getMessage());
        }
    }
}