package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WardTest {

    @Test
    void cleansWardId() {
        Ward ward = new Ward(" w-01 ", "east wing", "cardiology", "5");

        assertEquals("W-01", ward.getWardId());
    }

    @Test
    void cleansWingName() {
        Ward ward = new Ward("W-01", "  EAST   WING  ", "cardiology", "5");

        assertEquals("East Wing", ward.getWing());
    }

    @Test
    void cleansDepartmentName() {
        Ward ward = new Ward("W-01", "East Wing", " CARDIOLOGY ", "5");

        assertEquals("Cardiology", ward.getDepartment());
    }

    @Test
    void parsesValidBeds() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "25");

        assertEquals(25, ward.getBedsAvailable());
        assertNull(ward.getNotes());
    }

    @Test
    void missingBedsAreFlagged() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("missing"));
    }

    @Test
    void placeholderBedsAreFlagged() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "N/A");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("placeholder"));
    }

    @Test
    void negativeBedsAreFlagged() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "-2");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("negative"));
    }

    @Test
    void unrealisticBedsAreFlagged() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "2023");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("unrealistic"));
    }

    @Test
    void nonNumericBedsAreFlagged() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "abc");

        assertNull(ward.getBedsAvailable());
        assertTrue(ward.getNotes().contains("non-numeric"));
    }

    @Test
    void placeholderNamesBecomeNull() {
        Ward ward = new Ward("W-01", "N/A", "unknown", "5");

        assertNull(ward.getWing());
        assertNull(ward.getDepartment());
    }

    @Test
    void addNoteAddsNoteToEmptyNotes() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "5");

        ward.addNote("Needs inspection");

        assertEquals("Needs inspection", ward.getNotes());
    }

    @Test
    void addNoteAppendsToExistingNotes() {
        Ward ward = new Ward("W-01", "East Wing", "Cardiology", "N/A");

        ward.addNote("Needs inspection");

        assertTrue(ward.getNotes().contains("placeholder"));
        assertTrue(ward.getNotes().contains("Needs inspection"));
    }

    @Test
    void wardsWithSameIdAreEqual() {
        Ward first = new Ward("W-01", "East Wing", "Cardiology", "5");
        Ward second = new Ward("w-01", "West Wing", "Oncology", "10");

        assertEquals(first, second);
    }
}

