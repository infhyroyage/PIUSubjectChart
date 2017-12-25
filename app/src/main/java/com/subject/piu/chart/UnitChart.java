package com.subject.piu.chart;

class UnitChart {
    // 曲名
    private String name;

    // Single、Doubleのフラグ
    private boolean isDouble;

    // Performance譜面のフラグ
    private boolean isPerformance;

    // 難易度
    private int difficulty;

    // CO-OP譜面の文字列
    private String coopStr;

    /**
     * CO-OP譜面以外のコンストラクタ
     * @param name 曲名
     * @param isDouble Single、Doubleのフラグ
     * @param isPerformance Performance譜面のフラグ
     * @param difficulty 難易度
     */
    UnitChart(String name, boolean isDouble, boolean isPerformance, int difficulty) {
        this.name = name;
        this.isDouble = isDouble;
        this.isPerformance = isPerformance;
        this.difficulty = difficulty;
        this.coopStr = "";
    }

    /**
     * CO-OP譜面のコンストラクタ
     * @param name 曲名
     */
    UnitChart(String name) {
        this.name = name;
        this.coopStr = "COOP";
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(name);
        strBuilder.append(" ");
        if (coopStr.equals("")) {
            strBuilder.append((isDouble) ? "Double " : "Single ");
            strBuilder.append((isPerformance) ? "Performance " : "");
            strBuilder.append(difficulty);
        } else {
            strBuilder.append(coopStr);
        }

        return strBuilder.toString();
    }
}