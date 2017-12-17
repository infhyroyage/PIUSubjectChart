package com.piusubjectchart.scraping;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ExecutionException;

public abstract class ScrapingChart {
    public static String get() throws InterruptedException, ExecutionException {
        // TODO : デバッグ用の文字列
        String debugStr = "";

        // 各バージョンのHTMLドキュメントを取得
        // NOTE : Androidで通信を行うメソッドは別スレッドで行う必要がある
        for (Document doc : new GettingDocsTask().execute().get()) {
            /*
             * 各バージョンのHTMLドキュメントの中にあるh3タグから、曲種別が
             * 「NORMAL」、「REMIX」、「FULL SONG」、「SHORT CUT」のみ取得
             */
            for (Element h3 : doc.getElementsByTag("h3")) {
                if (!h3.text().trim().equals("NORMAL")
                 && !h3.text().trim().equals("REMIX")
                 && !h3.text().trim().equals("FULL SONG")
                 && !h3.text().trim().equals("SHORT CUT")) {
                    continue;
                }

                // 抽出したカテゴリーのインスタンス
                String categoryStr = "Unknown Category";

                // h3タグの親の親のタグを、現在の基準タグとして取得
                Element divCurrent = h3.parent().parent();
                // 現在の基準タグより先にある、同階層のタグを取得
                for (int idx = divCurrent.elementSiblingIndex() + 1; idx < divCurrent.parent().children().size(); idx++) {
                    Element divIdx = divCurrent.parent().children().get(idx);

                    /*
                     * 同階層のタグの最初の子供の最初の子供のタグを取得し、
                     * h4タグならカテゴリーが変化するのでそれを抽出、
                     * h3タグなら次の曲種別となるのでbreakする
                     */
                    Element divHeader = divIdx.child(0).child(0);
                    if (divHeader.tagName().equals("h4")) {
                        categoryStr = divHeader.text().trim();
                        debugStr += "[" + categoryStr + "]\n";
                    }
                    if (divHeader.tagName().equals("h3")) break;

                    // 同階層のタグに含まれるtableタグをすべて取得
                    for (Element table : divIdx.select("table")) {
                        // tableタグの中にある全trタグを取得
                        Elements trs = table.getElementsByTag("tr");

                        /*
                         * tableタグの中にある最初のtrタグで、以下の順にtdタグの文字列が格納されているかチェックする
                         * アーティスト→BPM→SINGLE→DOUBLE→S-PERF→D-PERF→編集
                         * 格納されていない場合は、曲情報以外のtableタグなのでスキップする
                         */
                        StringBuilder tr1stStr = new StringBuilder();
                        for (Element td : trs.first().getElementsByTag("td")) {
                            tr1stStr.append(td.text().trim());
                        }
                        if (!tr1stStr.toString().equals("アーティストBPMSINGLEDOUBLES-PERFD-PERF編集")) continue;

                        for (int i = 1; i < trs.size(); i++) {
                            // 現在のインデックスの曲情報を取得
                            Element tr = trs.get(i);
                            Elements tds = tr.getElementsByTag("td");

                            // 「曲名」を取得
                            String name = tr.child(0).text().trim();

                            // 「SINGLE」を取得
                            Element tdSingle = tds.get(2);
                            // 「DOUBLE」を取得
                            Element tdDouble = tds.get(3);
                            // 「S-PERF」を取得
                            Element tdSPerf = tds.get(4);
                            // 「D-PERF」を取得
                            Element tdDPerf = tds.get(5);
                            debugStr += name + " (" + tdSingle.html() + ")(" + tdDouble.html() + ")(" + tdSPerf.html() + ")(" + tdDPerf.html() + ")\n";
                        }
                    }
                }
                debugStr += "----------\n";
            }
        }

        return debugStr;
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private ScrapingChart() {}
}
