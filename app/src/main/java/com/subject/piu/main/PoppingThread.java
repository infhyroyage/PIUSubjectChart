package com.subject.piu.main;

import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.subject.piu.R;
import com.subject.piu.CommonParams;
import com.subject.piu.chart.Chooser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class PoppingThread extends Thread {
    // デバッグ用のタグ
    private static final String TAG = "PoppingThread";

    // MainActivityのインスタンス
    private MainActivity mainActivity;

    /**
     * コンストラクタ
     * @param mainActivity MainActivityのインスタンス
     */
    PoppingThread(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void run() {
        // 現在の日付を取得
        String nowPop = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Calendar.getInstance().getTime());
        // 以前にお題を出した日付の文字列を取得
        String oldPop = mainActivity.sp.getString("oldPop", "----/--/--");

        // ログ出力
        Log.d(TAG, "onclick:nowPop=" + nowPop + ",oldPop=" + oldPop);

        /*
         * 同日に2回以上「今日のお題を出す」のボタンを押したかどうか判断し、
         * 押した場合は既にお題を出した旨のダイアログを出力する
         * 押していない場合は、別スレッドでお題を出す
         */
        if (nowPop.equals(oldPop)) {
            // 既に出したお題の譜面の文字列を取得する
            String subject = mainActivity.sp.getString("subject", "");

            CheckDialogFragment.newInstance(mainActivity, ButtonKind.POP, mainActivity.getString(R.string.pop_result, subject), "share")
                    .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);
        } else {
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // プログレスバーを初期化
                    ((ProgressBar) mainActivity.findViewById(R.id.progressBarPop)).setProgress(0);

                    // すべてのボタンをグレーアウトして押せなくする
                    mainActivity.buttonStep.setEnabled(false);
                    mainActivity.buttonDifficulty.setEnabled(false);
                    mainActivity.buttonType.setEnabled(false);
                    mainActivity.buttonSeries.setEnabled(false);
                    mainActivity.buttonCategory.setEnabled(false);
                    mainActivity.buttonOther.setEnabled(false);
                    mainActivity.buttonPop.setEnabled(false);

                    // 「今日のお題を出す」のボタンを「お待ちください…」に変更する
                    mainActivity.buttonPop.setText(R.string.getting_charts);
                }
            });

            // お題の譜面の文字列を取得する
            String subjectMessage;
            boolean isShared = false;
            try {
                String subject = Chooser.execute(mainActivity);
                if (subject != null) {
                    subjectMessage = mainActivity.getString(R.string.pop_result, subject);
                    isShared = true;

                    // 取得日付と、取得したお題の譜面の文字列を保存する
                    mainActivity.sp.edit()
                            .putString("subject", subject)
                            .putString("oldPop", new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Calendar.getInstance().getTime()))
                            .apply();
                } else {
                    // subjectMessageがnull(=該当する譜面が1つも存在しなかった)場合のメッセージを取得する
                    subjectMessage = mainActivity.getString(R.string.pop_not_found);
                }
            } catch (Exception e) {
                // ログ出力
                Log.e(TAG, "onClick->" + e.getClass().toString());

                switch (Chooser.cause) {
                case CONNECTION:
                    // 通信不可能エラーメッセージをセット
                    subjectMessage = mainActivity.getString(R.string.error_connection);
                    break;
                case INTERRUPT:
                    // 通信遮断メッセージをセット
                    subjectMessage = mainActivity.getString(R.string.error_interrupt);
                    break;
                case URL:
                    // URLエラーメッセージをセット
                    subjectMessage = mainActivity.getString(R.string.error_url);
                    break;
                case OTHER:
                default:
                    // システムエラーメッセージをセット
                    subjectMessage = mainActivity.getString(R.string.error_system);
                    break;
                }
            }
            final String finalSubjectMessage = subjectMessage;
            final String share = (isShared) ? "share" : "none";

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // お題を表示させるダイアログを表示
                    CheckDialogFragment.newInstance(mainActivity, ButtonKind.POP, finalSubjectMessage, share)
                            .show(mainActivity.getSupportFragmentManager(), CommonParams.MAIN_ACTIVITY_DIALOG_FRAGMENT);

                    // 「お待ちください」のボタンを「今日のお題を出す」に変更する
                    mainActivity.buttonPop.setText(R.string.pop);

                    // すべてのボタンをグレーアウトを解除して押せるようにする
                    mainActivity.buttonStep.setEnabled(true);
                    mainActivity.buttonDifficulty.setEnabled(true);
                    mainActivity.buttonType.setEnabled(true);
                    mainActivity.buttonSeries.setEnabled(true);
                    mainActivity.buttonCategory.setEnabled(true);
                    mainActivity.buttonOther.setEnabled(true);
                    mainActivity.buttonPop.setEnabled(true);

                    // 取得日付を最終取得日のテキストビューに指定
                    ((TextView) mainActivity.findViewById(R.id.textViewPop))
                            .setText(mainActivity.getString(R.string.old_pop, mainActivity.sp.getString("oldPop", "----/--/--")));
                }
            });
        }
    }
}
