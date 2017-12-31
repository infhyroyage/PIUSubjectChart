package com.subject.piu.main;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.subject.piu.CommonParams;
import com.subject.R;

public class MainActivity extends AppCompatActivity {
    // メイン画面にある全ボタン
    Button buttonStep, buttonDifficulty, buttonType, buttonSeries, buttonCategory, buttonOther, buttonPop;

    // MainActivityのSharedPreferenceインスタンス
    SharedPreferences sp;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // メイン画面取得
        setContentView(R.layout.activity_main);

        // MainActivityのインスタンス
        final MainActivity mainActivity = this;

        // MainActivityのSharedPreferenceインスタンスを取得
        sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);
        // 以前にお題を出した日付の文字列を取得し、テキストビューに指定
        String oldPop = mainActivity.sp.getString("oldPop", "----/--/--");
        ((TextView) findViewById(R.id.textViewPop)).setText(getString(R.string.old_pop, oldPop));

        // 「ステップ」のボタンにリスナーをセット
        buttonStep = findViewById(R.id.buttonStep);
        buttonStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.STEP, getString(R.string.step)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「ステップ」のチェック状態、その下にあるテキストを初期化
        initializeChecksAndText(ButtonKind.STEP);

        // 「難易度」のボタンにリスナーをセット
        buttonDifficulty = findViewById(R.id.buttonDifficulty);
        buttonDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.DIFFICULTY, getString(R.string.difficulty)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「難易度」のチェック状態、その下にあるテキストを初期化
        initializeChecksAndText(ButtonKind.DIFFICULTY);

        // 「タイプ」のボタンにリスナーをセット
        buttonType = findViewById(R.id.buttonType);
        buttonType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.TYPE, getString(R.string.type)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「タイプ」のチェック状態、その下にあるテキストを初期化
        initializeChecksAndText(ButtonKind.TYPE);

        // 「シリーズ」のボタンにリスナーをセット
        buttonSeries = findViewById(R.id.buttonSeries);
        buttonSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.SERIES, getString(R.string.series)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「シリーズ」のチェック状態、その下にあるテキストを初期化
        initializeChecksAndText(ButtonKind.SERIES);

        // 「カテゴリー」のボタンにリスナーをセット
        buttonCategory = findViewById(R.id.buttonCategory);
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.CATEGORY, getString(R.string.category)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「カテゴリー」のチェック状態、その下にあるテキストを初期化
        initializeChecksAndText(ButtonKind.CATEGORY);

        // 「その他」のボタンにリスナーをセット
        buttonOther = findViewById(R.id.buttonOther);
        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.OTHER, getString(R.string.other)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「その他」のチェック状態を初期化
        CommonParams.ppUnlockedStepCheck = sp.getBoolean("ppUnlockedStepCheck", false);
        CommonParams.amPassOnlyUsedStepCheck = sp.getBoolean("amPassOnlyUsedStepCheck", false);

        // 「お題を出す」のボタンにリスナーをセット
        buttonPop = findViewById(R.id.buttonPop);
        buttonPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PoppingThread(mainActivity).start();
            }
        });
    }

    /**
     * メイン画面の以下のボタンのチェック状態と、その下にあるTextViewのテキストを初期化する
     *  ・ステップ
     *  ・難易度
     *  ・タイプ
     *  ・シリーズ
     *  ・カテゴリー
     * @param buttonKind 初期化するボタンのタイプ
     */
    private void initializeChecksAndText(ButtonKind buttonKind) {
        switch (buttonKind) {
        case STEP:
            for (int i = 0; i < CommonParams.stepChecks.length; i++) {
                CommonParams.stepChecks[i] = sp.getBoolean("stepChecks[" + i + "]", true);
            }
            updateTextViewUnderButton(buttonKind);
            break;
        case DIFFICULTY:
            for (int i = 0; i < CommonParams.difficultyChecks.length; i++) {
                CommonParams.difficultyChecks[i] = sp.getBoolean("difficultyChecks[" + i + "]", true);
            }
            updateTextViewUnderButton(buttonKind);
            break;
        case TYPE:
            for (int i = 0; i < CommonParams.typeChecks.length; i++) {
                CommonParams.typeChecks[i] = sp.getBoolean("typeChecks[" + i + "]", true);
            }
            updateTextViewUnderButton(buttonKind);
            break;
        case SERIES:
            for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
                CommonParams.seriesChecks[i] = sp.getBoolean("seriesChecks[" + i + "]", true);
            }
            updateTextViewUnderButton(buttonKind);
            break;
        case CATEGORY:
            for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
                CommonParams.categoryChecks[i] = sp.getBoolean("categoryChecks[" + i + "]", true);
            }
            updateTextViewUnderButton(buttonKind);
            break;
        default:
            throw new IllegalArgumentException("The ButtonKind argument cannot be applied.");
        }
    }

    /**
     * メイン画面の以下のボタンの下にあるTextViewのテキストを更新する
     *  ・ステップ
     *  ・難易度
     *  ・タイプ
     *  ・シリーズ
     *  ・カテゴリー
     * @param buttonKind 更新するテキストの上にあるボタンのタイプ
     */
    void updateTextViewUnderButton(ButtonKind buttonKind) {
        // 更新後のテキスト文字列
        StringBuilder textBuilder = new StringBuilder();

        switch (buttonKind) {
        case STEP:
            // 更新後のテキスト文字列をセット
            for (int i = 0; i < CommonParams.stepChecks.length; i++) {
                if (CommonParams.stepChecks[i]) {
                    if (!textBuilder.toString().equals("")) {
                        textBuilder.append(", ");
                    }
                    textBuilder.append(CommonParams.STEPS[i]);
                }
            }

            // TextViewにセット
            ((TextView)findViewById(R.id.textViewStep)).setText(getString(R.string.current, textBuilder.toString()));
            break;
        case DIFFICULTY:
            // 更新後のテキスト文字列をセット
            for (int i = 0; i < CommonParams.difficultyChecks.length; i++) {
                if (CommonParams.difficultyChecks[i]) {
                    if (!textBuilder.toString().equals("")) {
                        textBuilder.append(",");
                    }
                    textBuilder.append(String.valueOf(i + 1));
                }
            }

            // TextViewにセット
            ((TextView)findViewById(R.id.textViewDifficulty)).setText(getString(R.string.current, textBuilder.toString()));
            break;
        case TYPE:
            // 更新後のテキスト文字列をセット
            for (int i = 0; i < CommonParams.typeChecks.length; i++) {
                if (CommonParams.typeChecks[i]) {
                    if (!textBuilder.toString().equals("")) {
                        textBuilder.append(", ");
                    }
                    textBuilder.append(CommonParams.TYPES[i]);
                }
            }

            // TextViewにセット
            ((TextView)findViewById(R.id.textViewType)).setText(getString(R.string.current, textBuilder.toString()));
            break;
        case SERIES:
            // 更新後のテキスト文字列をセット
            for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
                if (CommonParams.seriesChecks[i]) {
                    if (!textBuilder.toString().equals("")) {
                        textBuilder.append(", ");
                    }
                    textBuilder.append(CommonParams.SERIES[i]);
                }
            }

            // TextViewにセット
            ((TextView)findViewById(R.id.textViewSeries)).setText(getString(R.string.current, textBuilder.toString()));
            break;
        case CATEGORY:
            // 更新後のテキスト文字列をセット
            for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
                if (CommonParams.categoryChecks[i]) {
                    if (!textBuilder.toString().equals("")) {
                        textBuilder.append(", ");
                    }
                    textBuilder.append(CommonParams.CATEGORIES[i]);
                }
            }

            // TextViewにセット
            ((TextView)findViewById(R.id.textViewCategory)).setText(getString(R.string.current, textBuilder.toString()));
            break;
        default:
            throw new IllegalArgumentException("The ButtonKind argument cannot be applied.");
        }
    }
}
