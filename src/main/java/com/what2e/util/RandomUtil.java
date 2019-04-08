package com.what2e.util;

import java.util.Random;

public class RandomUtil {

    final static char[] deful = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static String getRandomString(String requestFlag, int lenth) {
        Random random = new Random();
        int[] temp = new int[lenth - 1];
        StringBuilder sbd = new StringBuilder();

        sbd.append(requestFlag);

        for (int i = 0; i < temp.length; i++) {
            temp[i] = random.nextInt(62);
            sbd.append(deful[temp[i]]);
        }
        return sbd.toString();
    }
}
