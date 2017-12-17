package com.piusubjectchart.scraping;

import android.os.AsyncTask;
import android.util.Log;

import com.piusubjectchart.CommonParams;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

// TODO : 第1型パラメーターには、「バージョン」で選択した状態を表す型名にする
class GettingDocsTask extends AsyncTask<Void, Void, Document[]> {
    // デバッグ用のタグ
    private static final String TAG = "GettingDocsTask";

    // スマートフォン用WebページをPC用として取得するユーザエージェントの指定
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    /**
     * CommonParams.WIKI_URLSに定義してある、PUMP IT UP (JAPAN) Wikiの各バージョンのURLから、
     * 各HTMLのドキュメントを取得するバックグラウンド動作を行う
     * @return HTMLのドキュメント配列、null時は各バージョンのURLのいずれかが誤っている
     */
    @Override
    protected Document[] doInBackground(Void... args) {
        Document[] docs = new Document[CommonParams.WIKI_URLS.length];

        for (int i = 0; i < CommonParams.WIKI_URLS.length; i++) {
            try {
                // 各バージョンのURLからHTMLのドキュメントを取得
                docs[i] = Jsoup.connect(CommonParams.WIKI_URLS[i]).userAgent(USER_AGENT).get();
            } catch (IOException e) {
                // ログ出力
                Log.e(TAG, "doInBackGround->IOException:i=" + i);

                // URLが誤っているので、新規HTMLを取得できない
                return null;
            }
        }

        return docs;
    }
}