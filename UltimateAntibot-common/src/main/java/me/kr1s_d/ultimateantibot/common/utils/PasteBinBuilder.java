package me.kr1s_d.ultimateantibot.common.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class PasteBinBuilder {

    private List<String> toWrite;
    private String url;
    private boolean isReady;

    public PasteBinBuilder(){
        this.toWrite = new ArrayList<>();
        this.url = null;
        this.isReady = false;
    }

    public PasteBinBuilder addLine(String line){
        toWrite.add(line);
        return this;
    }

    public PasteBinBuilder addAll(List<String> lines){
        toWrite.addAll(lines);
        return this;
    }

    public PasteBinBuilder setLinesToWrite(List<String> lines){
        this.toWrite = lines;
        return this;
    }

    public void pasteSync(){
        try {
            URL url = new URL("https://pastebin.com/api/api_post.php");
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setDoInput(true);
            Map<String,String> arguments = new HashMap<>();
            arguments.put("api_dev_key", "2CF1IHfum6AaC7fnZdYf99k7ML5ZpWGW");
            arguments.put("api_option", "paste");
            arguments.put("api_paste_expire_date", "1W");
            StringBuilder stringBuilder = new StringBuilder();
            for(String toWr : toWrite){
                stringBuilder.append(toWr).append("\n");
            }
            arguments.put("api_paste_code", stringBuilder.toString());
            StringJoiner sj = new StringJoiner("&");
            for(Map.Entry<String,String> entry : arguments.entrySet())
                sj.add(URLEncoder.encode(entry.getKey(), "UTF-8") + "=" + URLEncoder.encode(entry.getValue(), "UTF-8"));
            byte[] out = sj.toString().getBytes(StandardCharsets.UTF_8);
            int length = out.length;
            //http.setFixedLengthStreamingMode(length);
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            http.connect();
            OutputStream os = http.getOutputStream();
            os.write(out);
            InputStream is = http.getInputStream();
            this.url = new BufferedReader(new InputStreamReader(is,StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            this.isReady = true;
        } catch (IOException urlException) {
            this.isReady = true;
            urlException.printStackTrace();
        }
    }

    public boolean isReady() {
        return isReady;
    }

    public String getUrl(){
        return url;
    }

    public static PasteBinBuilder builder(){
        return new PasteBinBuilder();
    }
}
