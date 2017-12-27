package com.subject.piu.chart;

import android.os.AsyncTask;
import android.util.Log;

import com.subject.piu.GettingHTMLError;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.UnknownHostException;

class GettingHTMLsTask extends AsyncTask<String, Void, Document> {
    // デバッグ用のタグ
    private static final String TAG = "GettingHTMLsTask";

    // スマートフォン用WebページをPC用として取得するユーザエージェントの指定
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    /**
     * CommonParams.WIKI_URLSに定義してある、PUMP IT UP (JAPAN) WikiのあるバージョンのURLから、
     * HTMLのドキュメントを取得するバックグラウンド動作を行う
     * @param args PUMP IT UP (JAPAN) WikiのあるバージョンのURL(1つのみ)
     * @return あるバージョンのHTMLのドキュメント、エラーが発生した場合はnull
     */
    @Override
    protected Document doInBackground(String... args) {
        Document doc = null;

        try {
            // あるバージョンのURLからHTMLのドキュメントを取得
            doc = Jsoup.connect(args[0]).userAgent(USER_AGENT).get();
        } catch (UnknownHostException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround->" + e.getClass().toString());

            // オフラインのため通信できない
            Chooser.cause = GettingHTMLError.CONNECTION;
        } catch (HttpStatusException e) {
            // ログ出力
            Log.e(TAG, "doInBackGround->" + e.getClass().toString());

            // URLが誤っているため通信できない
            Chooser.cause = GettingHTMLError.URL;
        } catch (Exception e) {
            // ログ出力
            Log.e(TAG, "doInBackGround->" + e.getClass().toString());

            // システムエラー
            Chooser.cause = GettingHTMLError.OTHER;
        }

        return doc;
    }
}
