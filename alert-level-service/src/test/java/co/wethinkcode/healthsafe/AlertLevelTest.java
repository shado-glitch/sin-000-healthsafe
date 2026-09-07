
package co.wethinkcode.healthsafe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlertLevelTest {

    @Test
    void responseStoresLevel() {
        AlertLevelResponse response = new AlertLevelResponse(5);

        assertEquals(5, response.getLevel());
    }

    @Test
    void responseCanSetLevel() {
        AlertLevelResponse response = new AlertLevelResponse(0);

        response.setLevel(8);

        assertEquals(8, response.getLevel());
    }

    @Test
    void requestStoresLevel() {
        AlertLevelRequest request = new AlertLevelRequest();

        request.setLevel(6);

        assertEquals(6, request.getLevel());
    }

    @Test
    void errorResponseStoresMessage() {
        ErrorResponse response = new ErrorResponse("Invalid level");

        assertEquals("Invalid level", response.getError());
    }

    @Test
    void errorResponseCanSetMessage() {
        ErrorResponse response = new ErrorResponse();

        response.setError("Test error");

        assertEquals("Test error", response.getError());
    }
}

