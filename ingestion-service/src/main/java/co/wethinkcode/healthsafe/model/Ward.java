package co.wethinkcode.healthsafe.model;

public class Ward {

    private String wardId;
    private String wing;
    private String department;
    private Integer bedsAvailable;
    private String notes;

    public Ward(String wardId, String wing, String department, String bedsAvailable) {

        this.wardId = cleanId(wardId);
        this.wing = cleanName(wing);
        this.department = cleanName(department);

        cleanBeds(bedsAvailable);
    }

    private String cleanId(String value) {

        if (value == null) {
            return null;
        }

        return value.trim()
                .replaceAll("\\s+", "")
                .toUpperCase();
    }

    private String cleanName(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim().replaceAll("\\s+", " ");

        if (cleaned.isEmpty() || isPlaceholder(cleaned)) {
            return null;
        }

        String[] words = cleaned.toLowerCase().split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(Character.toUpperCase(word.charAt(0)))
                    .append(word.substring(1));
        }

        return result.toString();
    }

    private boolean isPlaceholder(String value) {
        String lowerValue = value.trim().toLowerCase();

        return lowerValue.equals("n/a")
                || lowerValue.equals("tbd")
                || lowerValue.equals("unknown")
                || lowerValue.equals("-")
                || lowerValue.equals("nan");
    }

    private void cleanBeds(String value) {
        if (value == null || value.trim().isEmpty()) {
            bedsAvailable = null;
            notes = "bedsAvailable was missing — flagged for follow-up";
            return;
        }

        String cleaned = value.trim();
        String lowerValue = cleaned.toLowerCase();

        if (lowerValue.equals("n/a")
                || lowerValue.equals("tbd")
                || lowerValue.equals("unknown")
                || lowerValue.equals("-")
                || lowerValue.equals("nan")
                || lowerValue.equals("full")) {

            bedsAvailable = null;
            notes = "bedsAvailable was a placeholder ('"
                    + cleaned + "') — flagged for follow-up";
            return;
        }

        try {
            int number = Integer.parseInt(cleaned);

            if (number < 0) {
                bedsAvailable = null;
                notes = "bedsAvailable was negative ('"
                        + cleaned + "') — flagged for follow-up";
            } else if (number > 1000) {
                bedsAvailable = null;
                notes = "bedsAvailable was unrealistic ('"
                        + cleaned + "') — flagged for follow-up";
            } else {
                bedsAvailable = number;
                notes = null;
            }
        } catch (NumberFormatException e) {
            // Spelled-out values such as "five" are deliberately flagged,
            // rather than guessed or silently converted.
            bedsAvailable = null;
            notes = "bedsAvailable was non-numeric ('"
                    + cleaned + "') — flagged for follow-up";
        }
    }

   

    public String getWardId() {
        return wardId;
    }

    public String getWing() {
        return wing;
    }

    public String getDepartment() {
        return department;
    }

    public Integer getBedsAvailable() {
        return bedsAvailable;
    }

    public String getNotes() {
        return notes;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Ward other = (Ward) obj;

        return wardId.equalsIgnoreCase(other.wardId);
    }

    @Override
    public int hashCode() {
        return wardId.toUpperCase().hashCode();
    }
}