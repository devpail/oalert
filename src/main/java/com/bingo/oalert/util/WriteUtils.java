package com.bingo.oalert.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteUtils {


    public static String checkinDataFilePath = "checkinDataFile" + File.separator + "data.txt";

    private static Logger logger = LoggerFactory.getLogger(WriteUtils.class);

    public static void writeResult(String data, String filePath) throws IOException {
        String []filePaths = filePath.split("[.]");
        if(filePath.length() > 0){
            filePath = filePaths[0] + System.currentTimeMillis() + "." + filePaths[1];
        }
        File file = new File(filePath);
        File dir = file.getParentFile();
        if(!dir.exists()){
            dir.mkdir();
        }
        //if file doesnt exists, then create it
        if(!file.exists()){
            file.createNewFile();
        }
        //true = append file
        FileWriter fileWritter = new FileWriter(filePath,true);
        fileWritter.write(data);
        fileWritter.close();

        logger.info("数据已经写入文件");
    }
}
