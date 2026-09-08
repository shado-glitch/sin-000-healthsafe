package co.wethinkcode.healthsafe.mq;

/**
 * The ward-service side of staffing-service's StaffingEvent. Deliberately a
 * separate, duplicated class (not a shared one) — same reasoning as
 * MqConfig: these are independent services with no shared module, so each
 * side owns its own copy of "what this message looks like to me".
 */
public class StaffingUpdate {

    private String wardId;
    private int alertLevel;
    private int doctorsRequired;
    private String timestamp;

    // No-arg constructor required by Jackson for deserialization.
    public StaffingUpdate() {
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
