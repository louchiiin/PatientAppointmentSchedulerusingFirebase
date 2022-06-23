package com.example.patientappointmentscheduler_usingfirebase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperUtilities {
    public static boolean isEmptyOrNull;

    public static String filter(String input) {

        if (!hasSpecialChars(input)) {
            return (input);
        }
        StringBuilder sb = new StringBuilder(input.length());
        char c;

        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();

    }

    public static String formatDate(int y, int m, int d){

        try{
            String date = d + "/" + (m+1) +"/" + y;
            Date date1= new SimpleDateFormat("dd/MM/yyyy").parse(date);
            DateFormat fullDf = DateFormat.getDateInstance(DateFormat.FULL);

            return fullDf.format(date1);

        }catch(Exception e){

        }

        return null;
    }

    private static boolean hasSpecialChars(String input) {

        Pattern regexSpecialChars = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Matcher inputStr = regexSpecialChars.matcher(input);
        boolean hasSpecialChars = inputStr.find();

        if (!hasSpecialChars) {
            return false;
        }

        return true;
    }
    public static boolean isEmptyOrNull(String param) {
        if (param == null || param.trim().equals("")) {
            return true;
        }
        return false;
    }

    public static boolean isString(String data) {

        if (data.matches("\\d+(?:\\.\\d+)?")) {
            return false;
        }

        return true;
    }

    public static boolean isShortPassword(String password) {
        if (password.length() < 5) {
            return false;
        }
        return true;
    }

    public static boolean isValidUserName(String username){
        if (username.length() > 5) {
            return true;
        }
        return false;
    }

    public static boolean isValidEmail(String email) {
        String regexEmail = "([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";

        if (email.matches(regexEmail)) {
            return true;
        }
        return false;
    }

    public static boolean isValidPhone(String phone) {
        String regexPhone = "\\d{11}|(?:\\d{3}-){2}\\d{4}|\\(\\d{3}\\)\\d{3}-?\\d{4}";

        if (phone.matches(regexPhone)) {
            return true;
        }
        return false;
    }


}
