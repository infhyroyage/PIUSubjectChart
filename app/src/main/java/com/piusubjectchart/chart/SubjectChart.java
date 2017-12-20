package com.piusubjectchart.chart;

import com.piusubjectchart.CommonParams;
import com.piusubjectchart.GettingHTMLError;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class SubjectChart {
    // デバッグ用のタグ
    private static final String TAG = "SubjectChart";

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
    public static String choice() throws InterruptedException, ExecutionException, IOException {
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
     * あるシリーズのHTMLドキュメントから、そのシリーズの譜面リストを生成する
     * @param doc : あるシリーズのHTMLドキュメント
     * @return 上記シリーズの譜面リスト
     */
    private static List<UnitChart> scrapeFromDocument(Document doc) {
        // 譜面リストを生成
        List<UnitChart> chartList = new ArrayList<>();

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

        return chartList;
    }

    /**
     * 指定されたh3/h4タグのテキストが、以下のいずれかの文字列と一致するかどうか判別する
     * なお、英文字の大文字と小文字は区別しない
     *  ・NORMAL
     *  ・REMIX
     *  ・FULL SONG
     *  ・SHORT CUT
     * TODO : ・PRIME2でプレイ可能になったPRIME JE未収録曲
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

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private SubjectChart() {}
}
