package com.bankapplication.utils;

import java.time.Year;
import java.util.Random;

public class AccountUtils {

    public static final String ACCOUNT_EXISTS_CODE ="001";
    public static final String ACCOUNT_EXISTS_MESSAGE ="This User Already Has Created An Account";

    public static final String ACCOUNT_CREATED_CODE ="002";
    public static final String ACCOUNT_CREATED_MESSAGE ="Account created successfully!";

    public static String accountNumber(){
        Year currentyear = Year.now();
        int min = 100000;
        int max = 999999;
        int randomNumber = (int) Math.floor(Math.random() * (max - min +1) +min );

        String year = String.valueOf(currentyear);
        String randomnumber = String.valueOf(randomNumber);
        return year + randomNumber;
    }
}
