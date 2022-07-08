package com.hgz.file;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() throws ParseException {

        Date date = new Date(1652975086448L);


        String t = date.getYear() + "-" + date.getMonth() + "-" + date.getDay() + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();


        System.out.println(date);
        System.out.println(t);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            System.out.println(format.parse(t));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    void test01() throws ParseException {
        Date date = new Date();
        String s = date.toString();

        String s1 = "2022-05-20 22:22:22";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(dateFormat.parse(s1));
    }

}
