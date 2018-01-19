package com.subject.piu.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import com.subject.piu.CommonParams;
import com.subject.piu.R;

/**
 * メイン画面のPagerAdapterのクラス
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {
    // デバッグ用のタグ
    private static final String TAG = "MainPagerAdapter";

    // 呼びだされたMainActivityのインスタンス
    private MainActivity mainActivity;

    /**
     * コンストラクタ
     * @param mainActivity メイン画面のアクティビティ
     * @param manager 親クラスのコンストラクタで使用するフラグメントマネージャー
     */
    MainPagerAdapter(MainActivity mainActivity, FragmentManager manager) {
        super(manager);
        this.mainActivity = mainActivity;
    }

    /**
     * 選択したメイン画面のタブの番号から、それに対応するフラグメントを返す
     * @param position 選択したメイン画面のタブの番号
     * @return 選択したメイン画面のタブの番号に対応するフラグメント
     */
    @Override
    public Fragment getItem(int position) {
        // ログ出力
        Log.d(TAG, "getItem:position=" + position);

        return MainSwitchFragment.newInstance(mainActivity, position);
    }

    /**
     * 選択したメイン画面のタブの番号から、それに対応するタブのタイトルの文字列を返す
     * @param position 選択したメイン画面のタブの番号
     * @return 選択したメイン画面のタブの番号に対応するタイトルの文字列
     */
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
        case 0:
            // 「難易度」の場合
            return mainActivity.getString(R.string.difficulty);
        case 1:
            // 「種別」の場合
            return mainActivity.getString(R.string.type);
        case 2:
            // 「シリーズ」の場合
            return mainActivity.getString(R.string.series);
        case 3:
            // 「カテゴリー」の場合
            return mainActivity.getString(R.string.category);
        case 4:
            // 「その他」の場合
            return mainActivity.getString(R.string.other);
        default:
            throw new IllegalArgumentException("position is out between 0 to 4.");
        }
    }

    /**
     * メイン画面のタブの個数を返す
     * @return メイン画面のタブの個数
     */
    @Override
    public int getCount() {
        return CommonParams.TAB_PAGE_NUM;
    }
}
