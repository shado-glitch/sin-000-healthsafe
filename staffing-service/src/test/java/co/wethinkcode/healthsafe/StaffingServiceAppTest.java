package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StaffingServiceAppTest {
     @Test
    void alertLevel0RequiresOneDoctor() {
        assertEquals(1, StaffingServiceApp.calculateDoctors(0));
    }

    @Test
    void alertLevel2RequiresOneDoctor() {
        assertEquals(1, StaffingServiceApp.calculateDoctors(2));
    }

    @Test
    void alertLevel3RequiresTwoDoctors() {
        assertEquals(2, StaffingServiceApp.calculateDoctors(3));
    }

    @Test
    void alertLevel4RequiresTwoDoctors() {
        assertEquals(2, StaffingServiceApp.calculateDoctors(4));
    }

    @Test
    void alertLevel5RequiresThreeDoctors() {
        assertEquals(3, StaffingServiceApp.calculateDoctors(5));
    }

    @Test
    void alertLevel6RequiresThreeDoctors() {
        assertEquals(3, StaffingServiceApp.calculateDoctors(6));
    }

    @Test
    void alertLevel7RequiresFourDoctors() {
        assertEquals(4, StaffingServiceApp.calculateDoctors(7));
    }

    @Test
    void alertLevel8RequiresFourDoctors() {
        assertEquals(4, StaffingServiceApp.calculateDoctors(8));
    }
    
}
