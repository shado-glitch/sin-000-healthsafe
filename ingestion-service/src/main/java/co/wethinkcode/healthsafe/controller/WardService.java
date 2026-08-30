package co.wethinkcode.healthsafe.controller;

import co.wethinkcode.healthsafe.Model.Ward;
import co.wethinkcode.healthsafe.service.ReadCsv;

import java.util.List;

/**
 * Provides the cleaned ward records to the REST application.
 */
public class WardService {

    private final List<Ward> wards;

    public WardService(ReadCsv readCsv) {
        this.wards = readCsv.getWards();
    }

    public List<Ward> getWards() {
        return wards;
    }
}