package com.subject.piu.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.subject.piu.CommonParams;
import com.subject.piu.R;
import com.subject.piu.taking.TakingAsyncTask;

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
    public Button mainButtonTaking;

    /**
     * アクティビティを生成する
     * @param bundle バンドルインスタンス
     */
    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面のレイアウトをビューにセット
        setContentView(R.layout.activity_main);

        // ToolBarを取得し、ActionBarとしてセット
        Toolbar mainToolBar = findViewById(R.id.mainToolBar);
        setSupportActionBar(mainToolBar);

        // MainActivityのSharedPreferenceインスタンスを取得
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        // 以前にお題を出した日付の文字列を取得し、テキストビューに指定
        ((TextView) findViewById(R.id.textViewTaking)).setText(getString(R.string.last_taking_date, sp.getString("lastTakingDate", "----/--/--")));

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
        mainButtonTaking = findViewById(R.id.mainButtonTaking);
        mainButtonTaking.setOnClickListener(new TakingAsyncTask(this));

        // AdMobの初期化
        MobileAds.initialize(this, CommonParams.AD_VIEW_ID_MAIN);
        AdView mainAdView = findViewById(R.id.mainAdView);
        AdRequest request = new AdRequest.Builder().build();
        mainAdView.loadAd(request);
    }

    /**
     * メニュー画面をインフレートする
     * @param menu メニューのインスタンス
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * メニュー画面で選択したメニューに対する動作を行う
     * @param item 選択したメニューのアイテム
     * @return メニューを選択した場合はtrue、そうではない場合はfalse
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuLicense:
            // ライセンス情報を表示するアクティビティにインテントする
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.menu_license));
            startActivity(new Intent(this, OssLicensesMenuActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
