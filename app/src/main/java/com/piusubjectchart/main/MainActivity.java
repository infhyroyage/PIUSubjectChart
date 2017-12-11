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

    // 「譜面タイプ」のチェック状態
    boolean[] type = new boolean[CommonParams.TYPES.length];
    // 「難易度」のチェック状態
    boolean[] difficulty = new boolean[CommonParams.MAX_DIFFICULTY - CommonParams.MIN_DIFFICULTY + 1];
    // 「バージョン」のチェック状態
    boolean[] version = new boolean[CommonParams.VERSIONS.length];

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面取得
        setContentView(R.layout.activity_main);

        // MainActivityのインスタンス
        mainActivity = this;
        // チェック状態を初期化
        Arrays.fill(type, true);
        Arrays.fill(difficulty, true);
        Arrays.fill(version, true);

        // 「譜面タイプ」のボタンにリスナーをセット
        findViewById(R.id.buttonType).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.TYPE, R.string.type).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「譜面タイプ」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.TYPE);

        // 「難易度」のボタンにリスナーをセット
        findViewById(R.id.buttonDifficulty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.DIFFICULTY, R.string.difficulty).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「難易度」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.DIFFICULTY);

        // 「バージョン」のボタンにリスナーをセット
        findViewById(R.id.buttonVersion).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckDialogFragment.newInstance(mainActivity, ButtonKind.VERSION, R.string.version).show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
            }
        });
        // 「バージョン」の下にあるテキストを更新
        updateTextByCheck(ButtonKind.VERSION);

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
            case TYPE:
                // 更新後のテキスト文字列をセット
                for (int i = 0; i < type.length; i++) {
                    if (type[i]) {
                        if (!textBuilder.toString().equals("")) {
                            textBuilder.append(", ");
                        }
                        textBuilder.append(CommonParams.TYPES[i]);
                    }
                }

                // TextViewにセット
                ((TextView)findViewById(R.id.textViewType)).setText(getString(R.string.current, textBuilder.toString()));
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
                // 更新後のテキスト文字列をセット
                for (int i = 0; i < version.length; i++) {
                    if (version[i]) {
                        if (!textBuilder.toString().equals("")) {
                            textBuilder.append(", ");
                        }
                        textBuilder.append(CommonParams.VERSIONS[i]);
                    }
                }

                // TextViewにセット
                ((TextView)findViewById(R.id.textViewVersion)).setText(getString(R.string.current, textBuilder.toString()));
                break;
            default:
                throw new IllegalArgumentException("The ButtonKind argument cannot be applied.");
        }
    }
}
