package co.wethinkcode.healthsafe.service;

import java.util.ArrayList;
import java.util.List;

import co.wethinkcode.healthsafe.Model.Ward;

public class ParsesCsv {

    
    public static ArrayList<Ward> parsesCsv(List<Ward> oldwards){

        ArrayList<Ward> wards = new ArrayList<Ward>();

        for(int i = 0 ; i < wards.size() ; i++){
           wards.add(oldwards.get(i));
        }

        return wards;


    }

    
}
