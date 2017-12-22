package com.subject.piu.chart;

import com.subject.piu.CommonParams;
import com.subject.piu.GettingHTMLError;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
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
     * @throws InterruptedException GettingHTMLsTask.executeメソッドでエラーが発生した場合
     * @throws ExecutionException GettingHTMLsTask.executeメソッドでエラーが発生した場合
     * @throws IOException GettingHTMLsTask.doInBackgroundメソッドでエラーが発生した場合
     */
    public static String run() throws InterruptedException, ExecutionException, IOException {
        // 各シリーズのHTMLドキュメントを取得
        // NOTE : Androidで通信を行うメソッドは別スレッドで行う必要がある
        Document[] docs = new GettingHTMLsTask().execute().get();
        if (docs == null) throw new IOException();

        // TODO : 各シリーズのHTMLドキュメントからh3タグをスクレイピングして取得した譜面サブリストを譜面リストに格納
        for (Document doc : docs) {
            chartList.addAll(scrapeH3FromDocument(doc));
        }

        return debugStr;
    }

    /**
     * 指定されたあるシリーズのHTMLドキュメントから、h3タグをスクレイピングする
     * @param doc あるシリーズのHTMLドキュメント
     * @return あるシリーズの譜面サブリスト
     */
    private static List<UnitChart> scrapeH3FromDocument(Document doc) {
        // 指定されたあるシリーズのHTMLドキュメントに含まれる譜面サブリストのインスタンス
        List<UnitChart> chartSubList = new ArrayList<>();

        /*
         * 各シリーズのHTMLドキュメントの中にあるh3タグから、種別が
         * 「NORMAL」、「REMIX」、「FULL SONG」、「SHORT CUT」
         * TODO : 、「PRIME2で復活・削除した曲」
         * TODO : 、「PRIME2でプレイ可能になったPRIME JE未収録曲」
         * を取得し、そうではない場合はスキップ
         */
        for (Element h3 : doc.getElementsByTag("h3")) {
            if (!isCoincidedTypes(h3)) {
            //if (!isSkipped(h3)) { // TODO
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

                // 同階層のタグに含まれるtableタグをすべて取得し、譜面サブリストに追加
                for (Element table : divIdx.select("table")) {
                    chartSubList.addAll(scrapeTdFromTable(table));
                }
            }
            debugStr += "----------\n";
        }

        return chartSubList;
    }

    /**
     * 指定されたh3/h4タグのテキストが、以下のいずれかの文字列と一致するかどうか判別する
     * なお、英文字の大文字と小文字は区別しない
     *  ・NORMAL
     *  ・REMIX
     *  ・FULL SONG
     *  ・SHORT CUT
     *  ・PRIME2で復活・削除した曲
     *  ・PRIME2でプレイ可能になったPRIME JE未収録曲
     * @param element h3/h4タグのインスタンス
     * @return 一致する場合はtrue、そうではない場合はfalse
     */
    private static boolean isSkipped(Element element) {
        // 「NORMAL」、「REMIX」、「FULL SONG」、「SHORT CUT」の一致判定
        boolean types = isCoincidedTypes(element);
        // 「PRIME2で復活・削除した曲」の一致判定
        boolean revival = element.text().trim().equals("PRIME2で復活・削除した曲");
        // 「PRIME2でプレイ可能になったPRIME JE未収録曲」の一致判定
        boolean capableFromJE = element.text().trim().equals("PRIME2でプレイ可能になったPRIME JE未収録曲");

        return types || revival || capableFromJE;
    }

    /**
     * 指定されたh3/h4タグのテキストが、以下のいずれかの文字列と一致するかどうか判別する
     * なお、英文字の大文字と小文字は区別しない
     *  ・NORMAL
     *  ・REMIX
     *  ・FULL SONG
     *  ・SHORT CUT
     * @param element h3/h4タグのインスタンス
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
     * 指定されたtableタグから、tdタグをスクレイピングする
     * @param table tableタグのインスタンス
     * @return tableタグに含まれる譜面サブリスト
     */
    private static List<UnitChart> scrapeTdFromTable(Element table) {
        // 指定されたtableタグに含まれる譜面サブリストのインスタンス
        List<UnitChart> chartSubList = new ArrayList<>();

        // tableタグの中にある全trタグを取得
        Elements trs = table.getElementsByTag("tr");

        /*
         * tableタグの中にある最初のtrタグで、以下の順にtdタグの文字列が格納されているかチェックする
         * アーティスト→BPM→SINGLE→DOUBLE→S-PERF→D-PERF→編集
         * 格納されていない場合は曲情報以外のtableタグなので、空の譜面サブリストを返す
         */
        StringBuilder tr1stStr = new StringBuilder();
        for (Element td : trs.first().getElementsByTag("td")) {
            tr1stStr.append(td.text().trim());
        }
        if (!tr1stStr.toString().equals("アーティストBPMSINGLEDOUBLES-PERFD-PERF編集")) {
            return chartSubList;
        }

        for (int i = 1; i < trs.size(); i++) {
            // 現在のインデックスの曲情報を取得
            Element tr = trs.get(i);
            Elements tds = tr.getElementsByTag("td");

            // 「曲名」を取得
            String name = tr.child(0).text().trim();
            debugStr += name + " ";

            // 「SINGLE」を取得し、空文字でなければ譜面サブリストに追加
            Element tdSingle = tds.get(2);
            if (!tdSingle.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdSingle, name));
            }

            // 「DOUBLE」を取得し、空文字でなければ譜面サブリストに追加
            Element tdDouble = tds.get(3);
            if (!tdDouble.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdDouble, name));
            }

            // 「S-PERF」を取得し、空文字でなければ譜面サブリストに追加
            Element tdSPerf = tds.get(4);
            if (!tdSPerf.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdSPerf, name));
            }

            // 「D-PERF」を取得し、空文字でなければ譜面サブリストに追加
            Element tdDPerf = tds.get(5);
            if (!tdDPerf.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdDPerf, name));
            }

            debugStr += "\n";
        }

        return chartSubList;
    }

    /**
     * 指定されたtdタグから、tdタグに含まれるをスクレイピングする
     * @param td tdタグのインスタンス
     * @param name tdタグのインスタンス
     * @return tdタグに含まれる譜面サブリスト
     */
    private static List<UnitChart> scrapeChartFromTd(Element td, String name) {
        // 指定されたtdタグに含まれる譜面サブリストのインスタンス
        List<UnitChart> chartSubList = new ArrayList<>();

        // tdタグ中の「その他」に該当しない譜面を表した文字列を取得
        String chartsStr = td.ownText();
        // TODO : "4 / 7 / 9 / 16 / 18/"、"7 / 14 //"、"10 / 16"などから数字のみ取得
        debugStr += "(" + chartsStr + ")";

        // tdタグ中の「その他」に該当する譜面をすべて取得
        for (Element span : td.getElementsByTag("span")) {
            // TODO : "[20_color:#ffaa00;]" : 20レベルのPP解禁譜面
            // TODO : "[20_color:#0075c8;]" : 20レベルのAM.PASS専用譜面
            // TODO : "[20_color:#009e25;]" : 20レベルのAM.PASS専用譜面
            // TODO 上記以外は譜面サブリストに追加しない
            debugStr += "[" + span.text() + "_" + span.attributes().get("style") + "]";
        }

        return chartSubList;
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private Chooser() {}
}
