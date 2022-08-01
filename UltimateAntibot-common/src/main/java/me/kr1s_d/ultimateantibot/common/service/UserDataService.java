package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IService;
import me.kr1s_d.ultimateantibot.common.utils.FileUtil;
import me.kr1s_d.ultimateantibot.common.utils.SerializeUtil;

import java.util.ArrayList;
import java.util.List;

public class UserDataService implements IService {
    private final IAntiBotPlugin plugin;
    private final LogHelper logHelper;
    private List<String> firstJoin;

    public UserDataService(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.logHelper = plugin.getLogHelper();
        this.firstJoin = new ArrayList<>();
    }

    @Override
    public void load() {
        try{
            String encoded = FileUtil.getEncodedBase64("users.dat", FileUtil.UABFolder.DATA);
            if(encoded == null) {
                firstJoin = new ArrayList<>();
                return;
            }
            firstJoin = SerializeUtil.deserialize(encoded, ArrayList.class);
            if(firstJoin == null) firstJoin = new ArrayList<>();
        }catch (Exception e){
            this.firstJoin = new ArrayList<>();
            logHelper.error("Unable to load users.dat! If error persists contact support please!");
        }
    }

    @Override
    public void unload() {
        FileUtil.writeBase64("users.dat", FileUtil.UABFolder.DATA, firstJoin);
    }

    public void resetFirstJoin(String ip){
        firstJoin.remove(ip);
    }

    public boolean isFirstJoin(String ip){
        boolean b = !firstJoin.contains(ip);
        if(b) firstJoin.add(ip);
        return b;
    }

    public int size(){
        return firstJoin.size();
    }

    public List<String> getIPMap() {
        return firstJoin;
    }
}
