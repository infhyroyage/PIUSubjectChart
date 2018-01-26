package com.subject.piu;

/**
 * 全パッケージの共通パラメータを定義する抽象staticクラス
 */
public abstract class CommonParams {
    /**
     * 以下で定義される、メイン画面のタブの数
     *  ・難易度
     *  ・タイプ
     *  ・シリーズ
     *  ・カテゴリー
     *  ・その他
     */
    public static final int TAB_PAGE_NUM = 5;

    /**
     * 現行稼働のSingle譜面の最高難易度
     */
    private static final int MAX_SINGLE_DIFFICULTY = 26;
    /**
     * 「難易度」のSingle譜面のチェック状態
     */
    public static boolean[] singleChecks = new boolean[MAX_SINGLE_DIFFICULTY];
    /**
     * 現行稼働のDouble譜面の最高難易度
     */
    private static final int MAX_DOUBLE_DIFFICULTY = 28;
    /**
     * 「難易度」のSingle譜面のチェック状態
     */
    public static boolean[] doubleChecks = new boolean[MAX_DOUBLE_DIFFICULTY];
    /**
     * 「難易度」のCOOP譜面のチェック状態
     */
    public static boolean coopCheck = false;

    /**
     * 「タイプ」の種類
     */
    public static final String[] TYPES = {
            "Normal",
            "Remix",
            "Full Song",
            "Short Cut"
    };
    /**
     * 「タイプ」のチェック状態
     */
    public static boolean[] typeChecks = new boolean[TYPES.length];

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
     * 「シリーズ」のPUMP IT UP (JAPAN) WikiのURLの文字列
     * 第1インデックスは、SERIESのインデックスと一致するように定義すること
     */
    public static final String[][] SERIES_URL = {
            {"http://seesaawiki.jp/piujpn/d/1st%a1%c1PERFECT%20COLLECTION"},
            {"http://seesaawiki.jp/piujpn/d/EXTRA%a1%c1PREX%203"},
            {"http://seesaawiki.jp/piujpn/d/EXCEED%a1%c1ZERO"},
            {"http://seesaawiki.jp/piujpn/d/NX%a1%c1NXA"},
            {"http://seesaawiki.jp/piujpn/d/FIESTA", "http://seesaawiki.jp/piujpn/d/FIESTA%20EX"},
            {"http://seesaawiki.jp/piujpn/d/FIESTA2"},
            {"http://seesaawiki.jp/piujpn/d/PRIME"},
            {"http://seesaawiki.jp/piujpn/d/PRIME2", "http://seesaawiki.jp/piujpn/d/PRIME2%282018%29"},
    };
    /**
     * 「シリーズ」のチェック状態
     */
    public static boolean[] seriesChecks = new boolean[SERIES.length];

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
    public static boolean[] categoryChecks = new boolean[CATEGORIES.length];

    /**
     * 「その他」の「PP解禁譜面を含む」のチェック状態
     */
    public static boolean ppUnlockedStepCheck = false;
    /**
     * 「その他」の「AM.PASS使用時限定譜面を含む」のチェック状態
     */
    public static boolean amPassOnlyUsedStepCheck = false;

    /**
     * メイン画面のAdMobのアプリID
     */
    public static final String AD_VIEW_ID_MAIN = "ca-app-pub-2231903967147229~6474652519";

    /**
     * MainActivityからDialogFragmentを表示するのに用いるタグ
     */
    public static final String DIALOG_FRAGMENT_MAIN = "DialogFragmentMain";

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private CommonParams() {}
}
