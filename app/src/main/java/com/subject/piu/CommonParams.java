package com.subject.piu;

/**
 * 全パッケージの共通パラメータを定義する抽象staticクラス
 */
public abstract class CommonParams {
    /**
     * 以下で定義される、メイン画面のタブの数
     *  ・ステップ
     *  ・難易度
     *  ・種別
     *  ・シリーズ
     *  ・カテゴリー
     *  ・その他
     */
    public static final int TAB_PAGE_NUM = 6;

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
    public static boolean[] stepChecks = new boolean[STEPS.length];

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
    public static boolean[] difficultyChecks = new boolean[MAX_DIFFICULTY - MIN_DIFFICULTY + 1];

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
     * 「その他」のPP解禁譜面の種類
     */
    public static final String PP_UNLOCKED_STEP = "PP解禁譜面を含む";
    /**
     * 「その他」のPP解禁譜面のチェック状態
     */
    public static boolean ppUnlockedStepCheck = false;

    /**
     * 「その他」のAM.PASS使用時限定譜面の種類
     */
    public static final String AM_PASS_ONLY_USED_STEP = "AM.PASS使用時限定譜面を含む";
    /**
     * 「その他」のAM.PASS使用時限定譜面のチェック状態
     */
    public static boolean amPassOnlyUsedStepCheck = false;

    /**
     * メイン画面のAdMobのアプリID
     */
    public static final String MAIN_AD_VIEW_ID = "ca-app-pub-2231903967147229~6474652519";

    /**
     * MainActivityからDialogFragmentを表示するのに用いるタグ
     */
    public static final String MAIN_ACTIVITY_DIALOG_FRAGMENT = "MainActivityDialogFragment";

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private CommonParams() {}
}
