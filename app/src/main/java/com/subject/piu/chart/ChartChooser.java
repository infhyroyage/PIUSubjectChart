package com.subject.piu.chart;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ProgressBar;

import com.subject.piu.R;
import com.subject.piu.CommonParams;
import com.subject.piu.main.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * お題の譜面を1つ選択する動作を行う抽象staticクラス
 */
public abstract class ChartChooser {
    // デバッグ用のタグ
    private static final String TAG = "ChartChooser";

    // スマートフォン用WebページをPC用として取得するユーザエージェントの指定
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36";

    /**
     * お題の譜面を1つ選択し、そのメッセージの文字列を作成する
     * @param mainActivity メインアクティビティのインスタンス
     * @return お題の譜面を表したメッセージの文字列
     * @throws IOException 通信時にエラーが発生した場合
     */
    public static String execute(final MainActivity mainActivity) throws IOException {
        // スクレイピングを行った譜面リストを生成
        final List<UnitChart> chartList = new CopyOnWriteArrayList<>();

        // 「シリーズ」のチェック状態に応じて、各シリーズのURLを取得
        List<String> urlList = new ArrayList<>();
        for (int i = 0; i < CommonParams.seriesChecks.length; i++) {
            if (CommonParams.seriesChecks[i]) {
                urlList.addAll(Arrays.asList(CommonParams.SERIES_URL[i]));
            }
        }

        // プログレスバーを取得し、最大値を設定
        final ProgressBar progressBarRun = mainActivity.findViewById(R.id.progressBarTaking);
        progressBarRun.setMax(urlList.size());

        // スレッドプールの生成
        ExecutorService service = Executors.newFixedThreadPool(urlList.size());

        // 各シリーズの譜面サブリストを譜面リストに格納する複数の動作を並列で実行する
        for (int i = 0; i < urlList.size(); i++) {
            final String url = urlList.get(i);
            service.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    // ログ出力
                    Log.d(TAG, "call:start->connect,url=" + url);

                    // あるシリーズのURLから、そのシリーズのHTMLドキュメントを取得
                    Document doc = Jsoup.connect(url).userAgent(USER_AGENT).get();

                    // ログ出力
                    Log.d(TAG, "call:connect->scrape,url=" + url);

                    // HTMLドキュメントからh3タグをスクレイピングして取得した譜面サブリストを譜面リストに格納
                    chartList.addAll(DocumentScraper.execute(doc));

                    // ログ出力
                    Log.d(TAG, "call:scrape->end,url=" + url);

                    // プログレスバーの進捗を1段階上げる(UIスレッドで実行)
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBarRun.setProgress(progressBarRun.getProgress() + 1);
                        }
                    });

                    return null;
                }
            });
        }
        // 新規動作の受付を終了する
        service.shutdown();
        // 上記の動作がすべて終わるまで待機する
        while (true) {
            if (service.isTerminated()) {
                break;
            }
        }

        // 譜面リストが空だった(=該当する譜面が1つも存在しなかった)場合、nullを返す
        if (chartList.size() == 0) {
            // 該当する譜面が1つも存在しなかった(=messageがnullの)場合のメッセージを取得する
            return mainActivity.getString(R.string.error_not_found);
        }

        // MainActivityのSharedPreferenceインスタンスを取得
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mainActivity);

        // スクレイピングを行った譜面リストから、ランダムに1つの譜面を選ぶ
        UnitChart uc = chartList.get(new Random().nextInt(chartList.size()));

        // 選んだ譜面とその種別を取得から、メッセージを作成する
        String message, subject = uc.toString();
        if (uc.type.equals("")) {
            message = mainActivity.getString(R.string.result, subject);
        } else {
            message = mainActivity.getString(R.string.result_type, subject, uc.type);
        }

        // 選んだお題の譜面、その種別、選んだ日付を保存する
        sp.edit()
                .putString("subject", subject)
                .putString("type", uc.type)
                .putString("lastTakingDate", new SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Calendar.getInstance().getTime()))
                .apply();

        return message;
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private ChartChooser() {}
}
