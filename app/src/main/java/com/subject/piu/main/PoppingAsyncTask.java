package com.subject.piu.main;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.subject.piu.R;
import com.subject.piu.CommonParams;
import com.subject.piu.chart.ConnectingChooser;

import org.jsoup.HttpStatusException;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class PoppingAsyncTask extends AsyncTask<Void, Void, PoppingAsyncTask.PoppingResult> {
    // デバッグ用のタグ
    private static final String TAG = "PoppingAsyncTask";

    // AsyncTask内のContextの弱参照性を考慮したMainActivityのインスタンス
    private WeakReference<MainActivity> weakReference;

    PoppingAsyncTask(MainActivity mainActivity) {
        this.weakReference = new WeakReference<>(mainActivity);
    }

    /**
     * doInBackgroundメソッドでの動作を実行する前にUIスレッドで実行する
     */
    @Override
    protected void onPreExecute() {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();

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

    /**
     * バックグラウンド動作でお題を出す
     * @param args 未使用
     * @return お題を出したリザルトのインスタンス
     */
    @Override
    protected PoppingAsyncTask.PoppingResult doInBackground(Void... args) {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();

        // お題の譜面の文字列
        String message;
        // 「共有」ボタンを表示するかどうかのフラグ
        boolean isShared = false;

        // お題の譜面の文字列を取得する
        try {
            String subject = ConnectingChooser.execute(mainActivity);
            if (subject != null) {
                message = mainActivity.getString(R.string.pop_result, subject);
                isShared = true;

                // 取得日付と、取得したお題の譜面の文字列を保存する
                mainActivity.sp.edit()
                        .putString("subject", subject)
                        .putString("oldPop", new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Calendar.getInstance().getTime()))
                        .apply();
            } else {
                // 該当する譜面が1つも存在しなかった(=messageがnullの)場合のメッセージを取得する
                message = mainActivity.getString(R.string.pop_not_found);
            }
        } catch (UnknownHostException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround->" + e.getClass().toString());

            // オフラインのため通信できない旨のメッセージをセット
            message = mainActivity.getString(R.string.error_connection);
        } catch (ConnectException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround->" + e.getClass().toString());

            // 途中で通信が遮断された旨のメッセージをセット
            message = mainActivity.getString(R.string.error_interrupt);
        } catch (HttpStatusException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround->" + e.getClass().toString());

            // URLが誤っているため通信できない旨のメッセージをセット
            message = mainActivity.getString(R.string.error_url);
        } catch (Exception e) {
            // ログ出力
            Log.e(TAG, "onClick->" + e.getClass().toString());

            // システムエラーメッセージをセット
            message = mainActivity.getString(R.string.error_system);
        }

        return new PoppingResult(message, isShared);
    }

    /**
     * doInBackgroundメソッドでの動作を実行した後にUIスレッドで実行する
     * @param result お題を出したリザルトのインスタンス
     */
    @Override
    protected void onPostExecute(PoppingAsyncTask.PoppingResult result) {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();

        // 「共有」ボタンを表示する場合は"share"、しない場合は"none"にセット
        String share = (result.isShared) ? "share" : "none";

        // お題を表示させるダイアログを表示
        CheckDialogFragment.newInstance(mainActivity, ButtonKind.POP, result.message, share)
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

    /**
     * お題を出した後の結果を表すクラス
     */
    class PoppingResult {
        // ダイアログのメッセージ
        String message;
        // 「共有」ボタンを表示させるかどうかのフラグ
        boolean isShared;

        /**
         * コンストラクタ
         * @param message ダイアログのメッセージ
         * @param isShared 「共有」ボタンを表示させるかどうかのフラグ
         */
        PoppingResult(String message, boolean isShared) {
            this.message = message;
            this.isShared = isShared;
        }
    }
}
