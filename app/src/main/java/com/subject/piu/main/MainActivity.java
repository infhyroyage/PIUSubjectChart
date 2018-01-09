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

import java.text.SimpleDateFormat;
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
    public Set<Switch> switches = new CopyOnWriteArraySet<>();

    /**
     * 「今日のお題を出す」ボタンが「お待ちください…」へと変化したかどうかのフラグ
     */
    public AtomicBoolean isWaited = new AtomicBoolean(false);

    /**
     * 「今日のお題を出す」のボタンのインスタンス
     */
    public Button buttonPop;

    /**
     * MainActivityのSharedPreferenceインスタンス
     */
    public SharedPreferences sp;

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
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // 以前にお題を出した日付の文字列を取得し、テキストビューに指定
        String oldPop = sp.getString("oldPop", "----/--/--");
        ((TextView) findViewById(R.id.textViewPop)).setText(getString(R.string.old_pop, oldPop));

        /*
         * 以下のタブごとのチェック状態を初期化
         *  ・ステップ
         *  ・難易度
         *  ・タイプ
         *  ・シリーズ
         *  ・カテゴリー
         *  ・その他
         */
        for (int i = 0; i < CommonParams.stepChecks.length; i++) {
            CommonParams.stepChecks[i] = sp.getBoolean("stepChecks[" + i + "]", true);
        }
        for (int i = 0; i < CommonParams.difficultyChecks.length; i++) {
            CommonParams.difficultyChecks[i] = sp.getBoolean("difficultyChecks[" + i + "]", true);
        }
        for (int i = 0; i < CommonParams.typeChecks.length; i++) {
            CommonParams.typeChecks[i] = sp.getBoolean("typeChecks[" + i + "]", true);
        }
        for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
            CommonParams.seriesChecks[i] = sp.getBoolean("seriesChecks[" + i + "]", true);
        }
        for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
            CommonParams.categoryChecks[i] = sp.getBoolean("categoryChecks[" + i + "]", true);
        }
        CommonParams.ppUnlockedStepCheck = sp.getBoolean("ppUnlockedStepCheck", false);
        CommonParams.amPassOnlyUsedStepCheck = sp.getBoolean("amPassOnlyUsedStepCheck", false);

        // メイン画面のViewPagerに、そのPagerAdapterをセット
        ViewPager mainViewPager = findViewById(R.id.mainViewPager);
        mainViewPager.setAdapter(new MainPagerAdapter(this, getSupportFragmentManager()));
        // メイン画面のタブのレイアウトと、そのViewPagerを自動で連動させる
        ((TabLayout) findViewById(R.id.mainTabLayout)).setupWithViewPager(mainViewPager);

        // 「今日のお題を出す」のボタンにリスナーをセット
        final MainActivity mainActivity = this;
        buttonPop = findViewById(R.id.buttonPop);
        buttonPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 現在の日付と、以前にお題を出した日付の文字列を取得
                String nowPop = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
                String oldPop = sp.getString("oldPop", "----/--/--");

                /*
                 * 同日に2回以上「今日のお題を出す」のボタンを押したかどうか判断し、
                 * 押した場合は既に出したお題の文字列を取得してダイアログを出力する
                 * 押していない場合は、別スレッドでお題を出す
                 */
                if (nowPop.equals(oldPop)) {
                    String subject = sp.getString("subject", "");
                    if (subject.equals("")) {
                        throw new IllegalStateException("Subject chart has already popped, but cannot be gotten.");
                    }

                    ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.pop_result, subject), true)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
                } else {
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
