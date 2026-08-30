package co.wethinkcode.healthsafe.service;

import co.wethinkcode.healthsafe.Model.Ward;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ReadCsv {

    private final List<Ward> wards = new ArrayList<>();

    public ReadCsv(String filename) {

        InputStream inputStream =
                getClass().getClassLoader().getResourceAsStream(filename);

        if (inputStream == null) {
            throw new IllegalArgumentException(
                    "Could not find CSV file: " + filename
            );
        }

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(inputStream))) {

           
            reader.readLine();

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] row = line.split(",", -1);

                if (row.length < 4) {
                    System.out.println(
                            "Skipping malformed row: " + line
                    );
                    continue;
                }

                Ward ward = new Ward(
                        row[0],
                        row[1],
                        row[2],
                        row[3]
                );

                addWardIfNotDuplicate(ward);
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Could not read CSV file", e
            );
        }
    }

    private void addWardIfNotDuplicate(Ward ward) {

        for (Ward existingWard : wards) {

            if (existingWard.equals(ward)) {

                System.out.println(
                        "Duplicate ward detected: "
                                + ward.getWardId()
                );

                return;
            }
        }

        wards.add(ward);
    }

    public List<Ward> getWards() {
        return new ArrayList<>(wards);
    }
}