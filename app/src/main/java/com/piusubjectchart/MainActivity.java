package com.piusubjectchart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // デバッグ用のタグ
    private static final String TAG = "MainActivity";

    // 「タイプ」のチェックボックス
    private CheckBox typeSingle, typeDouble, typeCoop;
    // 「バージョン」のチェックボックス
    private CheckBox checkBox1stToPerfectCollection, checkBoxExtraToPrex3, checkBoxExceedToZero, checkBoxNXToNXA, checkBoxFiestaToFiestaEx, checkBoxFiesta2, checkBoxPrime, checkBoxPrime2;
    
    // 「難易度」の上限&下限値
    private int difficultyFrom = 1, difficultyTo = 1;

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
        Spinner spinnerFrom = findViewById(R.id.spinnerFrom);
        Spinner spinnerTo = findViewById(R.id.spinnerTo);
        // 「難易度」の上限&下限スピナーに難易度の初期値(MIN_DIFFICULTY〜MAX_DIFFICULTY)をセット
        List<Integer> list = new ArrayList<>();
        for (int i = CommonParams.MIN_DIFFICULTY; i <= CommonParams.MAX_DIFFICULTY; i++) {
            list.add(i);
        }
        final ArrayAdapter<Integer> adapterFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        final ArrayAdapter<Integer> adapterTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        spinnerFrom.setAdapter(adapterFrom);
        spinnerTo.setAdapter(adapterTo);
        // 「難易度」の上限&下限スピナーにリスナーをセット
        spinnerFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // 選択した難易度をセット
                difficultyFrom = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Spinnerでは使用しない
            }
        });
        spinnerTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                // 選択した難易度をセット
                difficultyTo = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "spinnerTo#onNothingSelected");
            }
        });

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
}
