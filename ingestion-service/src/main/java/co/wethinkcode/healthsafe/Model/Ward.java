package co.wethinkcode.healthsafe.Model;

import java.util.Arrays;

import co.wethinkcode.healthsafe.service.WordToNumber;

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

    public String getNotes() {
        return notes;
    }


    public String getWardId() {
        return wardId.trim().replaceAll("\\s+"," ");
    }


    public String getWing() {
        return wing.trim().replaceAll("\\s+"," ");
    }


    public String getDepartment() {
        return department.trim().replaceAll("\\s+"," ");
    }


    public String getBedsAvailable() {
        return bedsAvailable;
    }


    private String returnNegativeSymbol(){
        return "-";
    }

    @Override
    public String toString() {


    return "{" + "\n"+
        "wardId: " + this.getWardId() +"," + "\n" +
        "wing: " + this.getWing() +  "," + "\n" + 
        "department: " + this.getDepartment() +"," +"\n"+
        "bedsAvailable: " + this.getBedsAvailable() +","+"\n" +
        "notes: "+this.notes+ "\n" +
            '}';
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj){
            return true;
        }
        if(obj == null || this.getClass() != obj.getClass()){
            return false;
        };

        Ward other = (Ward) obj;

        boolean WardId = this.getWardId().equalsIgnoreCase(other.getWardId());
        boolean wing = (this.getWing()).equalsIgnoreCase(other.getWing());

        return WardId && wing;
    }
 


    

   
    
}
