package com.piusubjectchart.chart;

import android.os.AsyncTask;
import android.util.Log;

import com.piusubjectchart.CommonParams;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.UnknownHostException;

// TODO : 第1型パラメーターには、「バージョン」で選択した状態を表す型名にする
class GettingHTMLsTask extends AsyncTask<Void, Void, Document[]> {
    // デバッグ用のタグ
    private static final String TAG = "GettingHTMLsTask";

    // スマートフォン用WebページをPC用として取得するユーザエージェントの指定
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    /**
     * CommonParams.WIKI_URLSに定義してある、PUMP IT UP (JAPAN) Wikiの各バージョンのURLから、
     * 各HTMLのドキュメントを取得するバックグラウンド動作を行う
     * @return HTMLのドキュメント配列、エラーが発生した場合はnull
     */
    @Override
    protected Document[] doInBackground(Void... args) {
        Document[] docs = new Document[CommonParams.WIKI_URLS.length];

        for (int i = 0; i < CommonParams.WIKI_URLS.length; i++) {
            try {
                // 各バージョンのURLからHTMLのドキュメントを取得
                docs[i] = Jsoup.connect(CommonParams.WIKI_URLS[i]).userAgent(USER_AGENT).get();
            } catch (UnknownHostException e) {
                // オフラインのため通信できない
                SubjectChart.cause = GettingHTMLError.CONNECTION;
                return null;
            } catch (HttpStatusException e) {
                // URLが誤っているため通信できない
                SubjectChart.cause = GettingHTMLError.URL;
                return null;
            } catch (IOException e) {
                // ログ出力
                Log.e(TAG, "doInBackGround->" + e.getClass().toString() + ":i=" + i);

                // システムエラー
                SubjectChart.cause = GettingHTMLError.OTHER;
                return null;
            }
        }

        return docs;
    }
}
