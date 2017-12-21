package com.subject.piu;

public abstract class CommonParams {
    /**
     * 「ステップ」の種類
     */
    public static final String[] STEPS = {
            "Single + Single Performance",
            "Double + Double Performance",
            "CO-OP"
    };
    /**
     * 「ステップ」のチェック状態
     */
    public static boolean[] step = new boolean[STEPS.length];

    /**
     * 現行稼働の最低難易度
     */
    private static final int MIN_DIFFICULTY = 1;
    /**
     * 現行稼働の最高難易度
     */
    private static final int MAX_DIFFICULTY = 28;
    /**
     * 「難易度」のチェック状態
     */
    public static boolean[] difficulty = new boolean[MAX_DIFFICULTY - MIN_DIFFICULTY + 1];

    /**
     * 「種別」の種類
     */
    public static final String[] TYPES = {
            "Normal",
            "Remix",
            "Full Song",
            "Short Cut"
    };
    /**
     * 「種別」のチェック状態
     */
    public static boolean[] type = new boolean[TYPES.length];

    /**
     * 「シリーズ」の種類
     */
    public static final String[] SERIES = {
            "1st～Perfect Collection",
            "Extra～PREX3",
            "Exceed〜Zero",
            "NX〜NXA",
            "Fiesta〜Fiesta EX",
            "Fiesta2",
            "Prime",
            "Prime2"
    };
    /**
     * 「シリーズ」のチェック状態
     */
    public static boolean[] series = new boolean[SERIES.length];

    /**
     * 「カテゴリー」の種類
     */
    public static final String[] CATEGORIES = {
            "Original",
            "K-POP",
            "World Music",
            "XROSS",
            "J-Music"
    };
    /**
     * 「カテゴリー」のチェック状態
     */
    public static boolean[] category = new boolean[CATEGORIES.length];

    /**
     * 「その他」の種類
     */
    public static final String[] OTHERS = {
            "PP解禁譜面を含む",
            "AM.PASS使用時限定譜面を含む"
    };
    /**
     * 「その他」のチェック状態
     */
    public static boolean[] other = new boolean[OTHERS.length];

    /**
     * PUMP IT UP (JAPAN) Wikiから取得する、各シリーズのURLの文字列配列
     */
    public static final String[] WIKI_URLS = {
            "http://seesaawiki.jp/piujpn/d/PRIME2"  // PRIME2
    };

    /**
     * MainActivityからDialogFragmentを表示するのに用いるタグ
     */
    public static final String MAIN_ACTIVITY_DIALOG_FRAGMENT = "MainActivityDialogFragment";

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private CommonParams() {}
}
