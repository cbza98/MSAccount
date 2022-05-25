package com.msntt.MSAccountService.application.helpers;

import java.security.SecureRandom;

public class AccountGeneratorValues {


    public static String NumberGenerate() {
        StringBuilder start = new StringBuilder();
        SecureRandom value = new SecureRandom(); // Compliant for security-sensitive use cases
        byte[] bytes = new byte[20];
        value.nextBytes(bytes);
        // Generar dos valores en base a los tipos de cuenta
        int v1 = value.nextInt(10);
        int v2 = value.nextInt(10);
        start.append(v1).append(v2).append(" ");

        int count = 0;
        int n = 0;
        for (int i = 0; i < 12; i++) {
            if (count == 4) {
                start.append(" ");
                count = 0;
            } else
                n = value.nextInt(10);
            start.append(n);
            count++;

        }
        return start.toString();
    }

}
