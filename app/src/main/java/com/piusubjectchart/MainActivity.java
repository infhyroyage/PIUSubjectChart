package com.piusubjectchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    // デバッグ用のタグ
    private static final String TAG = "MainActivity";

    // MainActivityのインスタンス
    private MainActivity mainActivity;

    // 「難易度」のチェック状態
    public boolean[] difficulty = new boolean[CommonParams.MAX_DIFFICULTY - CommonParams.MIN_DIFFICULTY + 1];

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面取得
        setContentView(R.layout.activity_main);

        // MainActivityのインスタンス
        mainActivity = this;
        // 「難易度」のチェック状態
        Arrays.fill(difficulty, true);

        // 「譜面タイプ」のボタンにリスナーをセット
        findViewById(R.id.buttonType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CheckDialogFragment.newInstance(mainActivity, ButtonType.TYPE, R.string.type).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });

        // 「難易度」のボタンにリスナーをセット
        findViewById(R.id.buttonDifficulty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonType.DIFFICULTY, R.string.difficulty).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「難易度」の下にあるテキストを更新
        updateTextByCheck(ButtonType.DIFFICULTY);

        // 「バージョン」のボタンにリスナーをセット
        findViewById(R.id.buttonVersion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CheckDialogFragment.newInstance(mainActivity, ButtonType.VERSION, R.string.version).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });

        // 「お題を出す」のボタンにリスナーをセット
        findViewById(R.id.buttonRun).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonType.RUN, R.string.run).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
    }

    /**
     * メイン画面の各ボタンの下にあるTextViewのテキストを更新する
     * @param buttonType : 更新するテキストの上にあるボタンのタイプ
     */
    public void updateTextByCheck(ButtonType buttonType) {
        // ログ出力
        Log.d(TAG, "updateTextByCheck:buttonType" + buttonType);

        // 更新後のテキスト文字列
        StringBuilder textBuilder = new StringBuilder();

        switch (buttonType) {
            case TYPE:
                break;
            case DIFFICULTY:
                // 更新後のテキスト文字列をセット
                for (int i = 0; i < difficulty.length; i++) {
                    if (difficulty[i]) {
                        if (!textBuilder.toString().equals("")) {
                            textBuilder.append(",");
                        }
                        textBuilder.append(String.valueOf(i + 1));
                    }
                }

                // TextViewにセット
                ((TextView)findViewById(R.id.textViewDifficulty)).setText(getString(R.string.current, textBuilder.toString()));
                break;
            case VERSION:
                break;
            default:
                throw new IllegalArgumentException("The ButtonType argument cannot be applied.");
        }
    }
}
