package co.wethinkcode.healthsafe.service;

import java.util.ArrayList;

import co.wethinkcode.healthsafe.Model.Ward;

public class WardService {

    private ArrayList<Ward> cleanRecords = new ArrayList<Ward>()  ;

    public WardService(ReadCsv readCsvObject){

        this.cleanRecords = new ArrayList<>(readCsvObject.getWards());
    }

    @Override

    public String toString(){

        String[] data = {"[","]"};
        String results = data[0];

        for(Ward ward : cleanRecords){
            results = results + "\n"+ "\n" + ward +",";
        }
        


        return (results.substring(0,(results.length()-1))+"\n" + data[1]);
    }

    
}
