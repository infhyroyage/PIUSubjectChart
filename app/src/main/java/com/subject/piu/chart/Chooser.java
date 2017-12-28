package com.subject.piu.chart;

import android.util.Log;
import android.widget.ProgressBar;

import com.subject.R;
import com.subject.piu.CommonParams;
import com.subject.piu.GettingHTMLError;
import com.subject.piu.main.MainActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public abstract class Chooser {
    // デバッグ用のタグ
    private static final String TAG = "Chooser";

    /**
     * GettingHTMLTask.doInBackgroundメソッドでエラーが発生した原因
     * choiceメソッドでIOExceptionがスローされた場合に値が格納される
     */
    public static GettingHTMLError cause;

    /**
     * 今日のお題を出す
     * @param mainActivity メインアクティビティのインスタンス
     * @return 今日のお題の譜面を表した文字列
     * @throws InterruptedException スレッドの割込みが発生した場合
     * @throws ExecutionException GettingHTMLTask.doInBackgroundメソッドで例外がスローされた場合
     * @throws IOException GettingHTMLTask.doInBackgroundメソッドでエラーが発生した場合
     */
    public static String run(MainActivity mainActivity) throws InterruptedException, ExecutionException, IOException {
        // スクレイピングを行った譜面リストを生成
        List<UnitChart> chartList = new CopyOnWriteArrayList<>();

        // 「シリーズ」のチェック状態に応じて、各シリーズのURLを取得
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < CommonParams.series.length; i++) {
            if (CommonParams.series[i]) {
                urlList.addAll(Arrays.asList(CommonParams.SERIES_URL[i]));
            }
        }

        // プログレスバーを取得し、最大値を設定
        final ProgressBar progressBarRun = mainActivity.findViewById(R.id.progressBarRun);
        progressBarRun.setMax(urlList.size());

        for (int i = 0; i < urlList.size(); i++) {
            String url = urlList.get(i);

            // ログ出力
            Log.d(TAG, "run:start->execute,url=" + url);

            // TODO : あるシリーズのURLから、そのシリーズのHTMLドキュメントを取得
            //Document doc = new GettingHTMLTask().execute(url).get();
            //if (doc == null) throw new IOException();
            Thread.sleep(500);
            chartList.add(new UnitChart(String.valueOf(i)));

            // ログ出力
            Log.d(TAG, "run:execute->scrape,url=" + url);

            // TODO : HTMLドキュメントからh3タグをスクレイピングして取得した譜面サブリストを譜面リストに格納
            //chartList.addAll(Scraper.scrapeH3FromDocument(doc));

            // ログ出力
            Log.d(TAG, "run:scrape->end,url=" + url);

            // プログレスバーの進捗を1段階上げる(UIスレッドで実行)
            final int idx = i;
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarRun.setProgress(idx + 1);
                }
            });
        }

        // スクレイピングを行った譜面リストから、ランダムに1つの譜面を選ぶ
        UnitChart uc = chartList.get(new Random().nextInt(chartList.size()));

        return uc.toString();
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private Chooser() {}
}
