package com.subject.piu.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.subject.piu.CommonParams;
import com.subject.piu.R;
import com.subject.piu.popping.PoppingAsyncTask;
import com.subject.piu.popping.ResultDialogFragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * メイン画面のアクティビティのクラス
 */
public class MainActivity extends AppCompatActivity {
    /**
     * すべてのフラグメント上で破棄されていないSwitchの集合
     */
    public Set<Switch> createdSwitches = new CopyOnWriteArraySet<>();

    /**
     * 「今日のお題を出す」ボタンが「お待ちください…」へと変化したかどうかのフラグ
     */
    public AtomicBoolean isWaited = new AtomicBoolean(false);

    /**
     * 「今日のお題を出す」のボタンのインスタンス
     */
    public Button mainButtonPop;

    /**
     * アクティビティを生成する
     * @param bundle バンドルインスタンス
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面のレイアウトをビューにセット
        setContentView(R.layout.activity_main);

        // MainActivityのSharedPreferenceインスタンスを取得
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // 以前にお題を出した日付の文字列を取得し、テキストビューに指定
        String oldPop = sp.getString("oldPop", "----/--/--");
        ((TextView) findViewById(R.id.textViewPop)).setText(getString(R.string.old_pop, oldPop));

        // 「難易度」タブのチェック状態を初期化
        for (int i = 0; i < CommonParams.singleChecks.length; i++) {
            CommonParams.singleChecks[i] = sp.getBoolean("singleChecks[" + i + "]", true);
        }
        for (int i = 0; i < CommonParams.doubleChecks.length; i++) {
            CommonParams.doubleChecks[i] = sp.getBoolean("doubleChecks[" + i + "]", true);
        }
        CommonParams.coopCheck = sp.getBoolean("coopCheck", true);
        // 「種別」タブのチェック状態を初期化
        for (int i = 0; i < CommonParams.typeChecks.length; i++) {
            CommonParams.typeChecks[i] = sp.getBoolean("typeChecks[" + i + "]", true);
        }
        // 「シリーズ」タブのチェック状態を初期化
        for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
            CommonParams.seriesChecks[i] = sp.getBoolean("seriesChecks[" + i + "]", true);
        }
        // 「カテゴリー」タブのチェック状態を初期化
        for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
            CommonParams.categoryChecks[i] = sp.getBoolean("categoryChecks[" + i + "]", true);
        }
        // 「その他」タブのチェック状態を初期化
        CommonParams.ppUnlockedStepCheck = sp.getBoolean("ppUnlockedStepCheck", false);
        CommonParams.amPassOnlyUsedStepCheck = sp.getBoolean("amPassOnlyUsedStepCheck", false);

        // メイン画面のViewPagerに、そのPagerAdapterをセット
        ViewPager mainViewPager = findViewById(R.id.mainViewPager);
        mainViewPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        // メイン画面のタブのレイアウトと、そのViewPagerを自動で連動させる
        ((TabLayout) findViewById(R.id.mainTabLayout)).setupWithViewPager(mainViewPager);

        // 「今日のお題を出す」のボタンにリスナーをセット
        final MainActivity mainActivity = this;
        mainButtonPop = findViewById(R.id.mainButtonPop);
        mainButtonPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 現在の日付と、以前にお題を出した日付の文字列を取得
                String nowPop = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String oldPop = sp.getString("oldPop", "----/--/--");

                if (nowPop.equals(oldPop)) {
                    /*
                     * 同日に2回以上「今日のお題を出す」のボタンを押した場合は、
                     * その場合は既に出したお題の文字列を取得してダイアログを出力する
                     */
                    String subject = sp.getString("subject", "");
                    if (subject.equals("")) {
                        throw new IllegalStateException("Subject chart has already popped, but cannot be gotten.");
                    }

                    ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.pop_result, subject), true)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
                } else if (Arrays.equals(CommonParams.singleChecks, new boolean[CommonParams.singleChecks.length]) && Arrays.equals(CommonParams.doubleChecks, new boolean[CommonParams.doubleChecks.length]) && !CommonParams.coopCheck) {
                    // 「難易度」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
                    ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, getString(R.string.difficulty)), false)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
                } else if (Arrays.equals(CommonParams.typeChecks, new boolean[CommonParams.typeChecks.length])) {
                    // 「種別」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
                    ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, getString(R.string.type)), false)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
                } else if (Arrays.equals(CommonParams.seriesChecks, new boolean[CommonParams.seriesChecks.length])) {
                    // 「シリーズ」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
                    ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, getString(R.string.series)), false)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
                } else if (Arrays.equals(CommonParams.categoryChecks, new boolean[CommonParams.categoryChecks.length])) {
                    // 「カテゴリー」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
                    ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, getString(R.string.category)), false)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
                } else {
                    // 上記以外の場合は、別スレッドでお題を出す
                    new PoppingAsyncTask(mainActivity).execute();
                }
            }
        });

        // AdMobの初期化
        MobileAds.initialize(this, CommonParams.MAIN_AD_VIEW_ID);
        final AdView mainAdView = findViewById(R.id.mainAdView);
        AdRequest request = new AdRequest.Builder().build();
        mainAdView.loadAd(request);
    }
}
