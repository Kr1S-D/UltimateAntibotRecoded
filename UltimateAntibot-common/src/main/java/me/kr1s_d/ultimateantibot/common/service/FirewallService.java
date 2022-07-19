package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.IConfiguration;
import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListProfile;
import me.kr1s_d.ultimateantibot.common.utils.RuntimeUtil;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class FirewallService {
    private final IAntiBotPlugin plugin;
    private final IConfiguration configuration;

    private final String ipSetID;
    private final int timeout;
    private final boolean resetOnBlackListClear;
    private final String blacklistCommand;
    private final List<String> unBlacklistCommand;
    private boolean isEnabled;

    private final Queue<String> IPQueue;
    private long blacklisted;

    public FirewallService(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.configuration = plugin.getConfigYml();

        this.ipSetID = configuration.getString("firewall.ip-set-id");
        this.timeout = configuration.getInt("firewall.timeout");
        this.resetOnBlackListClear = configuration.getBoolean("firewall.reset-on-blacklist-clear");
        this.blacklistCommand = configuration.getString("firewall.blacklist-command");
        this.unBlacklistCommand = configuration.getStringList("firewall.un-blacklist-command");
        this.isEnabled = configuration.getBoolean("firewall.enabled");

        this.IPQueue = new ArrayDeque<>();
        this.blacklisted = 0;

        plugin.scheduleRepeatingTask(() -> {
            String ip = IPQueue.poll();

            if(ip == null || IPQueue.size() == 0){
                return;
            }

            plugin.getLogHelper().debug("[FIREWALL] &c" + ip + " &fhas been firewalled!");
            blacklisted++;
            RuntimeUtil.execute(getBlackListCommand(ip));
        }, true, 20L);
    }

    public void enable(){
        if(System.getProperty("os.name").toLowerCase().contains("win")) {
            plugin.getLogHelper().error("Firewall hook is not available for windows!");
            this.isEnabled = false;
            return;
        }
        if(!isEnabled) return;
        plugin.getLogHelper().info("Trying to hook in IPTables & IPSet...");
        if(!checkInstallation()){
            plugin.getLogHelper().error("Unable to hook intro IPTables & IPSet!");
            plugin.getLogHelper().error("It looks like they haven't been installed!");
            isEnabled = false;
            return;
        }
        if(!isEnabled) return;
        setupFirewall();
        plugin.getLogHelper().info("Hooked intro IPTables & IPSet!");

        try {
            plugin.getLogHelper().info("Processing Firewall IPs... (it may take a while!)");

            int processed = 0;
            int percentCheck = 10;
            int total = plugin.getAntiBotManager().getBlackListService().getBlackListedIPS().size();

            for(String p : plugin.getAntiBotManager().getBlackListService().getBlackListedIPS()){
                RuntimeUtil.execute(getBlackListCommand(p));
                processed++;

                int percent = Math.round((float) processed / total * 100);
                if(percent >= percentCheck && total > 500){
                    percentCheck += 10;
                    plugin.getLogHelper().info("[FIREWALL] process status: " + percent + "%");
                }
            }

            plugin.getLogHelper().info("Firewall process completed...");
        }catch (Exception e){
            plugin.getLogHelper().error("Error during firewall initialization!");
        }
    }

    public void shutDownFirewall(){
        if(!isEnabled) return;
        for(String cmd : configuration.getStringList("firewall.shutdown-commands")){
            String cmd1 = cmd.replace("%t%", String.valueOf(timeout)).replace("%options%", "maxelem 200000 timeout").replace("%set%", ipSetID);
            RuntimeUtil.execute(cmd1);
        }
    }

    public void firewall(String ip) {
        if(!isEnabled) return;
        IPQueue.add(ip);
    }

    public void dropIP(String ip){
        if(!isEnabled) return;
        unBlacklist(ip);
    }

    public void drop() {
        if(!isEnabled) return;
        if(!resetOnBlackListClear) return;
        plugin.runTask(() -> RuntimeUtil.execute("ipset flush " + ipSetID), true);
        blacklisted = 0;
    }

    public String getFirewallStatus(){
        return isEnabled ? "ENABLED" : "DISABLED";
    }

    public int getIPQueue(){
        return IPQueue.size();
    }

    public long getBlacklistedIP(){
        return blacklisted;
    }

    private void setupFirewall() {
        RuntimeUtil.execute("ipset destroy " + ipSetID);
        for(String str : configuration.getStringList("firewall.setup-commands")){
            String cmd = str.replace("%t%", String.valueOf(timeout)).replace("%options%", "maxelem 200000 timeout").replace("%set%", ipSetID);
            RuntimeUtil.execute(cmd);
        }
    }

    private boolean checkInstallation() {
        String tables = RuntimeUtil.executeAndGetOutput("iptables --version");
        String ipset = RuntimeUtil.executeAndGetOutput("ipset --version");
        return tables.contains("iptables v") && ipset.contains("ipset v");
    }

    private String getBlackListCommand(String ip) {
        ip = ip.replace("/", "");

        String cmd = "";
        cmd = blacklistCommand.replace("%ip%", ip).replace("%t%", String.valueOf(timeout)).replace("%options%", "timeout").replace("%set%", ipSetID);

        return cmd;
    }

    private void unBlacklist(String ip) {
        ip = ip.replace("/", "");
        for(String str : unBlacklistCommand) {
            String cmd = str.replace("%ip%", ip).replace("%t%", String.valueOf(timeout)).replace("%options%", "timeout").replace("%set%", ipSetID);
            RuntimeUtil.execute(cmd);
        }
    }
}
