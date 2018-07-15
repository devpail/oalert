package com.bingo.oalert.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteUtils {


    public static void writeResult(String data) throws IOException {
        File file =new File("result"+System.currentTimeMillis()+".txt");

        //if file doesnt exists, then create it
        if(!file.exists()){
            file.createNewFile();
        }

        //true = append file
        FileWriter fileWritter = new FileWriter(file.getName(),true);
        fileWritter.write(data);
        fileWritter.close();

        System.out.println("Done");
    }
}
