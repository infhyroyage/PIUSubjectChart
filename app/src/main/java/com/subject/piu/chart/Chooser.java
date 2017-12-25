package com.subject.piu.chart;

import android.util.Log;

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
        // スクレイピングを行った譜面リストを生成
        List<UnitChart> chartList = new CopyOnWriteArrayList<>();

        // 各シリーズのHTMLドキュメントを取得
        // NOTE : Androidで通信を行うメソッドは別スレッドで行う必要がある
        Document[] docs = new GettingHTMLsTask().execute().get();
        if (docs == null) throw new IOException();

        // TODO : 各シリーズのHTMLドキュメントからh3タグをスクレイピングして取得した譜面サブリストを譜面リストに格納
        for (Document doc : docs) {
            chartList.addAll(scrapeH3FromDocument(doc));
        }

        return "hoge";
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
         * 各シリーズのHTMLドキュメントの中にあるh3タグから種別を取得し、以下と異なる場合はスキップする
         *  ・NORMAL
         *  ・REMIX
         *  ・FULL SONG
         *  ・SHORT CUT
         *  ・TODO : PRIME2で復活・削除した曲
         *  ・TODO : PRIME2でプレイ可能になったPRIME JE未収録曲
         */
        for (Element h3 : doc.getElementsByTag("h3")) {
            String type = h3.text().trim();
            if (isSkippedByType(type)) continue;

            // ログ出力
            Log.d(TAG, "scrapeH3FromDocument:type=" + type);

            // h3タグの親の親のタグを、現在の基準タグとして取得
            Element divCurrent = h3.parent().parent();
            // 現在の基準タグより先にある、同階層のタグを取得
            for (int idx = divCurrent.elementSiblingIndex() + 1; idx < divCurrent.parent().children().size(); idx++) {
                Element divIdx = divCurrent.parent().children().get(idx);

                // 同階層のタグの最初の子供の最初の子供のタグを取得する
                Element divHeader = divIdx.child(0).child(0);
                /*
                 * 上記タグがh3タグなら、別の種別をスクレイピングしてしまうのでbreakする
                 * h4タグならカテゴリーが変化するのでそれを抽出する
                 */
                if (divHeader.tagName().equals("h3")) {
                    break;
                } else if (divHeader.tagName().equals("h4")) {
                    /*
                     * 抽出したカテゴリーから、以下と異なる場合はスキップする
                     *  ・Original
                     *  ・K-POP
                     *  ・World Music
                     *  ・XROSS
                     *  ・J-Music
                     */
                    String category = divHeader.text().trim();
                    if (isSkippedByCategory(category)) continue;

                    // ログ出力
                    Log.d(TAG, "scrapeH3FromDocument:category=" + category);

                    // 抽出したカテゴリーに含まれるtableタグをすべて取得し、譜面サブリストに追加
                    for (Element table : selectTablesFromNonSkippedCategory(divIdx, category)) {
                        chartSubList.addAll(scrapeTdFromTable(table));
                    }
                }
            }
        }

        return chartSubList;
    }

    /**
     * 「種別」のチェック状態と、指定されたh3/h4タグのテキストにおける
     * 以下のいずれかの文字列との一致状態から、スキップするかどうか判別する
     *  ・NORMAL
     *  ・REMIX
     *  ・FULL SONG
     *  ・SHORT CUT
     *  ・PRIME2で復活・削除した曲
     *  ・PRIME2でプレイ可能になったPRIME JE未収録曲
     * @param type h3/h4タグのインスタンスのテキスト
     * @return スキップする場合はtrue、スキップしない場合はfalse
     */
    private static boolean isSkippedByType(String type) {
        // 「NORMAL」のスキップ判定
        boolean normal = CommonParams.type[0] && type.equalsIgnoreCase(CommonParams.TYPES[0]);
        // 「REMIX」のスキップ判定
        boolean remix = CommonParams.type[1] && type.equalsIgnoreCase(CommonParams.TYPES[1]);
        // 「FULL SONG」のスキップ判定
        boolean fullSong = CommonParams.type[2] && type.equalsIgnoreCase(CommonParams.TYPES[2]);
        // 「SHORT CUT」のスキップ判定
        boolean shortCut = CommonParams.type[3] && type.equalsIgnoreCase(CommonParams.TYPES[3]);

        return !(normal || remix || fullSong || shortCut);
    }

    /**
     * 「カテゴリー」のチェック状態と、指定されたh4/h5タグのテキストにおける
     * 以下のいずれかの文字列との一致状態から、スキップするかどうか判別する
     *  ・Original
     *  ・K-POP
     *  ・World Music
     *  ・XROSS
     *  ・J-Music
     * @param category h4/h5タグのインスタンスのテキスト
     * @return スキップする場合はtrue、スキップしない場合はfalse
     */
    private static boolean isSkippedByCategory(String category) {
        // 「Original」のスキップ判定
        boolean original = (CommonParams.category[0])
                        && (category.equalsIgnoreCase(CommonParams.CATEGORIES[0]));
        // 「K-POP」のスキップ判定
        boolean kPop = (CommonParams.category[1])
                    && (category.charAt(0) == 'K' || category.charAt(0) == 'k')
                    && (category.substring(2, 5).equalsIgnoreCase(CommonParams.CATEGORIES[1].substring(2, 5)));
        // 「World Music」のスキップ判定
        boolean worldMusic = (CommonParams.category[2])
                        && (category.substring(0, 5).equalsIgnoreCase(CommonParams.CATEGORIES[2].substring(0, 5)))
                        && (category.substring(6, 11).equalsIgnoreCase(CommonParams.CATEGORIES[2].substring(6, 11)));
        // 「XROSS」のスキップ判定
        boolean xross = (CommonParams.category[3])
                     && (category.substring(0, 5).equalsIgnoreCase(CommonParams.CATEGORIES[3]));
        // 「J-Music」のスキップ判定
        boolean jMusic = (CommonParams.category[4])
                && (category.charAt(0) == 'J' || category.charAt(0) == 'j')
                && (category.substring(2, 7).equalsIgnoreCase(CommonParams.CATEGORIES[4].substring(2, 7)));

        return !(original || kPop || worldMusic || xross || jMusic);
    }

    /**
     * 現在の基準タグより先にある同階層のタグから、すべてのtableタグを取得する
     * @param divIdx 現在の基準タグより先にある同階層のタグ
     * @param category 抽出したカテゴリー
     * @return 指定されたカテゴリーに含まれる、すべてのtableタグ
     */
    private static Elements selectTablesFromNonSkippedCategory(Element divIdx, String category) {
        /*
         * カテゴリーが「XROSS」の場合はさらに細かくh5タグがあるので、
         * divIdxより1つ先にある同階層のタグを取得する
         * それ以外のカテゴリーは、selectメソッドでtableタグを取得するだけでよい
         */
        if (category.substring(0, 5).equalsIgnoreCase(CommonParams.CATEGORIES[3])) {
            // カテゴリーが「XROSS」であるtableタグをすべて含めたリスト
            List<Element> xrossTableList = new ArrayList<>();

            // 現在の基準タグより先にある、同階層のタグを取得
            for (int xrossIdx = divIdx.elementSiblingIndex() + 1; xrossIdx < divIdx.parent().children().size(); xrossIdx++) {
                Element divXrossIdx = divIdx.parent().children().get(xrossIdx);

                // 同階層のタグの最初の子供の最初の子供のタグを取得する
                Element divHeader2 = divXrossIdx.child(0).child(0);
                /*
                 * 上記タグがh5タグならtableタグを抽出し、
                 * それ以外なら別の曲情報をスクレイピングしてしまうのでbreakする
                 */
                if (divHeader2.tagName().equals("h5")) {
                    xrossTableList.addAll(divXrossIdx.select("table"));
                } else {
                    break;
                }
            }

            return new Elements(xrossTableList);
        } else {
            return divIdx.select("table");
        }
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

            // 「SINGLE」を取得し、空文字でなければ譜面サブリストに追加
            Element tdSingle = tds.get(2);
            if (!tdSingle.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdSingle, name, false, false));
            }

            // 「DOUBLE」を取得し、空文字でなければ譜面サブリストに追加
            Element tdDouble = tds.get(3);
            if (!tdDouble.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdDouble, name, true, false));
            }

            // 「S-PERF」を取得し、空文字でなければ譜面サブリストに追加
            Element tdSPerf = tds.get(4);
            if (!tdSPerf.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdSPerf, name, false, true));
            }

            // 「D-PERF」を取得し、空文字でなければ譜面サブリストに追加
            Element tdDPerf = tds.get(5);
            if (!tdDPerf.html().equals("")) {
                chartSubList.addAll(scrapeChartFromTd(tdDPerf, name, true, true));
            }

            // ログ出力
            Log.d(TAG, "scrapeChartFromTd:name=" + name + ",tds=" + tds.text());
        }

        return chartSubList;
    }

    /**
     * 指定されたtdタグから、tdタグに含まれる譜面情報をスクレイピングする
     * @param td tdタグのインスタンス
     * @param name 曲名
     * @param isDouble Single、Doubleのフラグ
     * @param isPerformance Performance譜面のフラグ
     * @return tdタグに含まれる譜面サブリスト
     */
    private static List<UnitChart> scrapeChartFromTd(Element td, String name, boolean isDouble, boolean isPerformance) {
        // 指定されたtdタグに含まれる譜面サブリストのインスタンス
        List<UnitChart> chartSubList = new ArrayList<>();

        // tdタグ中の「その他」に該当しない譜面を表した文字列を取得
        String chartsStr = td.ownText();
        StringBuilder workStr = new StringBuilder();
        for (int i = 0; i < chartsStr.length(); i++) {
            if (chartsStr.charAt(i) == '/') {
                try {
                    // 難易度を取得して譜面サブリストに入れる
                    int difficulty = Integer.parseInt(workStr.toString().trim());
                    chartSubList.add(new UnitChart(name, isDouble, isPerformance, difficulty));
                } catch (Exception e) {
                    // "//"や"/ /"など、数値に変換できない文字列がスラッシュに囲まれている場合は何もしない
                } finally {
                    workStr = new StringBuilder();
                }
            } else {
                workStr.append(chartsStr.charAt(i));
            }
        }

        // tdタグ中の「その他」に該当する譜面をすべて取得
        for (Element span : td.getElementsByTag("span")) {
            /*
             * 以下の譜面を判別し、「その他」のチェック状態によって譜面サブリストに入れる
             * ・PP解禁譜面 : #ffaa00を含む
             * ・AM.PASS使用時限定譜面 : #0075c8または#009e25を含む
             * 上記譜面以外は、譜面サブリストに入れない
             */
            boolean other = span.attributes().get("style").contains("#ffaa00") && CommonParams.ppUnlockedStep;
            other = other || span.attributes().get("style").contains("#0075c8") && CommonParams.amPassOnlyUsedStep;
            other = other || span.attributes().get("style").contains("#009e25") && CommonParams.amPassOnlyUsedStep;
            if (other) {
                // COOP譜面の判別
                if (span.text().contains("COOP") || span.text().contains("CO-OP")) {
                    chartSubList.add(new UnitChart(name));
                } else {
                    chartSubList.add(new UnitChart(name, isDouble, isPerformance, Integer.parseInt(span.text())));
                }
            }
        }

        return chartSubList;
    }

    // 抽象staticクラスなのでコンストラクタはprivateにする
    private Chooser() {}
}