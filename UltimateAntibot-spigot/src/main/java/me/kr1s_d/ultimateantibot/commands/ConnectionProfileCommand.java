package me.kr1s_d.ultimateantibot.commands;

import me.kr1s_d.commandframework.objects.SubCommand;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.LimitedList;
import me.kr1s_d.ultimateantibot.common.objects.profile.ConnectionProfile;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.IpEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.NickNameEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ScoreTracker;
import me.kr1s_d.ultimateantibot.common.service.UserDataService;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MessageManager;
import me.kr1s_d.ultimateantibot.common.utils.TimeUtil;
import me.kr1s_d.ultimateantibot.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ConnectionProfileCommand implements SubCommand {
    private IAntiBotPlugin plugin;
    private UserDataService service;

    public ConnectionProfileCommand(IAntiBotPlugin plugin) {
        this.plugin = plugin;
        this.service = plugin.getUserDataService();
    }

    @Override
    public String getSubCommandId() {
        return "profile";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConnectionProfile pro = getFromNickName(args[1]);
        if(pro == null) {
            sender.sendMessage(Utils.colora(MessageManager.prefix + "&fUnable to find the player &c" + args[1]));
            sender.sendMessage(Utils.colora("&7This command supports offline profiles so make sure you spell the player's name correctly (including uppercase and lowercase letters)"));
            return;
        }


        LimitedList<NickNameEntry> nickHistory = pro.getLastNickNames();
        LimitedList<IpEntry> ipHistory = pro.getLastIPs();

        sender.sendMessage("§8§l§n___________________________________________");
        sender.sendMessage("");
        sender.sendMessage("§f§lRunning §c§lULTIMATE§F§L | ANTIBOT §r§7- V" + plugin.getVersion());
        sender.sendMessage("");
        sender.sendMessage(Utils.colora("&cIP &7» &c" + pro.getIP()));
        sender.sendMessage(Utils.colora("&cCurrent Name &7» &c" + pro.getCurrentNickName()));
        sender.sendMessage(Utils.colora("&cFirst Join &7» &c" + TimeUtil.convertSeconds(pro.getSecondsFromFirstJoin())));
        sender.sendMessage(Utils.colora("&cLast Join &7» &c" + TimeUtil.convertSeconds(pro.getSecondsFromLastJoin())));
        sender.sendMessage(Utils.colora("&cScore &7» &c" + pro.getConnectionScore()));
        sender.sendMessage(Utils.colora("&cTime Played &7» &c" + TimeUtil.convertSeconds(TimeUnit.MINUTES.toSeconds(pro.getMinutePlayed()))));
        sender.sendMessage(Utils.colora("&fLast Nicknames: "));
        for (NickNameEntry s : nickHistory) {
            sender.sendMessage(Utils.colora("&f» &c" + s.getName() + " &7(" + TimeUtil.convertSeconds(s.getSecondsFromLastJoin()) + " ago)"));
        }
        sender.sendMessage(Utils.colora("&fLast IPs: "));
        for (IpEntry s : ipHistory) {
            sender.sendMessage(Utils.colora("&f» &c" + s.getIP() + " &7(" + TimeUtil.convertSeconds(s.getSecondsFromLastJoin()) + " ago)"));
        }

        if(ConfigManger.isDebugModeOnline) {
            sender.sendMessage(Utils.colora("&fScore stats:"));
            for (ScoreTracker.ScoreAddition s : pro.getScoreTracker().getAdditionList()) {
                sender.sendMessage(Utils.colora("&f» &c" + s.toString()));
            }
        }
        sender.sendMessage("§8§l§n___________________________________________");
        sender.sendMessage(Utils.colora("&7PS: If you see that you are suspected as a bot at the beginning it is normal, don't worry, at the beginning all players have a minimum level of suspicion for safety which should not cause problems with the default config."));
    }

    @Override
    public String getPermission() {
        return "uab.command.profile";
    }

    @Override
    public int minArgs() {
        return 2;
    }

    @Override
    public Map<Integer, List<String>> getTabCompleter(CommandSender sender, Command command, String alias, String[] args) {
        Map<String, String> map2 = new HashMap<>();
        for (ConnectionProfile profile : service.getConnectedProfiles()) {
            map2.put(profile.getCurrentNickName(), profile.getIP());
        }

        Map<Integer, List<String>> map = new HashMap<>();
        map.put(1, new ArrayList<>(map2.keySet()));
        return map;
    }

    @Override
    public boolean allowedConsole() {
        return true;
    }

    //Name -> IP mapping
    private Map<String, String> getProfilesMapping() {
        Map<String, String> map = new HashMap<>();
        for (ConnectionProfile profile : service.getProfiles()) {
            for (NickNameEntry name : profile.getLastNickNames()) {
                map.put(name.getName(), profile.getIP());
            }
        }

        return map;
    }

    private ConnectionProfile getFromNickName(String nick) {
        Map<String, String> mapping = getProfilesMapping();
        String s = mapping.get(nick);
        if(s == null) return null;
        return service.getProfile(s);
    }
}
