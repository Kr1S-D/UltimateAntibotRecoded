package me.kr1s_d.ultimateantibot.common.objects.profile;

import me.kr1s_d.ultimateantibot.common.core.server.CloudConfig;
import me.kr1s_d.ultimateantibot.common.core.server.packet.SatellitePacket;
import me.kr1s_d.ultimateantibot.common.objects.LimitedList;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.IpEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.MessageEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.entry.NickNameEntry;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ContainerType;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.MetadataContainer;
import me.kr1s_d.ultimateantibot.common.objects.profile.meta.ScoreTracker;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.common.utils.MathUtil;
import me.kr1s_d.ultimateantibot.common.utils.ServerUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ConnectionProfile implements Serializable, SatellitePacket {
    private static final long serialVersionUID = 7293619377351515696L;

    private String ip;
    private String currentNickName;
    private long firstJoinDate;
    private long lastJoin;
    private long minutePlayed;
    private boolean firstJoin;
    private boolean isOnline;

    private long lastServerPing;
    private final ScoreTracker score;
    private final Map<ContainerType, MetadataContainer<String>> metadataContainerMap;


    public ConnectionProfile(String ip) {
        this.ip = ip;
        this.currentNickName = null;
        this.lastJoin = System.currentTimeMillis();
        this.firstJoinDate = System.currentTimeMillis();
        this.minutePlayed = 0;
        this.firstJoin = true;
        this.isOnline = false;

        this.score = new ScoreTracker();
        this.metadataContainerMap = new ConcurrentHashMap<>();
    }

    public void tickMinute() {
        score.checkRemoval();
        minutePlayed++;
    }

    public void trackJoin(String nickname) {
        if(nickname == null) {
            ServerUtil.getInstance().getLogHelper().error("[SEVERE] Null nickname on join, if this error persist please contact discord support!");
            throw new NullPointerException("Null nickname on player join!");
        }

        this.currentNickName = nickname;
        this.lastJoin = System.currentTimeMillis();
        this.isOnline = true;
        score.expireScores(ScoreTracker.ScoreDurationType.EXPIRE_ON_JOIN);

        //nickname change detection
        MetadataContainer<String> nickMeta = getMetadata(ContainerType.KNOWN_NICKNAMES_IP);
        LimitedList<NickNameEntry> nickHistory = nickMeta.getOrPutDefault("nickname-history", LimitedList.class, new LimitedList<>(10));
        LimitedList<IpEntry> ipHistory = nickMeta.getOrPutDefault("ip-history", LimitedList.class, new LimitedList<>(10));
        if(!nickHistory.contains(NickNameEntry.comparable(nickname))) nickHistory.add(new NickNameEntry(nickname, System.currentTimeMillis()));
        if(!ipHistory.contains(IpEntry.comparable(ip))) ipHistory.add(new IpEntry(ip, System.currentTimeMillis()));
        //process abnormal name check
        if(nickHistory.size() > 3) {
            process(ScoreTracker.ScoreID.CHANGE_NAME);
        }

        //join info
        MetadataContainer<String> joinMeta = getMetadata(ContainerType.JOIN_INFO);
        joinMeta.insert("packet-received", true); //default
        if(ConfigManger.getPacketCheckConfig().isEnabled()) {
            process(ScoreTracker.ScoreID.NO_PACKET_CHECK, false);
            joinMeta.insert("packet-received", false);
        }
        joinMeta.incrementInt("join-count", 0);
    }

    public void trackPing() {
        lastServerPing = System.currentTimeMillis();
        score.expireScores(ScoreTracker.ScoreDurationType.EXPIRE_ON_PING);

        //join info
        MetadataContainer<String> joinMeta = getMetadata(ContainerType.JOIN_INFO);
        joinMeta.incrementInt("ping-count", 0);
    }

    public void trackChat(String message) {
        MetadataContainer<String> metadata =  getMetadata(ContainerType.CHAT_HISTORY);
        LimitedList<MessageEntry> history = metadata.getOrPutDefault("chat-history", LimitedList.class, new LimitedList<>(15));
        history.add(new MessageEntry(message, System.currentTimeMillis()));
        metadata.insert("last-message", System.currentTimeMillis());
    }

    public void trackPacket() {
        MetadataContainer<String> metadata = getMetadata(ContainerType.JOIN_INFO);
        metadata.insert("packet-received", true);
        process(ScoreTracker.ScoreID.NO_PACKET_CHECK, true);
    }

    public void trackDisconnect() {
        this.isOnline = false;
        score.expireScores(ScoreTracker.ScoreDurationType.EXPIRE_ON_QUIT);
    }

    public String getIP() {
        return ip;
    }

    public void setIP(String ip) {
        this.ip = ip;
    }

    public String getCurrentNickName() {
        return currentNickName == null ? "_NULL_" : currentNickName;
    }

    public void setCurrentNickName(String currentNickName) {
        this.currentNickName = currentNickName;
    }

    public long getFirstJoinDate() {
        return firstJoinDate;
    }

    public void setFirstJoinDate(long firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
    }

    public long getLastJoin() {
        return lastJoin;
    }

    public void setLastJoin(long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public long getMinutePlayed() {
        return minutePlayed;
    }

    public boolean isFirstJoin() {
        return firstJoin;
    }

    public void setFirstJoin(boolean firstJoin) {
        this.firstJoin = firstJoin;
    }

    public void setMinutePlayed(long minutePlayed) {
        this.minutePlayed = minutePlayed;
    }

    public int getDaysFromLastJoin() {
        return (int) TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastJoin);
    }

    public long getSecondsFromLastPing() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastServerPing);
    }

    public long getSecondsFromLastJoin() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - lastJoin);
    }

    public long getSecondsFromFirstJoin() {
        return TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - firstJoinDate);
    }

    public boolean isOnline() {
        return isOnline;
    }

    public ScoreTracker getScoreTracker() {
        return score;
    }

    public int getConnectionScoreNum() {
        return score.getScore();
    }

    public ConnectionScore getConnectionScore() {
        int score = getConnectionScoreNum();

        if (score >= ConnectionScore.BOT_CONFIRMED.getDetectionScore()) {
            return ConnectionScore.BOT_CONFIRMED;
        } else if (score >= ConnectionScore.ALMOST_BOT.getDetectionScore()) {
            return ConnectionScore.ALMOST_BOT;
        } else if (score >= ConnectionScore.BOT_SUSPECTED.getDetectionScore()) {
            return ConnectionScore.BOT_SUSPECTED;
        } else if (score >= ConnectionScore.SUSPECT_ACTIVITY.getDetectionScore()) {
            return ConnectionScore.SUSPECT_ACTIVITY;
        } else {
            return ConnectionScore.NOT_BOT;
        }
    }

    public LimitedList<NickNameEntry> getLastNickNames() {
        return getMetadata(ContainerType.KNOWN_NICKNAMES_IP).getOrPutDefault("nickname-history", LimitedList.class, new LimitedList<>(10));
    }

    public LimitedList<IpEntry> getLastIPs() {
        return getMetadata(ContainerType.KNOWN_NICKNAMES_IP).getOrPutDefault("ip-history", LimitedList.class, new LimitedList<>(10));
    }

    public LimitedList<MessageEntry> getChatMessages() {
        return getMetadata(ContainerType.CHAT_HISTORY).getOrPutDefault("chat-history", LimitedList.class, new LimitedList<>(15));
    }

    public ConnectionProfile process(ScoreTracker.ScoreID scoreID, Object... o) {
        double multiplier = 1;
        if(ServerUtil.getSecondsFromLastAttack() < 300) {
            multiplier = CloudConfig.m;
            ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] Possible attack detected shortly after another attack ⚠");
        }

        switch (scoreID) {
            case NO_PACKET_CHECK: //no multiplier to avoid false flags
                boolean hasSentPacket = ((Boolean) o[0]);
                if(hasSentPacket) {
                    score.removeScore(scoreID, MathUtil.multiplyDouble(250, multiplier), ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, false);
                    return this;
                }

                score.addScore(scoreID, MathUtil.multiplyDouble(250, multiplier), false, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 30);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " process " + scoreID + " ✔");
                break;
            case IS_FIST_JOIN:
                boolean isFirstJoin = ((Boolean) o[0]);
                if(!isFirstJoin) return this;
                score.addScore(scoreID, MathUtil.multiplyDouble(150, multiplier), false, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 30);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " process " + scoreID + " ✔");
                break;
            case JOIN_NO_PING:
                if(getSecondsFromLastPing() >= 0 && getSecondsFromLastPing() <= 300) {
                    return this;
                }

                score.addScore(scoreID, MathUtil.multiplyDouble(200, multiplier), false, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 30);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " process " + scoreID + " ✔");
                break;
            case ABNORMAL_CHAT_MESSAGE:
                score.addScore(scoreID, MathUtil.multiplyDouble(20, multiplier), true, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 10);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " process " + scoreID + " ✔");
                break;
            case ABNORMAL_NAME:
                score.addScore(scoreID, MathUtil.multiplyDouble(40, multiplier), true, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 15);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " process " + scoreID + " ✔");
                break;
            case CHANGE_NAME:
                score.addScore(scoreID, MathUtil.multiplyDouble(175, multiplier), false, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 5);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " process " + scoreID + " ✔");
                break;
            case AUTH_CHECK_PASS:
                score.addScore(scoreID, ConnectionScore.SUSPECT_ACTIVITY.getDetectionScore(), false, ScoreTracker.ScoreDurationType.EXPIRE_BY_TIME, 10);
                ServerUtil.getInstance().getLogHelper().debug("[CONNECTION PROFILE] " + getCurrentNickName() + " Passed " + scoreID + " but maybe is a bot ✔");
                break;
        }

        return this;
    }

    public void checkMetadata() {
        for (Map.Entry<ContainerType, MetadataContainer<String>> map : metadataContainerMap.entrySet()) {
            if (!map.getValue().isPersistent()) {
                map.getValue().clear();
            }
        }
    }

    @Override
    public String getID() {
        return "0x00";
    }


    @Override
    public void write(DataOutputStream out) {
        try {
            out.writeUTF(ip);
            out.writeUTF(currentNickName);
            out.writeLong(firstJoinDate);
            out.writeLong(lastJoin);
            out.writeLong(minutePlayed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "ConnectionProfile{" +
                "ip='" + ip + '\'' +
                ", nickname='" + currentNickName + '\'' +
                ", firstJoin=" + firstJoinDate +
                ", lastJoin=" + lastJoin +
                ", minutePlayed=" + minutePlayed +
                '}';
    }

    private MetadataContainer<String> getMetadata(ContainerType containerType) {
        return metadataContainerMap.computeIfAbsent(containerType, k -> new MetadataContainer<>(containerType.isPersistence()));
    }

    public enum ConnectionScore {
        NOT_BOT(0),
        SUSPECT_ACTIVITY(200),
        BOT_SUSPECTED(400),
        ALMOST_BOT(600),
        BOT_CONFIRMED(800);

        public final int score;

        ConnectionScore(int score) {
            this.score = score;
        }

        public int getDetectionScore() {
            return score;
        }
    }
}
