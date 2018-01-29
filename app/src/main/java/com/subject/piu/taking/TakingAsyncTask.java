package com.subject.piu.taking;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.subject.piu.R;
import com.subject.piu.CommonParams;
import com.subject.piu.chart.ChartChooser;
import com.subject.piu.main.MainActivity;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

/**
 * バックグラウンド動作でお題を出す動作を実行する非同期処理クラス
 */
public class TakingAsyncTask extends AsyncTask<Void, Void, TakingAsyncTask.Result> implements View.OnClickListener {
    // デバッグ用のタグ
    private static final String TAG = "TakingAsyncTask";

    // AsyncTask内のContextの弱参照性を考慮したMainActivityのインスタンス
    private WeakReference<MainActivity> weakReference;

    public TakingAsyncTask(MainActivity mainActivity) {
        this.weakReference = new WeakReference<>(mainActivity);
    }

    /**
     * doInBackgroundメソッドでの動作を実行する前にUIスレッドで実行する
     */
    @Override
    protected void onPreExecute() {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();

        // 「お待ちください」のボタンと、破棄されていないSwitchをすべて無効にして押せなくする
        mainActivity.mainButtonTaking.setEnabled(false);
        for (Switch s : mainActivity.createdSwitches) {
            s.setEnabled(false);
        }

        // 「今日のお題を出す」のボタンを「お待ちください…」に変更し、そのフラグも変更する
        mainActivity.mainButtonTaking.setText(R.string.getting_charts);
        mainActivity.isWaited.set(true);

        // プログレスバーを初期化
        ((ProgressBar) mainActivity.findViewById(R.id.progressBarTaking)).setProgress(0);
    }

    /**
     * バックグラウンド動作でお題を出す
     * @param args 未使用
     * @return お題を出したリザルトのインスタンス
     */
    @Override
    protected Result doInBackground(Void... args) {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();

        // お題の譜面の文字列
        String message;
        // 「共有」ボタンを表示するかどうかのフラグ
        boolean isShared = false;

        // お題の譜面の文字列を取得する
        try {
            message = ChartChooser.execute(mainActivity);
            if (message != null && !message.equals(mainActivity.getString(R.string.error_not_found))) {
                isShared = true;
            }
        } catch (UnknownHostException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround:UnknownHostException");

            // オフラインのため通信できない旨のメッセージをセット
            message = mainActivity.getString(R.string.error_connection);
        } catch (ConnectException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround:ConnectException");

            // 途中で通信が遮断された旨のメッセージをセット
            message = mainActivity.getString(R.string.error_interrupt);
        } catch (IOException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround:IOException");

            // システムエラーなので、非チェック例外を再スロー
            throw new IllegalStateException("doInBackGround:IOException,msg=" + e.getMessage(), e);
        }

        return new Result(message, isShared);
    }

    /**
     * doInBackgroundメソッドでの動作を実行した後にUIスレッドで実行する
     * @param result お題を出したリザルトのインスタンス
     */
    @Override
    protected void onPostExecute(Result result) {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();
        // MainActivityのSharedPreferenceインスタンスを取得
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        // お題を表示させるダイアログを表示
        ResultDialogFragment.newInstance(mainActivity, result.message, result.isShared)
                .show(mainActivity.getSupportFragmentManager(), CommonParams.DIALOG_FRAGMENT_MAIN);

        // 「お待ちください」のボタンを「今日のお題を出す」に変更し、そのフラグも変更する
        mainActivity.isWaited.set(false);
        mainActivity.mainButtonTaking.setText(R.string.take);

        // 「お待ちください」のボタンと、破棄されていないSwitchをすべて有効にして押せるようにする
        for (Switch s : mainActivity.createdSwitches) {
            s.setEnabled(true);
        }
        mainActivity.mainButtonTaking.setEnabled(true);

        // 取得日付を最終取得日のテキストビューに指定
        ((TextView) mainActivity.findViewById(R.id.mainTextTaking))
                .setText(mainActivity.getString(R.string.last_taking_date, sp.getString("lastTakingDate", "----/--/--")));
    }

    /**
     * 「今日のお題を出す」のボタンをクリックした時の動作
     * @param view 「今日のお題を出す」のボタンのビュー
     */
    @Override
    public void onClick(View view) {
        // MainActivityを取得
        MainActivity mainActivity = weakReference.get();
        // MainActivityのSharedPreferenceインスタンスを取得
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        // 現在の日付と、以前にお題を出した日付の文字列を取得
        String nowTakingDate = new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Calendar.getInstance().getTime());
        String lastTakingDate = sp.getString("lastTakingDate", "----/--/--");

        if (nowTakingDate.equals(lastTakingDate)) {
            /*
             * 同日に2回以上「今日のお題を出す」のボタンを押した場合は、
             * その場合は既に出したお題の文字列を取得してダイアログを出力する
             */
            String subject = sp.getString("subject", "error"), type = sp.getString("type", "error");
            if (subject.equals("error")) {
                throw new IllegalStateException("Subject chart has already taken, but cannot be gotten.");
            } else if (type.equals("error")) {
                throw new IllegalStateException("Subject chart of type has already taken, but cannot be gotten.");
            }

            String message;
            if (type.equals("")) {
                message = mainActivity.getString(R.string.result, subject);
            } else {
                message = mainActivity.getString(R.string.result_type, subject, type);
            }

            ResultDialogFragment.newInstance(mainActivity, message, true)
                    .show(mainActivity.getSupportFragmentManager(), CommonParams.DIALOG_FRAGMENT_MAIN);
        } else if (Arrays.equals(CommonParams.singleChecks, new boolean[CommonParams.singleChecks.length]) && Arrays.equals(CommonParams.doubleChecks, new boolean[CommonParams.doubleChecks.length]) && !CommonParams.coopCheck) {
            // 「難易度」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
            ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, mainActivity.getString(R.string.difficulty)), false)
                    .show(mainActivity.getSupportFragmentManager(), CommonParams.DIALOG_FRAGMENT_MAIN);
        } else if (Arrays.equals(CommonParams.typeChecks, new boolean[CommonParams.typeChecks.length])) {
            // 「タイプ」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
            ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, mainActivity.getString(R.string.type)), false)
                    .show(mainActivity.getSupportFragmentManager(), CommonParams.DIALOG_FRAGMENT_MAIN);
        } else if (Arrays.equals(CommonParams.seriesChecks, new boolean[CommonParams.seriesChecks.length])) {
            // 「シリーズ」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
            ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, mainActivity.getString(R.string.series)), false)
                    .show(mainActivity.getSupportFragmentManager(), CommonParams.DIALOG_FRAGMENT_MAIN);
        } else if (Arrays.equals(CommonParams.categoryChecks, new boolean[CommonParams.categoryChecks.length])) {
            // 「カテゴリー」タブのチェック状態がすべてOFFだった場合は、お題を出せない旨のダイアログを出力
            ResultDialogFragment.newInstance(mainActivity, mainActivity.getString(R.string.error_all_off, mainActivity.getString(R.string.category)), false)
                    .show(mainActivity.getSupportFragmentManager(), CommonParams.DIALOG_FRAGMENT_MAIN);
        } else {
            // 上記以外の場合は、別スレッドでお題を出す
            execute();
        }
    }

    /**
     * お題を出した後の結果を表すクラス
     */
    class Result {
        // ダイアログのメッセージ
        String message;
        // 「共有」ボタンを表示させるかどうかのフラグ
        boolean isShared;

        /**
         * コンストラクタ
         * @param message ダイアログのメッセージ
         * @param isShared 「共有」ボタンを表示させるかどうかのフラグ
         */
        Result(String message, boolean isShared) {
            this.message = message;
            this.isShared = isShared;
        }
    }
}
