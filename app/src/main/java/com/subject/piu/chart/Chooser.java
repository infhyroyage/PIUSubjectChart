package com.subject.piu.chart;

import com.subject.piu.CommonParams;
import com.subject.piu.GettingHTMLError;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

public abstract class Chooser {
    // デバッグ用のタグ
    private static final String TAG = "Chooser";

    // スクレイピングを行った譜面リスト
    private static List<UnitChart> chartList = new CopyOnWriteArrayList<>();

    // TODO : デバッグ用の文字列
    private static String debugStr = "";

    /**
     * GettingHTMLsTask.doInBackgroundメソッドでエラーが発生した原因
     * choiceメソッドでIOExceptionがスローされた場合に値が格納される
     */
    public static GettingHTMLError cause;

    /**
     * 今日のお題を出す
     * @return 今日のお題の譜面を表した文字列
     * @throws InterruptedException : GettingHTMLsTask.executeメソッドでエラーが発生した場合
     * @throws ExecutionException : GettingHTMLsTask.executeメソッドでエラーが発生した場合
     * @throws IOException : GettingHTMLsTask.doInBackgroundメソッドでエラーが発生した場合
     */
    public static String run() throws InterruptedException, ExecutionException, IOException {
        // 各シリーズのHTMLドキュメントを取得
        // NOTE : Androidで通信を行うメソッドは別スレッドで行う必要がある
        Document[] docs = new GettingHTMLsTask().execute().get();
        if (docs == null) throw new IOException();

        // TODO : 各シリーズのHTMLドキュメントをスクレイピングして、譜面リストを取得
        for (Document doc : docs) {
            scrapeFromDocument(doc);
        }

        return debugStr;
    }

    /**
     * あるシリーズのHTMLドキュメントからスクレイピングする
     * @param doc : あるシリーズのHTMLドキュメント
     */
    private static void scrapeFromDocument(Document doc) {
        /*
         * 各シリーズのHTMLドキュメントの中にあるh3タグから、種別が
         * 「NORMAL」、「REMIX」、「FULL SONG」、「SHORT CUT」
         * TODO : 、「PRIME2でプレイ可能になったPRIME JE未収録曲」
         * を取得
         */
        for (Element h3 : doc.getElementsByTag("h3")) {
            if (!isCoincidedTypes(h3)) {
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
                 * h3タグなら次の種別となるのでbreakする
                 */
                Element divHeader = divIdx.child(0).child(0);
                if (divHeader.tagName().equals("h4")) {
                    categoryStr = divHeader.text().trim();
                    debugStr += "[" + categoryStr + "]\n";
                }
                if (divHeader.tagName().equals("h3")) break;

                // 同階層のタグに含まれるtableタグをすべて取得
                for (Element table : divIdx.select("table")) {
                    addChartFromTable(table);
                }
            }
            debugStr += "----------\n";
        }
    }

    /**
     * 指定されたh3/h4タグのテキストが、以下のいずれかの文字列と一致するかどうか判別する
     * なお、英文字の大文字と小文字は区別しない
     *  ・NORMAL
     *  ・REMIX
     *  ・FULL SONG
     *  ・SHORT CUT
     *  TODO : ・PRIME2で復活・削除した曲
     *  TODO : ・PRIME2でプレイ可能になったPRIME JE未収録曲
     * @param element : h3/h4タグのインスタンス
     * @return 一致する場合はtrue、そうではない場合はfalse
     */
    private static boolean isCoincidedTypes(Element element) {
        // 「NORMAL」の一致判定
        boolean normal = element.text().trim().equalsIgnoreCase(CommonParams.TYPES[0]);
        // 「REMIX」の一致判定
        boolean remix = element.text().trim().equalsIgnoreCase(CommonParams.TYPES[1]);
        // 「FULL SONG」の一致判定
        boolean fullSong = element.text().trim().equalsIgnoreCase(CommonParams.TYPES[2]);
        // 「SHORT CUT」の一致判定
        boolean shortCut = element.text().trim().equalsIgnoreCase(CommonParams.TYPES[3]);

        return normal || remix || fullSong || shortCut;
    }

    /**
     * あるシリーズのtableタグからスクレイピングし、譜面リストに格納する
     * @param table : tableタグのインスタンス
     */
    private static void addChartFromTable(Element table) {
        // tableタグの中にある全trタグを取得
        Elements trs = table.getElementsByTag("tr");

        /*
         * tableタグの中にある最初のtrタグで、以下の順にtdタグの文字列が格納されているかチェックする
         * アーティスト→BPM→SINGLE→DOUBLE→S-PERF→D-PERF→編集
         * 格納されていない場合は、曲情報以外のtableタグなのでreturnする
         */
        StringBuilder tr1stStr = new StringBuilder();
        for (Element td : trs.first().getElementsByTag("td")) {
            tr1stStr.append(td.text().trim());
        }
        debugStr += tr1stStr.toString() + "\n";
        if (!tr1stStr.toString().equals("アーティストBPMSINGLEDOUBLES-PERFD-PERF編集")) return;

        for (int i = 1; i < trs.size(); i++) {
            // 現在のインデックスの曲情報を取得
            Element tr = trs.get(i);
            Elements tds = tr.getElementsByTag("td");

            // 「曲名」を取得
            String name = tr.child(0).text().trim();

            // 「SINGLE」の通常譜面を取得
            Element tdSingle = tds.get(2);
            debugStr += name + " (" + tdSingle.ownText() + ")";
            // 「SINGLE」の「その他」の譜面を取得
            for (Element span : tdSingle.getElementsByTag("span")) {
                debugStr += "[" + span.text() + ":" + span.attributes().get("style") + "]";
            }

            // 「DOUBLE」を取得
            Element tdDouble = tds.get(3);
            debugStr += "(" + tdDouble.ownText() + ")";
            // 「DOUBLE」の「その他」の譜面を取得
            for (Element span : tdDouble.getElementsByTag("span")) {
                debugStr += "[" + span.text() + ":" + span.attributes().get("style") + "]";
            }

            // 「S-PERF」を取得
            Element tdSPerf = tds.get(4);
            debugStr += "(" + tdSPerf.ownText() + ")";
            // 「S-PERF」の「その他」の譜面を取得
            for (Element span : tdSPerf.getElementsByTag("span")) {
                debugStr += "[" + span.text() + ":" + span.attributes().get("style") + "]";
            }

            // 「D-PERF」を取得
            Element tdDPerf = tds.get(5);
            debugStr += "(" + tdDPerf.ownText() + ")";
            // 「D-PERF」の「その他」の譜面を取得
            for (Element span : tdDPerf.getElementsByTag("span")) {
                debugStr += "[" + span.text() + ":" + span.attributes().get("style") + "]";
            }
            debugStr += "\n";
            //debugStr += name + " (" + tdSingle.html() + ")(" + tdDouble.html() + ")(" + tdSPerf.html() + ")(" + tdDPerf.html() + ")\n";
        }
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private Chooser() {}
}
