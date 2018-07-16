package com.bingo.oalert.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class WriteUtils {

    private static Logger logger = LoggerFactory.getLogger(WriteUtils.class);

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

        logger.info("数据已经写入文件");
    }
}
