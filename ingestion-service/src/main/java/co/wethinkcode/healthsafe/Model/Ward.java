package co.wethinkcode.healthsafe.Model;

import java.util.Arrays;

public class Ward {
    
    private String wardId;
    private String wing;
    private String department;
    private int bedsAvailable;
    private String[] missingvalues = {"N/A", "n/a", "TBD", "unknown", "-", "NaN","full"};
  

    public Ward(String wardId, String wing, String department, String bedsAvailable) {
        this.wardId = wardId;
        this.wing = wing;
        this.department = department;

        try{

            int value = Integer.parseInt(bedsAvailable);
            if(value < 0){
                this.bedsAvailable = 0;
            }else{
                this.bedsAvailable = value;
            }

        }catch(NumberFormatException e){

            if(Arrays.asList(missingvalues).contains(bedsAvailable)){
                this.bedsAvailable = 0;
            }else{
                this.bedsAvailable = WordToNumber.wordsToNumber(bedsAvailable);
            }

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


    public int getBedsAvailable() {
        return bedsAvailable;
    }

    @Override
    public String toString() {
    return "Ward{" +
            "wardId='" + this.wardId + '\'' +
            ", wing='" + this.wing + '\'' +
            ", specialty='" + this.department + '\'' +
            ", beds='" + this.bedsAvailable + '\'' +
            '}';
    }

   
    
}
