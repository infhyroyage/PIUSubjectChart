package com.subject.piu.main;

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

import com.subject.piu.CommonParams;
import com.subject.R;
import com.subject.piu.chart.Chooser;

import java.io.IOException;
import java.util.Arrays;

public class CheckDialogFragment extends AppCompatDialogFragment {
    // デバッグ用のタグ
    private static final String TAG = "CheckDialogFragment";

    // 呼びだされたMainActivityのインスタンス
    private static MainActivity mainActivity;

    // 1dpの大きさ(単位:px)
    private static float dp;

    /**
     * このクラスのインスタンスの初期化を行い、それを返す
     * @param mainActivity : MainActivityのインスタンス
     * @param buttonKind : メイン画面のボタンの種類を表す列挙型
     * @param title : ダイアログのタイトル
     * @return このクラスのインスタンス
     */
    static CheckDialogFragment newInstance(MainActivity mainActivity, ButtonKind buttonKind, int title) {
        CheckDialogFragment thisFragment = new CheckDialogFragment();
        CheckDialogFragment.mainActivity = mainActivity;

        // ダイアログのタイトル、メイン画面のボタンの種類をセット
        Bundle bundle = new Bundle();
        bundle.putSerializable("ButtonKind", buttonKind);
        bundle.putInt("Title", title);
        thisFragment.setArguments(bundle);

        // 1dpの大きさをpx単位で計算
        dp = mainActivity.getResources().getDisplayMetrics().density;

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

            switch (buttonKind) {
                case STEP:
                    // TableLayoutの取得
                    View switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                    TableLayout tableLayout = switchesView.findViewById(R.id.switchingLayout);

                    // 「ステップ」の個数分だけ1行生成して格納
                    for (int i = 0; i < CommonParams.step.length; i++) {
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
                        s.setChecked(CommonParams.step[i]);
                        s.setPadding(0, 0, (int) (20 * dp), 0);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                CommonParams.step[idx] = isChecked;
                            }
                        });
                        row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    //  「ステップ」のON/OFF状態をバックアップ
                    backup = CommonParams.step.clone();

                    builder.setView(switchesView)
                            .setTitle(getArguments().getInt("Title"))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(CommonParams.step, new boolean[CommonParams.step.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        CommonParams.step = backup.clone();
                                    } else {
                                        // 上記以外の場合は、「難易度」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.STEP);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            CommonParams.step = backup.clone();
                        }
                    });

                    return builder.create();
                case DIFFICULTY:
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                    tableLayout = switchesView.findViewById(R.id.switchingLayout);

                    // MIN_DIFFICULTY〜MAX_DIFFICULTYの個数分だけ1行生成して格納
                    for (int i = 0; i < CommonParams.difficulty.length; i++) {
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
                        s.setChecked(CommonParams.difficulty[i]);
                        s.setPadding(0, 0, (int) (20 * dp), 0);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                CommonParams.difficulty[idx] = isChecked;
                            }
                        });
                        row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    //  「難易度」のON/OFF状態をバックアップ
                    backup = CommonParams.difficulty.clone();

                    builder.setView(switchesView)
                            .setTitle(getArguments().getInt("Title"))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(CommonParams.difficulty, new boolean[CommonParams.difficulty.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        CommonParams.difficulty = backup.clone();
                                    } else {
                                        // 上記以外の場合は、「難易度」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.DIFFICULTY);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // バックアップしたON/OFFの状態をセット
                                     CommonParams.difficulty = backup.clone();
                                }
                    });

                    return builder.create();
                case TYPE:
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                    tableLayout = switchesView.findViewById(R.id.switchingLayout);

                    // 「タイプ」の個数分だけ1行生成して格納
                    for (int i = 0; i < CommonParams.type.length; i++) {
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
                        s.setChecked(CommonParams.type[i]);
                        s.setPadding(0, 0, (int) (20 * dp), 0);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                CommonParams.type[idx] = isChecked;
                            }
                        });
                        row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    //  「タイプ」のON/OFF状態をバックアップ
                    backup = CommonParams.type.clone();

                    builder.setView(switchesView)
                            .setTitle(getArguments().getInt("Title"))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(CommonParams.type, new boolean[CommonParams.type.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        CommonParams.type = backup.clone();
                                    } else {
                                        // 上記以外の場合は、「シリーズ」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.TYPE);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            CommonParams.type = backup.clone();
                        }
                    });

                    return builder.create();
                case SERIES:
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                    tableLayout = switchesView.findViewById(R.id.switchingLayout);

                    // 「シリーズ」の個数分だけ1行生成して格納
                    for (int i = 0; i < CommonParams.series.length; i++) {
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
                        s.setChecked(CommonParams.series[i]);
                        s.setPadding(0, 0, (int) (20 * dp), 0);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                CommonParams.series[idx] = isChecked;
                            }
                        });
                        row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    //  「シリーズ」のON/OFF状態をバックアップ
                    backup = CommonParams.series.clone();

                    builder.setView(switchesView)
                            .setTitle(getArguments().getInt("Title"))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(CommonParams.series, new boolean[CommonParams.series.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        CommonParams.series = backup.clone();
                                    } else {
                                        // 上記以外の場合は、「シリーズ」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.SERIES);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            CommonParams.series = backup.clone();
                        }
                    });

                    return builder.create();
                case CATEGORY:
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                    tableLayout = switchesView.findViewById(R.id.switchingLayout);

                    // 「カテゴリー」の個数分だけ1行生成して格納
                    for (int i = 0; i < CommonParams.category.length; i++) {
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
                        s.setChecked(CommonParams.category[i]);
                        s.setPadding(0, 0, (int) (20 * dp), 0);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                CommonParams.category[idx] = isChecked;
                            }
                        });
                        row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    //  「カテゴリー」のON/OFF状態をバックアップ
                    backup = CommonParams.category.clone();

                    builder.setView(switchesView)
                            .setTitle(getArguments().getInt("Title"))
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {
                                    if (Arrays.equals(CommonParams.category, new boolean[CommonParams.category.length])) {
                                        // SwitchがすべてOFFの場合は更新せず、バックアップしたON/OFFの状態をセット
                                        Toast.makeText(mainActivity, R.string.error_all_off, Toast.LENGTH_SHORT).show();
                                        CommonParams.category = backup.clone();
                                    } else {
                                        // 上記以外の場合は、「カテゴリー」のボタンの下にあるTextViewの文字を更新
                                        mainActivity.updateTextByCheck(ButtonKind.CATEGORY);
                                    }
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            CommonParams.category = backup.clone();
                        }
                    });

                    return builder.create();
                case OTHER:
                    // TableLayoutの取得
                    switchesView = inflater.inflate(R.layout.switching_dialog, (ViewGroup) mainActivity.findViewById(R.id.switchingLayout));
                    tableLayout = switchesView.findViewById(R.id.switchingLayout);

                    // 「その他」の個数分だけ1行生成して格納
                    for (int i = 0; i < CommonParams.other.length; i++) {
                        // TableRowの格納
                        TableRow row = new TableRow(mainActivity);
                        row.setPadding(0, (int) (10 * dp), 0, (int) (10 * dp));
                        tableLayout.addView(row, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                        // TextViewの格納
                        TextView textView = new TextView(mainActivity);
                        textView.setText(CommonParams.OTHERS[i]);
                        textView.setPadding((int) (20 * dp), 0, 0, 0);
                        row.addView(textView, new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

                        // Switchの格納
                        Switch s = new Switch(mainActivity);
                        s.setChecked(CommonParams.other[i]);
                        s.setPadding(0, 0, (int) (20 * dp), 0);
                        final int idx = i;
                        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                CommonParams.other[idx] = isChecked;
                            }
                        });
                        row.addView(s, new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    }

                    //  「その他」のON/OFF状態をバックアップ
                    backup = CommonParams.other.clone();

                    builder.setView(switchesView)
                            .setTitle(getArguments().getInt("Title"))
                            .setPositiveButton(R.string.ok, null).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // バックアップしたON/OFFの状態をセット
                            CommonParams.other = backup.clone();
                        }
                    });

                    return builder.create();
                case RUN:
                    try {
                        // お題を取得し、メッセージをセット
                        builder.setMessage(getString(R.string.run_result, Chooser.run()));
                    } catch (IOException e) {
                        switch (Chooser.cause) {
                        case CONNECTION:
                            // 通信エラーメッセージをセット
                            builder.setMessage(getString(R.string.error_connection));
                            break;
                        case URL:
                            // URLエラーメッセージをセット
                            builder.setMessage(getString(R.string.error_url));
                            break;
                        case OTHER:
                        default:
                            // システムエラーメッセージをセット
                            builder.setMessage(getString(R.string.error_system));
                            break;
                        }
                    } catch (Exception e) {
                        // ログ出力
                        Log.e(TAG, "onCreateDialog->" + e.getClass().toString());

                        // システムエラーメッセージをセット
                        builder.setMessage(getString(R.string.error_system));
                    }
                    builder.setPositiveButton(R.string.ok, null);

                    return builder.create();
                default:
                    throw new IllegalArgumentException("The ButtonKind argument cannot be applied.");
            }
        }
    }
}
