package com.subject.piu.chart;

import android.util.Log;

import com.subject.piu.CommonParams;

/**
 * 1つの譜面データを表すクラス
 */
class UnitChart {
    // デバッグ用のタグ
    private static final String TAG = "UnitChart";

    // 曲名
    private String name;

    // 難易度
    private int difficulty;

    // タイプ
    String type;

    // Single、Doubleのフラグ
    private boolean isDouble;

    // Performance譜面のフラグ
    private boolean isPerformance;

    /**
     * COOP譜面以外のコンストラクタ
     * @param name 曲名
     * @param difficulty 難易度
     * @param type タイプ
     * @param isDouble Single、Doubleのフラグ
     * @param isPerformance Performance譜面のフラグ
     */
    UnitChart(String name, int difficulty, String type, boolean isDouble, boolean isPerformance) {
        this.name = name;
        this.difficulty = difficulty;
        if (type.equalsIgnoreCase(CommonParams.TYPES[1])) {
            this.type = "REMIX";
        } else if (type.equalsIgnoreCase(CommonParams.TYPES[2])) {
            this.type = "FULL SONG";
        } else if (type.equalsIgnoreCase(CommonParams.TYPES[3])) {
            this.type = "SHORT CUT";
        } else {
            this.type = "";
        }
        this.isDouble = isDouble;
        this.isPerformance = isPerformance;

        // ログ出力
        Log.d(TAG, "name=" + name + ",difficulty=" + difficulty + ",type=" + this.type + ",isDouble=" + isDouble + ",isPerformance=" + isPerformance);
    }

    /**
     * COOP譜面のコンストラクタ
     * @param name 曲名
     */
    UnitChart(String name) {
        this.name = name;
        this.type = "COOP";

        // ログ出力
        Log.d(TAG, "name=" + name + ",type=COOP");
    }

    /**
     * この譜面のString形式の文字列を返す
     * @return String形式の文字列
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(name);
        if (!type.equals("COOP")) {
            strBuilder.append((isDouble) ? " Double " : " Single ");
            strBuilder.append((isPerformance) ? "Performance " : "");
            strBuilder.append(difficulty);
        }

        return strBuilder.toString();
    }
}
