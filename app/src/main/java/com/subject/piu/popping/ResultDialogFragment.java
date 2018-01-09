package com.subject.piu.popping;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;

import com.subject.piu.R;
import com.subject.piu.main.MainActivity;

/**
 * お題を出した後の結果を表示するダイアログのクラス
 */
public class ResultDialogFragment extends AppCompatDialogFragment {
    // 呼びだされたMainActivityのインスタンス
    private MainActivity mainActivity;

    // このダイアログのメッセージ
    private String message;

    // このダイアログに「共有」ボタンを表示させるかどうかのフラグ
    private boolean isShared;

    /**
     * このクラスのインスタンスの初期化を行い、それを返す
     * @param mainActivity MainActivityのインスタンス
     * @param message このダイアログのメッセージ
     * @param isShared このダイアログに「共有」ボタンを表示させるかどうかのフラグ
     * @return このクラスのインスタンス
     */
    public static ResultDialogFragment newInstance(MainActivity mainActivity, String message, boolean isShared) {
        ResultDialogFragment thisFragment = new ResultDialogFragment();
        thisFragment.mainActivity = mainActivity;
        thisFragment.message = message;
        thisFragment.isShared = isShared;

        return thisFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        // ダイアログのビルダーを生成
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity)
                .setMessage(message)
                .setPositiveButton(R.string.ok, null);

        // 「共有」ボタンをビルダーにセット
        if (isShared) {
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
    }
}
