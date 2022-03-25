package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.objects.enums.CheckListenedEvent;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IManagedCheck;

import java.util.Map;

public class CheckService {
    private Map<CheckListenedEvent, IManagedCheck> loadedCheckMap;
}
