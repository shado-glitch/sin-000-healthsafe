package co.wethinkcode.healthsafe.Model;

import java.util.Arrays;

public class Ward {
    
    private String wardId;
    private String wing;
    private String department;
    private String bedsAvailable;
    private String notes;
    private String[] missingvalues = {"N/A", "n/a", "TBD", "unknown", "-", "NaN","full"};
  

    public Ward(String wardId, String wing, String department, String bedsAvailable) {
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;
        
        
        if(this.returnNegativeSymbol().equals(bedsAvailable.substring(0,1))){

            this.bedsAvailable = null ;
            this.notes = "bedsAvailable was  non-positive numeric('" + bedsAvailable + "')  — flagged for follow-up" ; 

        }else if(Arrays.asList(missingvalues).contains(bedsAvailable) || (bedsAvailable == null && bedsAvailable.isEmpty())){

            this.notes ="bedsAvailable was non-numeric (" + bedsAvailable + ") — flagged for follow-up";
            this.bedsAvailable = null;

        }else if(WordToNumber.wordsToNumber(bedsAvailable) !=  -1){

    
            this.bedsAvailable = Integer.toString(WordToNumber.wordsToNumber(bedsAvailable));
            this.notes = "bedsAvailable was non-numeric (" + bedsAvailable + ") — flagged for follow-up";
            
             
        }else {
            this.bedsAvailable = bedsAvailable;
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


    public String getBedsAvailable() {
        return bedsAvailable;
    }


    private String returnNegativeSymbol(){
        return "-";
    }

    @Override
    public String toString() {
    return "Ward{" +
            " wardId=' " + this.getWardId() + '\'' +
            ", wing=' " + this.getWing() + '\'' +
            ", department=' " + this.getDepartment() +'\'' +
            ", bedsAvailable= '" + this.getBedsAvailable() + '\'' +
            ", notes= '"+this.notes+ '\'' +
            '}';
    }



    public static void main(String[] args){
        Ward eWard = new Ward("w-17", "North wing", "ICU", "hello");
        System.out.println(eWard.toString());
    }

   
    
}
