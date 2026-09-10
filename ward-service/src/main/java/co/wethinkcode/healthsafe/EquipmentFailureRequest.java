package co.wethinkcode.healthsafe;

/**
 * Request used by ward-service to report an equipment failure.
 *
 * This endpoint represents the moment at which ward-service detects a failure.
 * The actual alert is then sent asynchronously through ActiveMQ.
 */
public class EquipmentFailureRequest {

    private String equipment;
    private String message;

    public EquipmentFailureRequest() {
    }

    public String getEquipment() {
        return equipment;
    }

    public String getMessage() {
        return message;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
