package com.piusubjectchart.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.concurrent.ExecutionException;

public abstract class ScrapingChart {
    public static String get() throws InterruptedException, ExecutionException {
        // NOTE : Androidで通信を行うJsoup.connectメソッドは別スレッドで行う必要がある
        Document[] docs = new ScrapingAsyncTask().execute().get();

        // TODO : PRIME2のみtableタグをチェック
        Elements elements = docs[0].getElementsByTag("table");
        return elements.toString();
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private ScrapingChart() {}
}
