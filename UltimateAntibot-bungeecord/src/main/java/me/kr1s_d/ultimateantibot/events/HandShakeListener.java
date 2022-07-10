package me.kr1s_d.ultimateantibot.events;

import me.kr1s_d.ultimateantibot.common.objects.profile.BlackListReason;
import me.kr1s_d.ultimateantibot.common.IAntiBotManager;
import me.kr1s_d.ultimateantibot.common.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.utils.ConfigManger;
import me.kr1s_d.ultimateantibot.utils.Utils;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.Handshake;

public class HandShakeListener implements Listener {

    private final IAntiBotManager antiBotManager;

    public HandShakeListener(IAntiBotPlugin plugin){
        this.antiBotManager = plugin.getAntiBotManager();
    }

    @EventHandler
    public void onHandShake(PlayerHandshakeEvent e){
        Handshake handshake = e.getHandshake();
        String ip = Utils.getIP(e.getConnection());

        if (handshake.getRequestedProtocol() > 2) {
            handshake.setRequestedProtocol(2); // converting to join
            if (ConfigManger.blacklistInvalidProtocol) {
                antiBotManager.getBlackListService().blacklist(ip, BlackListReason.STRANGE_PLAYER);
            }
        }
    }
}
