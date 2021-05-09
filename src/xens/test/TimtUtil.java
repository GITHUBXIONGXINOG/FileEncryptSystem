package xens.test;

import java.util.concurrent.TimeUnit;

public class TimtUtil {
    public static void main(String[] args) {
       String res =  TimeFormat2(1210);
        System.out.println(res);
    }
    public static String TimeFormat(long num) {
        if (num <= 1000) {
            return ("用时" + num + "ms");
        } else if (num > 1000 && num <= 60000) {
            return ("用时" +String.format("%.2f", num / 1000.0) + "s");
        } else if (num > 60000 && num <= 360000) {
            return ("用时" + String.format("%.2f", num / 60000.0)  + "m");
        } else if (num > 360000) {
            return ("用时" + String.format("%.2f", num / 360000.0)   + "h");
        }
        return "";
    }


    public static String TimeFormat2(long num) {
        if (num <= 1000) {
            return ("用时" + num + "ms");
        } else if (num > 1000 && num <= 60000) {
            long sec = TimeUnit.MILLISECONDS.toSeconds(num);
            long ms = num - sec*1000;
            return ("用时" + sec + "s " + ms + "ms");
        } else if (num > 60000 && num <3600000) {
            long min = TimeUnit.MILLISECONDS.toMinutes(num);
            long sec = TimeUnit.MILLISECONDS.toSeconds(num)-min*60;
            return ("用时" + min  + "m " + sec + "s");
        } else if (num >= 3600000) {
            long hour = TimeUnit.MILLISECONDS.toHours(num);
            long min = TimeUnit.MILLISECONDS.toMinutes(num) - hour*60;
            long sec = TimeUnit.MILLISECONDS.toSeconds(num)- hour*3600 - min*60;
            return ("用时" + hour + "h " + min + "m " + sec + "s");
        }
        return "";
    }
}
