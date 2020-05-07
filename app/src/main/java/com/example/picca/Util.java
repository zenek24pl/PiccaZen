package com.example.picca;

import java.text.DecimalFormat;
import java.util.List;

public class Util {

    public static <T> T or(T value, T orDefault) {
        return value == null ? orDefault : value;
    }

    public static String decimPlace(double val, int count) {
        try {
            String format = "0.00";
            switch (count) {
                case 0:
                    format = "0";
                    break;
                case 1:
                    format = "0.0";
                    if (val == Math.round(val)) {
                        return String.valueOf((int) Math.round(val));
                    }
                    break;
                case 2:
                    format = "0.00";
                    break;
                case 3:
                    format = "0.000";
                    break;
                case 4:
                    format = "0.0000";
                    break;
            }


            return new DecimalFormat(format).format(val).replace(".", ",");
        } catch (Exception ex) {
            return String.valueOf(val);
        }
    }

    public static boolean nullOrEmpty(String txt) {
        return txt == null || txt.length() < 1;
    }

    public static <T> boolean nullOrEmpty(List<T> list) {
        return list == null || list.size() == 0;
    }

}