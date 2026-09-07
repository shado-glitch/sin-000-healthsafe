package co.wethinkcode.healthsafe;

public class  ErrorResponse {
    private String error;

    public ErrorResponse() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ErrorResponse(String error) {
        this.error = error;
    }
    
}
