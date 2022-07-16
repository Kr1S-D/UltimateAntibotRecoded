package me.kr1s_d.ultimateantibot.common.utils;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Stream;

public class RuntimeUtil {
    private static IAntiBotPlugin instance;

    public static void setup(IAntiBotPlugin plugin){
        if(instance != null) return;
        instance = plugin;
    }

    public static Process execute(String... command) {
        try {
            String[] args = new String[]{"/bin/bash", "-c"};
            String[] commands = Stream.concat(Arrays.stream(args), Arrays.stream(command)).toArray(String[]::new);
            return new ProcessBuilder(commands).start();
        } catch (IOException e) {
            instance.getLogHelper().error("An error occurred while dispatching: " + Arrays.toString(command) + ", message -> " + e.getMessage());
        }

        return null;
    }

    public static String executeAndGetOutput(String command) {
        try {
            String[] args = new String[] {"/bin/bash", "-c", command};
            StringBuilder stringBuilder = new StringBuilder();
            Process process = new ProcessBuilder(args).start();
            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String str;
            while ((str = bufferedReader1.readLine()) != null)
                stringBuilder.append(str);
            while ((str = bufferedReader2.readLine()) != null)
                stringBuilder.append(str);
            return stringBuilder.toString();
        } catch (IOException e) {
            instance.getLogHelper().error("An error occurred while dispatching: " + command + ", message -> " + e.getMessage());
        }
        return "";
    }
}
