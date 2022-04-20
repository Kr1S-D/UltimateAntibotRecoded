package me.kr1s_d.ultimateantibot.common.service;

import me.kr1s_d.ultimateantibot.common.checks.*;
import me.kr1s_d.ultimateantibot.common.helper.LogHelper;
import me.kr1s_d.ultimateantibot.common.objects.enums.CheckListenedEvent;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IAntiBotPlugin;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.IService;
import me.kr1s_d.ultimateantibot.common.objects.interfaces.check.IManagedCheck;

import java.util.*;

public class CheckService implements IService {
    private final IAntiBotPlugin plugin;
    private final LogHelper logger;
    private final Map<CheckListenedEvent, List<IManagedCheck>> loadedChecks;

    public CheckService(IAntiBotPlugin plugin){
        this.plugin = plugin;
        this.logger = plugin.getLogHelper();
        this.loadedChecks = new HashMap<>();
    }

    @Override
    public void load() {
        register(new FirstJoinBasicCheck(plugin));
        register(new NameChangerBasicCheck(plugin));
        register(new SuperJoinBasicCheck(plugin));
        register(new AccountBasicCheck(plugin));
        register(new SimilarNameBasicCheck(plugin));
        register(new LengthBasicCheck(plugin));
        logger.debug("Initializing " + loadedChecks.size() + " blocks of checks!");
        for(CheckListenedEvent e : CheckListenedEvent.values()){
            List<IManagedCheck> checkRegistry = getCheckListByEvent(e);
            logger.debug("Loading checks for listener: " + e.name() + " | " + checkRegistry.size());
            for(IManagedCheck check : checkRegistry) {
                logger.debug("Registering check " + check.getCheckName() + " - " + check.getCheckVersion());
            }
        }
    }

    @Override
    public void unload() {
        loadedChecks.clear();
    }

    public List<IManagedCheck> getCheckListByEvent(CheckListenedEvent e){
        List<IManagedCheck> sorted = new ArrayList<>();
        List<IManagedCheck> record = loadedChecks.get(e);

        if(record == null) return new ArrayList<>();
        for(int j = 0; j < 5; j++){
            for(IManagedCheck c : record) if (c.getCheckPriority().getPriority() == j)  sorted.add(c);
        }

        return sorted;
    }

    public void executeDisconnect(String ip, String name){
        loadedChecks.forEach((event, checkList) -> checkList.forEach(e -> e.onDisconnect(ip, name)));
    }

    public void register(IManagedCheck check){
        if(!loadedChecks.containsKey(check.getCheckListenedEvent())){
            loadedChecks.put(check.getCheckListenedEvent(), new ArrayList<>(Collections.singletonList(check)));
            return;
        }
        loadedChecks.get(check.getCheckListenedEvent()).add(check);
    }
}