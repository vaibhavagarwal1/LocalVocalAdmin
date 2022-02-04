package com.dev.localvocaladmin;

public class RegisteredEmails {

    public static final String[] registeredEmail = {
            "deveshtibarewal529@gmail.com",
            "localvocalvao@gmail.com"
    };

    public static boolean contains(String email) {
        for (String s : registeredEmail) {
            if (s.equalsIgnoreCase(email)) {
                return true;
            }
        }
        return false;
    }

}
