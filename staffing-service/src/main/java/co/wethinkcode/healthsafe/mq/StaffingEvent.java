
package co.wethinkcode.healthsafe.mq;
 
import java.time.Instant;
 
/**
 * The message body published to "staffing-events-topic" whenever a ward's
 * on-call schedule changes.
 *
 * Kept as a plain, flat POJO (no nested objects) so it's trivial for any
 * subscriber to deserialize with plain Jackson, without needing to share a
 * common model module.
 */
public class StaffingEvent {
 
    private String wardId;
    private int alertLevel;
    private int doctorsRequired;
    private String timestamp;
 
    // No-arg constructor required by Jackson for (de)serialization.
    public StaffingEvent() {
    }
 
    public StaffingEvent(String wardId, int alertLevel, int doctorsRequired) {
        this.wardId = wardId;
        this.alertLevel = alertLevel;
        this.doctorsRequired = doctorsRequired;
        this.timestamp = Instant.now().toString();
    }
 
    public String getWardId() {
        return wardId;
    }
 
    public int getAlertLevel() {
        return alertLevel;
    }
 
    public int getDoctorsRequired() {
        return doctorsRequired;
    }
 
    public String getTimestamp() {
        return timestamp;
    }
 
    public void setWardId(String wardId) {
        this.wardId = wardId;
    }
 
    public void setAlertLevel(int alertLevel) {
        this.alertLevel = alertLevel;
    }
 
    public void setDoctorsRequired(int doctorsRequired) {
        this.doctorsRequired = doctorsRequired;
    }
 
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}