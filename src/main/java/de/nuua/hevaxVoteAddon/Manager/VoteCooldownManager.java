package de.nuua.hevaxVoteAddon.Manager;

public class VoteCooldownManager {
    private static long lastVoteTime = 0;
    private static long lastKickVoteTime = 0;
    private static final long COOLDOWN_DURATION = 600000; // 10 Minuten in Millisekunden
    private static final long KICK_COOLDOWN_DURATION = 300000; // 5 Minuten in Millisekunden

    public static boolean canVote(boolean isKickVote) {
        long currentTime = System.currentTimeMillis();
        if (isKickVote) {
            // nie genutzt, man weiss nie, wa
            return currentTime - lastKickVoteTime >= KICK_COOLDOWN_DURATION;
        } else {
            return currentTime - lastVoteTime >= COOLDOWN_DURATION;
        }
    }



    public static void setLastVoteTime(boolean isKickVote) {
        if (isKickVote) {
            // nie genutzt, man weiss nie, wa
            lastKickVoteTime = System.currentTimeMillis();
        } else {
            lastVoteTime = System.currentTimeMillis();
        }
    }

    public static long getRemainingCooldown(boolean isKickVote) {
        long currentTime = System.currentTimeMillis();
        if (isKickVote) {
            return Math.max(0, KICK_COOLDOWN_DURATION - (currentTime - lastKickVoteTime));
        } else {
            return Math.max(0, COOLDOWN_DURATION - (currentTime - lastVoteTime));
        }
    }



    // vote active generel checken

    private static boolean isVoteActive = false;

    public static boolean isVoteActive() {
        return isVoteActive;
    }

    public static void setVoteActive(boolean active) {
        isVoteActive = active;
    }

}