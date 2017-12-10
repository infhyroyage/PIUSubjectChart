package com.piusubjectchart;

public abstract class CommonParams {
    /**
     * 現行稼働の最低難易度
     */
    public static final int MIN_DIFFICULTY = 1;
    /**
     * 現行稼働の最高難易度
     */
    public static final int MAX_DIFFICULTY = 28;

    /**
     * MainActivityからDialogFragmentを表示するのに用いるタグ
     */
    public static final String MAIN_ACTIVITY_DIALOG_FRAGMENT = "MainActivityDialogFragment";

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private CommonParams() {}
}
