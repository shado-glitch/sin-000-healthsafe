package co.wethinkcode.healthsafe.Model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.ReadOnlyFileSystemException;
import java.util.ArrayList;


public class ReadCsv {

   private String filepath ;
   private BufferedReader reader;
   private String line;
   private ArrayList<Ward> wards;

   public ReadCsv(String filepath){
    this.filepath = filepath;
    this.reader = null;
    this.line = "";
    this.wards = new ArrayList<Ward>();


   }

   public void addWard(Ward ward){
      wards.add(ward);
   }

   public String getFilepath() {
    return filepath;
   }

   public BufferedReader getReader() {
      return reader;
   }

   public String getLine() {
      return line;
   }

   public void readCsvFile() {

      try{
               reader = new BufferedReader(new FileReader(this.getFilepath()));

               reader.readLine();

               while((this.line = reader.readLine()) != null){

                  String [] row =line.split(",");
                  Ward ward = new Ward(row[0], row[1], row[2], row[3]);
                     this.addWard(ward);
               }
               

            }catch(Exception e){
               e.printStackTrace();

            }finally{
               try {
                  reader.close();
               } catch (IOException e) {
         
                  e.printStackTrace();
               }
            
            }


         }

   public void display(){
      for(int i =0 ; i < wards.size();i++){
         System.out.println(wards.get(i).toString());
      }
   }

   public static void main(String[] args) {

      ReadCsv trys = new ReadCsv("/home/wtc/Desktop/Elective projects/sin-000-healthsafe/ingestion-service/src/main/resources/wards-outdated.csv");
      trys.readCsvFile();
      trys.display();
   }
   
}
  