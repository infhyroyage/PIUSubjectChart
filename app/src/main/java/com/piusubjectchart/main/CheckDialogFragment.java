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
import android.widget.Toast;

import com.piusubjectchart.CommonParams;
import com.piusubjectchart.R;

import java.util.Arrays;

public class CheckDialogFragment extends AppCompatDialogFragment {
    // デバッグ用のタグ
    private static final String TAG = "CheckDialogFragment";

    // 呼びだされたMainActivityのインスタンス
    private static MainActivity mainActivity;

    static CheckDialogFragment newInstance(MainActivity mainActivity, ButtonKind buttonKind, int title) {
        CheckDialogFragment thisFragment = new CheckDialogFragment();
        CheckDialogFragment.mainActivity = mainActivity;

        // ダイアログのタイトル、メイン画面のボタンの種類をセット
        Bundle bundle = new Bundle();
        bundle.putSerializable("ButtonKind", buttonKind);
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
        ButtonKind buttonKind;
        if ((buttonKind = (ButtonKind) getArguments().getSerializable("ButtonKind")) == null) {
            throw new IllegalArgumentException("The CommonDialogType argument cannot be applied.");
        } else {
            // ログ出力
            Log.d(TAG, "onCreateDialog:buttonKind=" + buttonKind);

            switch (buttonKind) {
                case TYPE:
                    // TableLayoutの取得
                    View switchesView = inflater.inflate(R.layout.set_switches_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchesLayout));
                    TableLayout tableLayout = switchesView.findViewById(R.id.switchesLayout);

                    // TYPESの個数分だけ1行生成して格納
                    for (int i = 0; i < mainActivity.type.length; i++) {
                        // TableRowの格納
                        TableRow row = new TableRow(mainActivity);
                        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tableLayout.addView(row);

                        // TextViewの格納
                        TextView textView = new TextView(mainActivity);
                        textView.setText(CommonParams.TYPES[i]);
                        row.addView(textView);

                        // Switchの格納
                        Switch s = new Switch(mainActivity);
                        s.setChecked(mainActivity.type[i]);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mainActivity.type[idx] = isChecked;
                            }
                        });
                        row.addView(s);
                    }

                    //  「譜面タイプ」のON/OFF状態をバックアップ
                    final boolean[] typeBackup = mainActivity.type.clone();

                    builder.setView(switchesView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(mainActivity.type, new boolean[mainActivity.type.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        mainActivity.type = typeBackup.clone();
                                    } else {
                                        // 上記以外の場合は、「難易度」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.TYPE);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            mainActivity.type = typeBackup.clone();
                        }
                    });

                    return builder.create();
                case DIFFICULTY:
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.set_switches_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchesLayout));
                    tableLayout = switchesView.findViewById(R.id.switchesLayout);

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

                    builder.setView(switchesView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(mainActivity.difficulty, new boolean[mainActivity.difficulty.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        mainActivity.difficulty = difficultyBackup.clone();
                                    } else {
                                        // 上記以外の場合は、「難易度」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.DIFFICULTY);
                                    }
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
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.set_switches_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchesLayout));
                    tableLayout = switchesView.findViewById(R.id.switchesLayout);

                    // VERSIONSの個数分だけ1行生成して格納
                    for (int i = 0; i < mainActivity.version.length; i++) {
                        // TableRowの格納
                        TableRow row = new TableRow(mainActivity);
                        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                        tableLayout.addView(row);

                        // TextViewの格納
                        TextView textView = new TextView(mainActivity);
                        textView.setText(CommonParams.VERSIONS[i]);
                        row.addView(textView);

                        // Switchの格納
                        Switch s = new Switch(mainActivity);
                        s.setChecked(mainActivity.version[i]);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                mainActivity.version[idx] = isChecked;
                            }
                        });
                        row.addView(s);
                    }

                    //  「バージョン」のON/OFF状態をバックアップ
                    final boolean[] versionBackup = mainActivity.version.clone();

                    builder.setView(switchesView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(mainActivity.version, new boolean[mainActivity.version.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        mainActivity.version = versionBackup.clone();
                                    } else {
                                        // 上記以外の場合は、「バージョン」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.VERSION);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            mainActivity.type = versionBackup.clone();
                        }
                    });

                    return builder.create();
                case RUN:
                    View resultView = inflater.inflate(R.layout.set_switches_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchesLayout));

                    builder.setView(resultView)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                }
                            }).setNegativeButton(R.string.cancel, null);

                    return builder.create();
                default:
                    throw new IllegalArgumentException("The ButtonKind argument cannot be applied.");
            }
        }
    }
}
