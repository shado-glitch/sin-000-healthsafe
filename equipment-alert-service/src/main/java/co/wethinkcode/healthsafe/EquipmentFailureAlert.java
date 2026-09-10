
package co.wethinkcode.healthsafe;

public class EquipmentFailureAlert {

    private String wardId;
    private String equipment;
    private String message;

    public EquipmentFailureAlert() {
    }

    public EquipmentFailureAlert(String wardId, String equipment, String message) {
        this.wardId = wardId;
        this.equipment = equipment;
        this.message = message;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getEquipment() {
        return equipment;
    }

    public void setEquipment(String equipment) {
        this.equipment = equipment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

