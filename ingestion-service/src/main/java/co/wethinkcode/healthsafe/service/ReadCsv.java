package co.wethinkcode.healthsafe.service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.wethinkcode.healthsafe.Model.Ward;


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

         try{
               reader = new BufferedReader(new FileReader(this.getFilepath()));

               reader.readLine();

               while((this.line = reader.readLine()) != null){

                  String [] row =line.split(",");
                  Ward ward = new Ward(row[0].trim(), row[1].trim(), row[2].trim(), row[3].trim());
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

   public ArrayList<Ward> cleanCsv(){
      ArrayList<Ward> cleanWards = new ArrayList<Ward>();

      for(int i = 0; i < this.getWards().size();i++){

         if(!(cleanWards.contains(this.getWards().get(i)))){
            cleanWards.add(this.getWards().get(i));
         } 
        }

      return cleanWards ;
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

   public List<Ward> getWards() {
      return Collections.unmodifiableList(wards);
   }

 


   

}
  