package com.subject.piu.chart;

class UnitChart {
    // 曲名
    private String name;

    // COOPのフラグ
    // trueの場合はisDoubleの値を見ない
    private boolean isCoop;

    //SingleとDoubleのフラグ
    private boolean isDouble;

    // 難易度
    // isCoopがtrueの場合は見ない
    private int difficulty;

    UnitChart(String name, boolean isCoop, boolean isDouble, int difficulty) {
        this.name = name;
        this.isCoop = isCoop;
        this.isDouble = isDouble;
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(name);
        strBuilder.append(" ");
        if (isCoop) {
            strBuilder.append("CO-OP");
        } else {
            strBuilder.append((isDouble) ? "D" : "S");
            strBuilder.append(difficulty);
        }

        return strBuilder.toString();
    }
}
