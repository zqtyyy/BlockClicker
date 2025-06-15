// src/main/java/org/zqty/blockClicker/NumberUtil.java
package org.zqty.blockClicker;

public class NumberUtil {

    /**
     * Formatte un entier en k/M/B.
     * Exemples : 1234 → "1.2k", 5_600_000 → "5.6M", 7_200_000_000 → "7.2B"
     */
    public static String formatNumber(long value) {
        if (value >= 1_000_000_000) {
            return String.format("%.1fB", value / 1_000_000_000.0);
        } else if (value >= 1_000_000) {
            return String.format("%.1fM", value / 1_000_000.0);
        } else if (value >= 1_000) {
            return String.format("%.1fk", value / 1_000.0);
        } else {
            return Long.toString(value);
        }
    }

    /**
     * Surcharge pour double : arrondit puis formate en k/M/B.
     */
    public static String formatNumber(double value) {
        return formatNumber(Math.round(value));
    }

    /**
     * Formatte un double avec un nombre fixe de décimales.
     * Ex : formatDecimal(1.738, 1) → "1.7"
     */
    public static String formatDecimal(double value, int decimals) {
        return String.format("%." + decimals + "f", value);
    }
}
