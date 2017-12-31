package com.subject.piu.main;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.subject.piu.CommonParams;
import com.subject.R;

import java.util.Arrays;

public class CheckDialogFragment extends AppCompatDialogFragment {
    // デバッグ用のタグ
    private static final String TAG = "CheckDialogFragment";

    // 呼びだされたMainActivityのインスタンス
    private MainActivity mainActivity;

    /**
     * このクラスのインスタンスの初期化を行い、それを返す
     * @param mainActivity MainActivityのインスタンス
     * @param buttonKind メイン画面のボタンの種類を表す列挙型
     * @param args ダイアログのタイトル/メッセージなど
     * @return このクラスのインスタンス
     */
    static CheckDialogFragment newInstance(MainActivity mainActivity, ButtonKind buttonKind, String... args) {
        CheckDialogFragment thisFragment = new CheckDialogFragment();
        thisFragment.mainActivity = mainActivity;

        // ダイアログのタイトル、メイン画面のボタンの種類をセット
        Bundle bundle = new Bundle();
        bundle.putSerializable("ButtonKind", buttonKind);
        for (int i = 0; i < args.length; i++) {
            bundle.putString("args[" + i + "]", args[i]);
        }
        thisFragment.setArguments(bundle);

        return thisFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // ダイアログとそのビルダーを生成
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        if (getArguments() == null) {
            throw new IllegalStateException("CheckDialogFragment.getArguments() returns null.");
        }

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

            // チェック状態のバックアップ
            final boolean[] backup;

            // 1dpの大きさをpx単位で計算
            float dp = mainActivity.getResources().getDisplayMetrics().density;

            switch (buttonKind) {
            case STEP:
                // TableLayoutの取得
                View switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                TableLayout tableLayout = switchesView.findViewById(R.id.switchingLayout);

                // 「ステップ」の個数分だけ1行生成して格納
                for (int i = 0; i < CommonParams.stepChecks.length; i++) {
                    // TableRowの格納
                    TableRow row = new TableRow(mainActivity);
                    row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                    tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // TextViewの格納
                    TextView textView = new TextView(mainActivity);
                    textView.setText(CommonParams.STEPS[i]);
                    textView.setPadding((int) (20 * dp), 0, 0, 0);
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // Switchの格納
                    Switch s = new Switch(mainActivity);
                    s.setChecked(CommonParams.stepChecks[i]);
                    s.setPadding(0, 0, (int) (20 * dp), 0);
                    final int idx = i;
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommonParams.stepChecks[idx] = isChecked;
                        }
                    });
                    row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                }

                // 「ステップ」のON/OFF状態をバックアップ
                backup = CommonParams.stepChecks.clone();

                builder.setView(switchesView)
                        .setTitle(getArguments().getString("args[0]"))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                if (Arrays.equals(CommonParams.stepChecks, new boolean[CommonParams.stepChecks.length])) {
                                    Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                    CommonParams.stepChecks = backup.clone();
                                } else {
                                    // 「ステップ」のチェック状態をSharedPreferenceに保存
                                    SharedPreferences.Editor editor = mainActivity.sp.edit();
                                    for (int i = 0; i < CommonParams.stepChecks.length; i++) {
                                        editor = editor.putBoolean("stepChecks[" + i + "]", CommonParams.stepChecks[i]);
                                    }
                                    editor.apply();

                                    // 「ステップ」のボタンの下にあるTextViewの文字を更新
                                    mainActivity.updateTextViewUnderButton(ButtonKind.STEP);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // バックアップしたON/OFFの状態をセット
                        CommonParams.stepChecks = backup.clone();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        // Backキーを押した場合、バックアップしたON/OFFの状態をセット
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            CommonParams.stepChecks = backup.clone();
                        }
                        return false;
                    }
                });

                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case DIFFICULTY:
                // TableLayoutの取得
                switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                tableLayout = switchesView.findViewById(R.id.switchingLayout);

                // MIN_DIFFICULTY〜MAX_DIFFICULTYの個数分だけ1行生成して格納
                for (int i = 0; i < CommonParams.difficultyChecks.length; i++) {
                    // TableRowの格納
                    TableRow row = new TableRow(mainActivity);
                    row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                    tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // TextViewの格納
                    TextView textView = new TextView(mainActivity);
                    textView.setText(String.valueOf(i + 1));
                    textView.setPadding((int) (20 * dp), 0, 0, 0);
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // Switchの格納
                    Switch s = new Switch(mainActivity);
                    s.setChecked(CommonParams.difficultyChecks[i]);
                    s.setPadding(0, 0, (int) (20 * dp), 0);
                    final int idx = i;
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommonParams.difficultyChecks[idx] = isChecked;
                        }
                    });
                    row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                }

                // 「難易度」のON/OFF状態をバックアップ
                backup = CommonParams.difficultyChecks.clone();

                builder.setView(switchesView)
                        .setTitle(getArguments().getString("args[0]"))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                if (Arrays.equals(CommonParams.difficultyChecks, new boolean[CommonParams.difficultyChecks.length])) {
                                    Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                    CommonParams.difficultyChecks = backup.clone();
                                } else {
                                    // 「難易度」のチェック状態をSharedPreferenceに保存
                                    SharedPreferences.Editor editor = mainActivity.sp.edit();
                                    for (int i = 0; i < CommonParams.difficultyChecks.length; i++) {
                                        editor = editor.putBoolean("difficultyChecks[" + i + "]", CommonParams.difficultyChecks[i]);
                                    }
                                    editor.apply();

                                    // 「難易度」のボタンの下にあるTextViewの文字を更新
                                    mainActivity.updateTextViewUnderButton(ButtonKind.DIFFICULTY);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // バックアップしたON/OFFの状態をセット
                        CommonParams.difficultyChecks = backup.clone();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        // Backキーを押した場合、バックアップしたON/OFFの状態をセット
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            CommonParams.difficultyChecks = backup.clone();
                        }
                        return false;
                    }
                });

                // ビルダーからダイアログを生成し、ダイアログ外をタップしても閉じないようにセット
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case TYPE:
                // TableLayoutの取得
                switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                tableLayout = switchesView.findViewById(R.id.switchingLayout);

                // 「タイプ」の個数分だけ1行生成して格納
                for (int i = 0; i < CommonParams.typeChecks.length; i++) {
                    // TableRowの格納
                    TableRow row = new TableRow(mainActivity);
                    row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                    tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // TextViewの格納
                    TextView textView = new TextView(mainActivity);
                    textView.setText(CommonParams.TYPES[i]);
                    textView.setPadding((int) (20 * dp), 0, 0, 0);
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // Switchの格納
                    Switch s = new Switch(mainActivity);
                    s.setChecked(CommonParams.typeChecks[i]);
                    s.setPadding(0, 0, (int) (20 * dp), 0);
                    final int idx = i;
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommonParams.typeChecks[idx] = isChecked;
                        }
                    });
                    row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                }

                // 「タイプ」のON/OFF状態をバックアップ
                backup = CommonParams.typeChecks.clone();

                builder.setView(switchesView)
                        .setTitle(getArguments().getString("args[0]"))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                if (Arrays.equals(CommonParams.typeChecks, new boolean[CommonParams.typeChecks.length])) {
                                    Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                    CommonParams.typeChecks = backup.clone();
                                } else {
                                    // 「タイプ」のチェック状態をSharedPreferenceに保存
                                    SharedPreferences.Editor editor = mainActivity.sp.edit();
                                    for (int i = 0; i < CommonParams.typeChecks.length; i++) {
                                        editor = editor.putBoolean("typeChecks[" + i + "]", CommonParams.typeChecks[i]);
                                    }
                                    editor.apply();

                                    // 「タイプ」のボタンの下にあるTextViewの文字を更新
                                    mainActivity.updateTextViewUnderButton(ButtonKind.TYPE);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // バックアップしたON/OFFの状態をセット
                        CommonParams.typeChecks = backup.clone();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        // Backキーを押した場合、バックアップしたON/OFFの状態をセット
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            CommonParams.typeChecks = backup.clone();
                        }
                        return false;
                    }
                });

                // ビルダーからダイアログを生成し、ダイアログ外をタップしても閉じないようにセット
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case SERIES:
                // TableLayoutの取得
                switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                tableLayout = switchesView.findViewById(R.id.switchingLayout);

                // 「シリーズ」の個数分だけ1行生成して格納
                for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
                    // TableRowの格納
                    TableRow row = new TableRow(mainActivity);
                    row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                    tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // TextViewの格納
                    TextView textView = new TextView(mainActivity);
                    textView.setText(CommonParams.SERIES[i]);
                    textView.setPadding((int) (20 * dp), 0, 0, 0);
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // Switchの格納
                    Switch s = new Switch(mainActivity);
                    s.setChecked(CommonParams.seriesChecks[i]);
                    s.setPadding(0, 0, (int) (20 * dp), 0);
                    final int idx = i;
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommonParams.seriesChecks[idx] = isChecked;
                        }
                    });
                    row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                }

                // 「シリーズ」のON/OFF状態をバックアップ
                backup = CommonParams.seriesChecks.clone();

                builder.setView(switchesView)
                        .setTitle(getArguments().getString("args[0]"))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                if (Arrays.equals(CommonParams.seriesChecks, new boolean[CommonParams.seriesChecks.length])) {
                                    Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                    CommonParams.seriesChecks = backup.clone();
                                } else {
                                    // 「シリーズ」のチェック状態をSharedPreferenceに保存
                                    SharedPreferences.Editor editor = mainActivity.sp.edit();
                                    for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
                                        editor = editor.putBoolean("seriesChecks[" + i + "]", CommonParams.seriesChecks[i]);
                                    }
                                    editor.apply();

                                    // 「シリーズ」のボタンの下にあるTextViewの文字を更新
                                    mainActivity.updateTextViewUnderButton(ButtonKind.SERIES);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // バックアップしたON/OFFの状態をセット
                        CommonParams.seriesChecks = backup.clone();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        // Backキーを押した場合、バックアップしたON/OFFの状態をセット
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            CommonParams.seriesChecks = backup.clone();
                        }
                        return false;
                    }
                });

                // ビルダーからダイアログを生成し、ダイアログ外をタップしても閉じないようにセット
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case CATEGORY:
                // TableLayoutの取得
                switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                tableLayout = switchesView.findViewById(R.id.switchingLayout);

                // 「カテゴリー」の個数分だけ1行生成して格納
                for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
                    // TableRowの格納
                    TableRow row = new TableRow(mainActivity);
                    row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                    tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // TextViewの格納
                    TextView textView = new TextView(mainActivity);
                    textView.setText(CommonParams.CATEGORIES[i]);
                    textView.setPadding((int) (20 * dp), 0, 0, 0);
                    row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                    // Switchの格納
                    Switch s = new Switch(mainActivity);
                    s.setChecked(CommonParams.categoryChecks[i]);
                    s.setPadding(0, 0, (int) (20 * dp), 0);
                    final int idx = i;
                    s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            CommonParams.categoryChecks[idx] = isChecked;
                        }
                    });
                    row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                }

                // 「カテゴリー」のON/OFF状態をバックアップ
                backup = CommonParams.categoryChecks.clone();

                builder.setView(switchesView)
                        .setTitle(getArguments().getString("args[0]"))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                if (Arrays.equals(CommonParams.categoryChecks, new boolean[CommonParams.categoryChecks.length])) {
                                    Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                    CommonParams.categoryChecks = backup.clone();
                                } else {
                                    // 「カテゴリー」のチェック状態をSharedPreferenceに保存
                                    SharedPreferences.Editor editor = mainActivity.sp.edit();
                                    for (int i = 0; i < CommonParams.categoryChecks.length; i++) {
                                        editor = editor.putBoolean("categoryChecks[" + i + "]", CommonParams.categoryChecks[i]);
                                    }
                                    editor.apply();

                                    // 「カテゴリー」のボタンの下にあるTextViewの文字を更新
                                    mainActivity.updateTextViewUnderButton(ButtonKind.CATEGORY);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // バックアップしたON/OFFの状態をセット
                        CommonParams.categoryChecks = backup.clone();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        // Backキーを押した場合、バックアップしたON/OFFの状態をセット
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            CommonParams.categoryChecks = backup.clone();
                        }
                        return false;
                    }
                });

                // ビルダーからダイアログを生成し、ダイアログ外をタップしても閉じないようにセット
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case OTHER:
                // TableLayoutの取得
                switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                tableLayout = switchesView.findViewById(R.id.switchingLayout);

                // 「PP解禁譜面を含む」のTableRowの格納
                TableRow row = new TableRow(mainActivity);
                row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // 「PP解禁譜面を含む」のTextViewの格納
                TextView textView = new TextView(mainActivity);
                textView.setText(CommonParams.PP_UNLOCKED_STEP);
                textView.setPadding((int) (20 * dp), 0, 0, 0);
                row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // 「PP解禁譜面を含む」のSwitchの格納
                Switch s = new Switch(mainActivity);
                s.setChecked(CommonParams.ppUnlockedStepCheck);
                s.setPadding(0, 0, (int) (20 * dp), 0);
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CommonParams.ppUnlockedStepCheck = isChecked;
                    }
                });
                row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                // 「AM.PASS使用時限定譜面を含む」のTableRowの格納
                row = new TableRow(mainActivity);
                row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // 「AM.PASS使用時限定譜面を含む」のTextViewの格納
                textView = new TextView(mainActivity);
                textView.setText(CommonParams.AM_PASS_ONLY_USED_STEP);
                textView.setPadding((int) (20 * dp), 0, 0, 0);
                row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                // 「AM.PASS使用時限定譜面を含む」のSwitchの格納
                s = new Switch(mainActivity);
                s.setChecked(CommonParams.amPassOnlyUsedStepCheck);
                s.setPadding(0, 0, (int) (20 * dp), 0);
                s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        CommonParams.amPassOnlyUsedStepCheck = isChecked;
                    }
                });
                row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                // 「その他」のON/OFF状態をバックアップ
                final boolean backupPpUnlockedStep = CommonParams.ppUnlockedStepCheck;
                final boolean backupAmPassOnlyUsedStep = CommonParams.amPassOnlyUsedStepCheck;

                builder.setView(switchesView)
                        .setCancelable(false)
                        .setTitle(getArguments().getString("args[0]"))
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // チェック状態をSharedPreferenceに保存
                                mainActivity.sp.edit()
                                        .putBoolean("ppUnlockedStepCheck", CommonParams.ppUnlockedStepCheck)
                                        .putBoolean("amPassOnlyUsedStepCheck", CommonParams.amPassOnlyUsedStepCheck)
                                        .apply();
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // バックアップしたON/OFFの状態をセット
                        CommonParams.ppUnlockedStepCheck = backupPpUnlockedStep;
                        CommonParams.amPassOnlyUsedStepCheck = backupAmPassOnlyUsedStep;
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                        // Backキーを押した場合、バックアップしたON/OFFの状態をセット
                        if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            CommonParams.ppUnlockedStepCheck = backupPpUnlockedStep;
                            CommonParams.amPassOnlyUsedStepCheck = backupAmPassOnlyUsedStep;
                        }
                        return false;
                    }
                });

                // ビルダーからダイアログを生成し、ダイアログ外をタップしても閉じないようにセット
                dialog = builder.create();
                dialog.setCanceledOnTouchOutside(false);
                return dialog;
            case POP:
                final String message = getArguments().getString("args[0]");
                builder = builder.setMessage(message)
                        .setPositiveButton(R.string.ok, null);
                if (getArguments().getString("args[1]", "none").equals("share")) {
                    builder = builder.setNeutralButton(R.string.share, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 共有インテントの設定
                            ShareCompat.IntentBuilder.from(mainActivity)
                                    .setType("text/plain")
                                    .setText(message)
                                    .startChooser();
                        }
                    });
                }

                // ビルダーからダイアログを生成
                return builder.create();
            default:
                throw new IllegalArgumentException("The ButtonKind argument cannot be applied.");
            }
        }
    }
}
