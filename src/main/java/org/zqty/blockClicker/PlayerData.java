package org.zqty.blockClicker;

public class PlayerData {
    private double points;
    private int upgradeLevel;

    private long   clickBoostEndTimestamp;
    private double clickBoostExtra;

    private long   passiveBoostEndTimestamp;
    private double passiveBoostRate;

    public PlayerData(double points, int upgradeLevel) {
        this.points       = points;
        this.upgradeLevel = upgradeLevel;
    }

    // ----------------------------
    // Points & upgrades
    // ----------------------------

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public void addPoints(double delta) {
        this.points += delta;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }

    public void incrementUpgradeLevel() {
        this.upgradeLevel++;
    }

    public double getPointsPerClick() {
        ConfigManager cfg = Main.getInstance().getConfigManager();
        double base = cfg.getConfig().getDouble("upgrades.base-points-per-click", 1.0);
        double mult = cfg.getConfig().getDouble("upgrades.points-multiplier", 1.2);
        double ppc = base * Math.pow(mult, upgradeLevel);

        // Ajout du boost click si actif
        if (System.currentTimeMillis() < clickBoostEndTimestamp) {
            ppc += clickBoostExtra;
        }
        return ppc;
    }

    // ----------------------------
    // Click-boost
    // ----------------------------

    public long getClickBoostEnd() {
        return clickBoostEndTimestamp;
    }

    public double getClickBoostExtra() {
        return clickBoostExtra;
    }

    public void setClickBoost(long endTimestamp, double extra) {
        this.clickBoostEndTimestamp = endTimestamp;
        this.clickBoostExtra = extra;
    }

    // ----------------------------
    // Passive-boost
    // ----------------------------

    public long getPassiveBoostEnd() {
        return passiveBoostEndTimestamp;
    }

    public double getPassiveBoostRate() {
        return passiveBoostRate;
    }

    public void setPassiveBoost(long endTimestamp, double rate) {
        this.passiveBoostEndTimestamp = endTimestamp;
        this.passiveBoostRate = rate;
    }
}
