package me.kr1s_d.ultimateantibot.common.objects.profile.meta;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ScoreTracker implements Serializable {
    private int CURRENT_SCORE;
    private final List<ScoreAddition> additionList;
    private final int MAX_SCORE;

    public ScoreTracker() {
        this.additionList = new CopyOnWriteArrayList<>();
        this.CURRENT_SCORE = 0;
        this.MAX_SCORE = 1000;
    }

    public List<ScoreAddition> getScoresByID(ScoreID id) {
        return additionList.stream()
                .filter(s -> s.id.equals(id))
                .collect(Collectors.toList());
    }

    public List<ScoreAddition> getScoresByAmount(int amount) {
        return additionList.stream()
                .filter(s -> s.scoreAmount == amount)
                .collect(Collectors.toList());
    }

    public List<ScoreAddition> getAdditionList() {
        return additionList;
    }

    /**
     *
     * @param scoreID the unique id for this score
     * @param score the score amount
     * @param isStackable if allows other scores with same id after first addition
     * @param type the score duration amount
     * @param duration the duration of the score
     */
    public void addScore(ScoreID scoreID, int score, boolean isStackable, ScoreDurationType type, long duration) {
        ScoreAddition addition = new ScoreAddition(scoreID, score, type, System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(duration), isStackable);
        if(alreadyHasScoreWithID(scoreID) && !isStackable) return;
        addScore0(score);
        additionList.add(addition);
    }

    /**
     * @param scoreID score id
     * @param score score amount
     * @param type score duration type
     * @param bulkRemoval should i remove ALL similar scores or only one?
     * @return if score has removed or not
     */
    public boolean removeScore(ScoreID scoreID, int score, ScoreDurationType type, boolean bulkRemoval) {
        List<ScoreAddition> scores = additionList.stream()
                .filter(s -> s.id.equals(scoreID))
                .filter(s -> s.scoreAmount == score)
                .filter(s ->  s.type == type)
                .collect(Collectors.toList());

        if(scores.isEmpty()) return false;

        if(bulkRemoval) {
            for (ScoreAddition addition : scores) {
                removeScore0(addition.scoreAmount);
                additionList.remove(addition);
            }
        }else {
            ScoreAddition addition = scores.stream().findAny().get();
            removeScore0(addition.scoreAmount);
            additionList.remove(addition);

        }

        return true;
    }

    public void checkRemoval() {
        for (ScoreAddition addition : new ArrayList<>(additionList)) {
            if(addition.type.equals(ScoreDurationType.EXPIRE_BY_TIME) && addition.isRemovable()) {
                removeScore(addition.id, addition.scoreAmount, addition.type, false);
            }
        }
    }

    public void reset() {
        additionList.clear();
        CURRENT_SCORE = 0;
    }

    public int getScore() {
        return CURRENT_SCORE;
    }

    public void expireScores(ScoreDurationType type) {
        List<ScoreAddition> collect = additionList.stream()
                .filter(s -> s.type.equals(type))
                .collect(Collectors.toList());

        for (ScoreAddition scoreAddition : collect) {
            removeScore(scoreAddition.id, scoreAddition.scoreAmount, scoreAddition.type, true);
        }
    }

    private void addScore0(int amount) {
        this.CURRENT_SCORE += amount;
        if(CURRENT_SCORE > MAX_SCORE) CURRENT_SCORE = MAX_SCORE;
    }

    private void removeScore0(int amount) {
        this.CURRENT_SCORE -= amount;
        if(CURRENT_SCORE < 0) CURRENT_SCORE = 0;
    }

    private boolean alreadyHasScoreWithID(ScoreID id) {
        return !getScoresByID(id).isEmpty();
    }

    public static class ScoreAddition implements Serializable {
        private final ScoreID id;
        private final int scoreAmount;
        private final ScoreDurationType type;
        private final long removalDate;
        private boolean isStackable;

        public ScoreAddition(ScoreID id, int scoreAmount, ScoreDurationType type, long removalDate, boolean isStackable) {
            this.id = id;
            this.scoreAmount = scoreAmount;
            this.type = type;
            this.removalDate = removalDate;
            this.isStackable = isStackable;
        }

        public boolean isRemovable() {
            return System.currentTimeMillis() > removalDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScoreAddition addition = (ScoreAddition) o;
            return id == addition.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }

        @Override
        public String toString() {
            return id + " -> " + scoreAmount;
        }
    }

    public enum ScoreID {
        NO_PACKET_CHECK,
        IS_FIST_JOIN,
        JOIN_NO_PING,
        ABNORMAL_CHAT_MESSAGE,
        ABNORMAL_NAME,
        CHANGE_NAME,
        AUTH_CHECK_PASS;

    }

    public enum ScoreDurationType {
        PERMANENT,
        EXPIRE_BY_TIME,
        EXPIRE_ON_QUIT,
        EXPIRE_ON_JOIN,
        EXPIRE_ON_PING;
    }


}
