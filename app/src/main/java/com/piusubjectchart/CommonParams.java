package com.piusubjectchart;

public abstract class CommonParams {
    /**
     * 譜面のタイプ
     */
    public static final String[] TYPES = {
            "Single + Single Performance",
            "Double + Double Performance",
            "CO-OP"};

    /**
     * 現行稼働の最低難易度
     */
    public static final int MIN_DIFFICULTY = 1;
    /**
     * 現行稼働の最高難易度
     */
    public static final int MAX_DIFFICULTY = 28;

    /**
     * 歴代バージョン
     */
    public static final String[] VERSIONS = {
            "1st～Perfect Collection",
            "Extra～PREX3",
            "Exceed〜Zero",
            "NX〜NXA",
            "Fiesta〜Fiesta EX",
            "Fiesta2",
            "Prime",
            "Prime2"};

    /**
     * MainActivityからDialogFragmentを表示するのに用いるタグ
     */
    public static final String MAIN_ACTIVITY_DIALOG_FRAGMENT = "MainActivityDialogFragment";

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private CommonParams() {}
}
