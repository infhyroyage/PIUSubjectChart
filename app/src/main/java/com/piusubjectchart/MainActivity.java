package com.piusubjectchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    // 「タイプ」のチェックボックス
    private CheckBox typeSingle, typeDouble, typeCoop;
    // 「難易度」の上限&下限スピナー
    private Spinner difficultyFrom, difficultyTo;
    // 「バージョン」のチェックボックス
    private CheckBox checkBox1stToPerfectCollection, checkBoxExtraToPrex3, checkBoxExceedToZero, checkBoxNXToNXA, checkBoxFiestaToFiestaEx, checkBoxFiesta2, checkBoxPrime, checkBoxPrime2;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        // メイン画面取得
        setContentView(R.layout.activity_main);

        // 「タイプ」のチェックボックス取得
        typeSingle = findViewById(R.id.typeSingle);
        typeDouble = findViewById(R.id.typeDouble);
        typeCoop = findViewById(R.id.typeCoop);

        // 「難易度」の上限&下限スピナー取得
        difficultyFrom = findViewById(R.id.difficultyFrom);
        difficultyTo = findViewById(R.id.difficultyTo);

        // 「バージョン」のチェックボックス取得
        checkBox1stToPerfectCollection = findViewById(R.id.version1stToPerfectCollection);
        checkBoxExtraToPrex3 = findViewById(R.id.versionExtraToPrex3);
        checkBoxExceedToZero = findViewById(R.id.versionExceedToZero);
        checkBoxNXToNXA = findViewById(R.id.versionNXToNXA);
        checkBoxFiestaToFiestaEx = findViewById(R.id.versionFiestaToFiestaEx);
        checkBoxFiesta2 = findViewById(R.id.versionFiesta2);
        checkBoxPrime = findViewById(R.id.versionPrime);
        checkBoxPrime2 = findViewById(R.id.versionPrime2);
    }

    private void setEntriesFrom() {
        // TODO : 難易度上限のスピナーのエントリーのうち、最大値を取得
    }

    private void setEntriesTo() {
        // TODO : 難易度下限のスピナーのエントリーのうち、最小値を取得
    }
}
