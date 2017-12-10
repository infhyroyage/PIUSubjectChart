package com.piusubjectchart.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.piusubjectchart.R;

public class CheckDialogFragment extends AppCompatDialogFragment {
    // デバッグ用のタグ
    private static final String TAG = "CheckDialogFragment";

    // 呼びだされたMainActivityのインスタンス
    private static MainActivity mainActivity;

    static CheckDialogFragment newInstance(MainActivity mainActivity, ButtonType buttonType, int title) {
        CheckDialogFragment thisFragment = new CheckDialogFragment();
        CheckDialogFragment.mainActivity = mainActivity;

        // ダイアログのタイトル、メイン画面のボタンの種類をセット
        Bundle bundle = new Bundle();
        bundle.putSerializable("ButtonType", buttonType);
        bundle.putInt("Title", title);
        thisFragment.setArguments(bundle);

        return thisFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // ダイアログを生成し、タイトルを取得してセット
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        if (getArguments() == null) {
            throw new IllegalStateException("CheckDialogFragment.getArguments() returns null.");
        }
        builder.setTitle(getArguments().getInt("Title"));

        LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            throw new IllegalStateException("MainActivity.getSystemService() returns null.");
        }

        // メイン画面のボタンの種類を取得
        ButtonType buttonType;
        if ((buttonType = (ButtonType) getArguments().getSerializable("ButtonType")) == null) {
            throw new IllegalArgumentException("The CommonDialogType argument cannot be applied.");
        } else {
            // ログ出力
            Log.d(TAG, "onCreateDialog:buttonType=" + buttonType);

            switch (buttonType) {
                case TYPE:
                    View checkView = inflater.inflate(R.layout.select_checkbox_dialog, (ViewGroup) mainActivity.findViewById(R.id.checkLayout));

                    builder.setView(checkView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            }).setNegativeButton(R.string.cancel, null);

                    return builder.create();
                case DIFFICULTY:
                    // TableLayoutの取得
                    checkView = inflater.inflate(R.layout.select_checkbox_dialog, (ViewGroup) mainActivity.findViewById(R.id.checkLayout));
                    TableLayout tableLayout = checkView.findViewById(R.id.checkLayout);

                    // MIN_DIFFICULTY〜MAX_DIFFICULTYの個数分だけ1行生成して格納
                    for (int i = 0; i < mainActivity.difficulty.length; i++) {
                        // TableRowの格納
                        TableRow row = new TableRow(mainActivity);
                        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tableLayout.addView(row);

                        // TextViewの格納
                        TextView textView = new TextView(mainActivity);
                        textView.setText(String.valueOf(i + 1));
                        row.addView(textView);

                        // Switchの格納
                        Switch s = new Switch(mainActivity);
                        s.setChecked(mainActivity.difficulty[i]);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mainActivity.difficulty[idx] = isChecked;
                            }
                        });
                        row.addView(s);
                    }

                    //  「難易度」のON/OFF状態をバックアップ
                    final boolean[] difficultyBackup = mainActivity.difficulty.clone();

                    builder.setView(checkView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    // 「難易度」のボタンの下にあるTextViewの文字を更新
                                    mainActivity.updateTextByCheck(ButtonType.DIFFICULTY);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // バックアップしたON/OFFの状態をセット
                                     mainActivity.difficulty = difficultyBackup.clone();
                                }
                    });

                    return builder.create();
                case VERSION:
                    checkView = inflater.inflate(R.layout.select_checkbox_dialog, (ViewGroup) mainActivity.findViewById(R.id.checkLayout));

                    builder.setView(checkView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            }).setNegativeButton(R.string.cancel, null);

                    return builder.create();
                case RUN:
                    View resultView = inflater.inflate(R.layout.select_checkbox_dialog, (ViewGroup) mainActivity.findViewById(R.id.checkLayout));

                    builder.setView(resultView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            }).setNegativeButton(R.string.cancel, null);

                    return builder.create();
                default:
                    throw new IllegalArgumentException("The ButtonType argument cannot be applied.");
            }
        }
    }
}
