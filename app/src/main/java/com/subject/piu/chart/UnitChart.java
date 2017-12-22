package com.subject.piu.chart;

class UnitChart {
    // 曲名
    private String name;

    // COOP譜面の文字列
    private String coopStr;

    // Single、Doubleのフラグ
    private boolean isDouble;

    // 難易度
    private int difficulty;

    /**
     * 単一譜面クラスのコンストラクタ
     * @param name 曲名
     * @param coopStr COOP譜面の文字列(""でない場合はCOOP譜面を表す)
     * @param isDouble Single、Doubleのフラグ(coopStrが""でない場合は任意の値でOK)
     * @param difficulty 難易度(coopStrが""でない場合は任意の値でOK)
     */
    UnitChart(String name, String coopStr, boolean isDouble, int difficulty) {
        this.name = name;
        this.coopStr = coopStr;
        this.isDouble = isDouble;
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(name);
        strBuilder.append(" ");
        if (coopStr.equals("")) {
            strBuilder.append((isDouble) ? "D" : "S");
            strBuilder.append(difficulty);
        } else {
            strBuilder.append(coopStr);
        }

        return strBuilder.toString();
    }
}
