package com.subject.piu.chart;

import android.util.Log;
import android.widget.ProgressBar;

import com.subject.piu.R;
import com.subject.piu.CommonParams;
import com.subject.piu.main.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ConnectingChooser {
    // デバッグ用のタグ
    private static final String TAG = "ConnectingChooser";

    // スマートフォン用WebページをPC用として取得するユーザエージェントの指定
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    /**
     * 今日のお題を出す
     * @param mainActivity メインアクティビティのインスタンス
     * @return 今日のお題の譜面を表した文字列、1日中に2回以上お題を出した場合はnull
     * @throws IOException 通信時にエラーが発生した場合
     */
    public static String execute(MainActivity mainActivity) throws IOException {
        // スクレイピングを行った譜面リストを生成
        List<UnitChart> chartList = new CopyOnWriteArrayList<>();

        // 「シリーズ」のチェック状態に応じて、各シリーズのURLを取得
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
            if (CommonParams.seriesChecks[i]) {
                urlList.addAll(Arrays.asList(CommonParams.SERIES_URL[i]));
            }
        }

        // プログレスバーを取得し、最大値を設定
        final ProgressBar progressBarRun = mainActivity.findViewById(R.id.progressBarPop);
        progressBarRun.setMax(urlList.size());

        for (int i = 0; i < urlList.size(); i++) {
            String url = urlList.get(i);

            // ログ出力
            Log.d(TAG, "execute:start->connect,url=" + url);

            // あるシリーズのURLから、そのシリーズのHTMLドキュメントを取得
            Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

            // ログ出力
            Log.d(TAG, "execute:connect->scrape,url=" + url);

            // HTMLドキュメントからh3タグをスクレイピングして取得した譜面サブリストを譜面リストに格納
            chartList.addAll(Scraper.execute(doc));

            // ログ出力
            Log.d(TAG, "execute:scrape->end,url=" + url);

            // プログレスバーの進捗を1段階上げる(UIスレッドで実行)
            final int idx = i;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarRun.setProgress(idx + 1);
                }
            });
        }

        // chartListが空だった(=該当する譜面が1つも存在しなかった)場合、nullを返す
        if (chartList.size() == 0) {
            return null;
        }

        // スクレイピングを行った譜面リストから、ランダムに1つの譜面を選ぶ
        UnitChart uc = chartList.get(new Random().nextInt(chartList.size()));

        return uc.toString();
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private ConnectingChooser() {}
}
