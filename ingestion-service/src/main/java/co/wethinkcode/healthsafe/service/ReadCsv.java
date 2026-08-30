package co.wethinkcode.healthsafe.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import co.wethinkcode.healthsafe.model.Ward;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the legacy CSV and turns each valid row into a cleaned Ward.
 */
public class ReadCsv {

    private final List<Ward> wards = new ArrayList<>();

    public ReadCsv(String filename) {
        InputStream inputStream = openResource(filename);

        if (inputStream == null) {
            throw new IllegalArgumentException("Could not find CSV file: " + filename);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            // The first row contains the column headings.
            reader.readNext();

            String[] row;

            while ((row = reader.readNext()) != null) {
                if (isBlankRow(row)) {
                    continue;
                }

                if (row.length < 4) {
                    System.out.println("Skipping malformed row: " + String.join(",", row));
                    continue;
                }

                Ward ward = new Ward(row[0], row[1], row[2], row[3]);

                if (ward.getWardId() == null || ward.getWardId().isBlank()) {
                    System.out.println("Skipping row with missing ward ID: " + String.join(",", row));
                    continue;
                }

                addWardIfNotDuplicate(ward);
            }
        } catch (IOException | CsvValidationException e) {
            throw new RuntimeException("Could not read CSV file: " + filename, e);
        }
    }

    private InputStream openResource(String filename) {
        String resourceName = filename.startsWith("/")
                ? filename.substring(1)
                : filename;

        return getClass().getClassLoader().getResourceAsStream(resourceName);
    }
    private void addWardIfNotDuplicate(Ward ward) {

    System.out.println("Trying to add: " + ward.getWardId());

    for (Ward existingWard : wards) {

        System.out.println(
                "Comparing "
                        + existingWard.getWardId()
                        + " with "
                        + ward.getWardId()
        );

        if (existingWard.getWardId().equalsIgnoreCase(ward.getWardId())) {

            System.out.println(
                    "DUPLICATE FOUND: " + ward.getWardId()
            );

            return;
        }
    }

    wards.add(ward);

    System.out.println(
            "ADDED: " + ward.getWardId()
    );
}

    private boolean isBlankRow(String[] row) {
        for (String value : row) {
            if (value != null && !value.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    

    public List<Ward> getWards() {
        return new ArrayList<>(wards);
    }
}
