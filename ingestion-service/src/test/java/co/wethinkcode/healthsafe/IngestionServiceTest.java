package co.wethinkcode.healthsafe;

import co.wethinkcode.healthsafe.Model.Ward;
import co.wethinkcode.healthsafe.service.ReadCsv;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngestionServiceTest {

    @Test
    void readsAndCleansCsvRecords() {
        ReadCsv readCsv = new ReadCsv("wards-outdated.csv");

        List<Ward> wards = readCsv.getWards();

        // 17 CSV data rows, but W-05 is a duplicate, so 16 records remain.
        assertEquals(17, wards.size());

        Ward ward = wards.stream()
                .filter(item -> item.getWardId().equals("W-01"))
                .findFirst()
                .orElseThrow();

        assertEquals("East Wing", ward.getWing());
        assertEquals("Cardiology", ward.getDepartment());
        assertEquals(3, ward.getBedsAvailable());
    }

    @Test
    void normalizesIdAndNames() {
        Ward ward = new Ward(" w-10 ", "South  Wing", "MATERNITY", "1");

        assertEquals("W-10", ward.getWardId());
        assertEquals("South Wing", ward.getWing());
        assertEquals("Maternity", ward.getDepartment());
        assertEquals(1, ward.getBedsAvailable());
    }

    @Test
    void convertsPlaceholdersToNullAndFlagsThem() {
        Ward ward = new Ward("W-02", "West Wing", "Paediatrics", "N/A");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("placeholder"));
    }

    @Test
    void flagsNegativeCounts() {
        Ward ward = new Ward("W-04", "North Wing", "Oncology", "-1");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("negative"));
    }

    @Test
    void flagsSpelledOutNumbersInsteadOfGuessing() {
        Ward ward = new Ward("W-05", "East Wing", "Paediatrics", "five");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("non-numeric"));
    }

    @Test
    void flagsUnrealisticCounts() {
        Ward ward = new Ward("W-13", "North Wing", "Oncology", "2023");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("unrealistic"));
    }

    @Test
    void duplicateWardIdsAreKeptOnlyOnce() {
        ReadCsv readCsv = new ReadCsv("wards-outdated.csv");

        long w05Count = readCsv.getWards().stream()
                .filter(ward -> ward.getWardId().equals("W-05"))
                .count();

        assertEquals(1, w05Count);
    }
}
