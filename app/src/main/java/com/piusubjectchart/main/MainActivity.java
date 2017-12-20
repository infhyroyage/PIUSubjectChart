package com.piusubjectchart.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.piusubjectchart.CommonParams;
import com.piusubjectchart.R;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // デバッグ用のタグ
    private static final String TAG = "MainActivity";

    // MainActivityのインスタンス
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面取得
        setContentView(R.layout.activity_main);

        // MainActivityのインスタンス
        mainActivity = this;
        // チェック状態を初期化
        Arrays.fill(CommonParams.step, true);
        Arrays.fill(CommonParams.difficulty, true);
        Arrays.fill(CommonParams.type, true);
        Arrays.fill(CommonParams.series, true);
        Arrays.fill(CommonParams.category, true);

        // 「ステップ」のボタンにリスナーをセット
        findViewById(R.id.buttonStep).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.STEP, R.string.step).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「ステップ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.STEP);

        // 「難易度」のボタンにリスナーをセット
        findViewById(R.id.buttonDifficulty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.DIFFICULTY, R.string.difficulty).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「難易度」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.DIFFICULTY);

        // 「タイプ」のボタンにリスナーをセット
        findViewById(R.id.buttonType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.TYPE, R.string.type).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「タイプ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.TYPE);

        // 「シリーズ」のボタンにリスナーをセット
        findViewById(R.id.buttonSeries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.SERIES, R.string.series).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「シリーズ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.SERIES);

        // 「カテゴリー」のボタンにリスナーをセット
        findViewById(R.id.buttonCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.CATEGORY, R.string.category).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「カテゴリー」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.CATEGORY);

        // 「お題を出す」のボタンにリスナーをセット
        findViewById(R.id.buttonRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.RUN, R.string.run).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
    }

    /**
     * メイン画面の各ボタンの下にあるTextViewのテキストを更新する
     * @param buttonKind : 更新するテキストの上にあるボタンのタイプ
     */
    void updateTextByCheck(ButtonKind buttonKind) {
        // ログ出力
        Log.d(TAG, "updateTextByCheck:buttonKind=" + buttonKind);

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
