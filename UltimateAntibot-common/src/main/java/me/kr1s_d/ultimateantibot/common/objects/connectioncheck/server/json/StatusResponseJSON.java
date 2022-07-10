package me.kr1s_d.ultimateantibot.common.objects.connectioncheck.server.json;

import me.kr1s_d.ultimateantibot.common.utils.MessageManager;

import java.util.ArrayList;
import java.util.List;

public class StatusResponseJSON {
    public double avgrequest;
    public int lastminuterequest;
    public int currentrequest;
    public int connectedservers;
    public int maxservers;
    public String status;
    public String argument;
    public String lastreboot;
    public String message;

    public List<String> getStatusMessage(long responsetime){
        if(String.valueOf(avgrequest).length() > 3){
            avgrequest = Double.parseDouble(String.valueOf(avgrequest).substring(0, 3));
        }
        if(status == null){
            status = "null";
        }
        if(lastreboot == null){
            lastreboot = "none";
        }
        if(message == null){
            message = "Server online!";
        }
        if(status.contains("OPERATIONAL")){
            status = "&f" + status;
        }else{
            status = "&e" + status;
        }
        List<String> a = new ArrayList<>();
        for (String rep : MessageManager.satelliteStatus){
            a.add(rep
                    .replace("%avg%", "" + avgrequest)
                    .replace("%lastmin%",  "" + lastminuterequest)
                    .replace("%currentreq%", "" + currentrequest)
                    .replace("%currentserver%", "" + connectedservers)
                    .replace("%maxservers%", "" + maxservers)
                    .replace("%status%", status)
                    .replace("%lastreboot%", lastreboot)
                    .replace("%message%", message)
                    .replace("%time%", responsetime + "")
            );
        }
        return a;
    }
}

