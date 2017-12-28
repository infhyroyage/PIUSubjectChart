package com.subject.piu.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.subject.piu.CommonParams;
import com.subject.R;
import com.subject.piu.chart.Chooser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // デバッグ用のタグ
    private static final String TAG = "MainActivity";

    // メイン画面にある全ボタン
    private Button buttonStep, buttonDifficulty, buttonType, buttonSeries, buttonCategory, buttonOther, buttonRun;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面取得
        setContentView(R.layout.activity_main);

        // MainActivityのインスタンス
        final MainActivity mainActivity = this;
        // TODO : チェック状態を初期化(SharedPreferenceを使用する)
        Arrays.fill(CommonParams.step, true);
        Arrays.fill(CommonParams.difficulty, true);
        Arrays.fill(CommonParams.type, true);
        Arrays.fill(CommonParams.series, true);
        Arrays.fill(CommonParams.category, true);

        // 「ステップ」のボタンにリスナーをセット
        buttonStep = findViewById(R.id.buttonStep);
        buttonStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.STEP, getString(R.string.step)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「ステップ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.STEP);

        // 「難易度」のボタンにリスナーをセット
        buttonDifficulty = findViewById(R.id.buttonDifficulty);
        buttonDifficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.DIFFICULTY, getString(R.string.difficulty)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「難易度」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.DIFFICULTY);

        // 「タイプ」のボタンにリスナーをセット
        buttonType = findViewById(R.id.buttonType);
        buttonType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.TYPE, getString(R.string.type)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「タイプ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.TYPE);

        // 「シリーズ」のボタンにリスナーをセット
        buttonSeries = findViewById(R.id.buttonSeries);
        buttonSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.SERIES, getString(R.string.series)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「シリーズ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.SERIES);


        // 「カテゴリー」のボタンにリスナーをセット
        buttonCategory = findViewById(R.id.buttonCategory);
        buttonCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.CATEGORY, getString(R.string.category)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「カテゴリー」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.CATEGORY);

        // 「その他」のボタンにリスナーをセット
        buttonOther = findViewById(R.id.buttonOther);
        buttonOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.OTHER, getString(R.string.other)).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });

        // 「お題を出す」のボタンにリスナーをセット
        buttonRun = findViewById(R.id.buttonRun);
        buttonRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 別スレッドでお題を出す
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // プログレスバーを初期化
                                ((ProgressBar) mainActivity.findViewById(R.id.progressBarRun)).setProgress(0);

                                // すべてのボタンをグレーアウトして押せなくする
                                buttonStep.setEnabled(false);
                                buttonDifficulty.setEnabled(false);
                                buttonType.setEnabled(false);
                                buttonSeries.setEnabled(false);
                                buttonCategory.setEnabled(false);
                                buttonOther.setEnabled(false);
                                buttonRun.setEnabled(false);

                                // 「今日のお題を出す」のボタンを「お待ちください…」に変更する
                                buttonRun.setText(R.string.getting_charts);
                            }
                        });

                        // お題の譜面の文字列を取得する
                        String subjectMessage;
                        try {
                            subjectMessage = getString(R.string.run_result, Chooser.run(mainActivity));
                        } catch (Exception e) {
                            switch (Chooser.cause) {
                            case CONNECTION:
                                // 通信エラーメッセージをセット
                                subjectMessage = getString(R.string.error_connection);
                                break;
                            case URL:
                                // URLエラーメッセージをセット
                                subjectMessage = getString(R.string.error_url);
                                break;
                            case OTHER:
                            default:
                                // ログ出力
                                Log.e(TAG, "onClick->" + e.getClass().toString());

                                // システムエラーメッセージをセット
                                subjectMessage = getString(R.string.error_system);
                                break;
                            }
                        }
                        final String finalSubjectMessage = subjectMessage;

                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // お題を表示させるダイアログを表示
                                CheckDialogFragment.newInstance(mainActivity, ButtonKind.RUN, finalSubjectMessage).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);

                                // 「お待ちください」のボタンを「今日のお題を出す」に変更する
                                buttonRun.setText(R.string.run);

                                // すべてのボタンをグレーアウトを解除して押せるようにする
                                buttonStep.setEnabled(true);
                                buttonDifficulty.setEnabled(true);
                                buttonType.setEnabled(true);
                                buttonSeries.setEnabled(true);
                                buttonCategory.setEnabled(true);
                                buttonOther.setEnabled(true);
                                buttonRun.setEnabled(true);
                            }
                        });
                    }
                }).start();
            }
        });
    }

    /**
     * メイン画面の各ボタンの下にあるTextViewのテキストを更新する
     * @param buttonKind 更新するテキストの上にあるボタンのタイプ
     */
    void updateTextByCheck(ButtonKind buttonKind) {
        // 更新後のテキスト文字列
        StringBuilder textBuilder = new StringBuilder();

        switch (buttonKind) {
        case STEP:
            // 更新後のテキスト文字列をセット
            for (int i = 0; i < CommonParams.step.length; i++) {
                if (CommonParams.step[i]) {
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
            for (int i = 0; i < CommonParams.difficulty.length; i++) {
                if (CommonParams.difficulty[i]) {
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
            for (int i = 0; i < CommonParams.type.length; i++) {
                if (CommonParams.type[i]) {
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
            for (int i = 0; i < CommonParams.series.length; i++) {
                if (CommonParams.series[i]) {
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
            for (int i = 0; i < CommonParams.category.length; i++) {
                if (CommonParams.category[i]) {
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
