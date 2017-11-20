package com.piusubjectchart

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // メイン画面取得
        setContentView(R.layout.activity_main)

        // 「タイプ」のチェックボックス取得
        var checkBoxSingle = findViewById<CheckBox>(R.id.typeSingle)
        var checkBoxDouble = findViewById<CheckBox>(R.id.typeDouble)
        var checkBoxCoop = findViewById<CheckBox>(R.id.typeCoop)

        // 「バージョン」のチェックボックス取得
        var checkBox1stToPerfectCollection = findViewById<CheckBox>(R.id.version1stToPerfectCollection)
        var checkBoxExtraToPrex3 = findViewById<CheckBox>(R.id.versionExtraToPrex3)
        var checkBoxExceedToZero = findViewById<CheckBox>(R.id.versionExceedToZero)
        var checkBoxNXToNXA = findViewById<CheckBox>(R.id.versionNXToNXA)
        var checkBoxFiestaToFiestaEx = findViewById<CheckBox>(R.id.versionFiestaToFiestaEx)
        var checkBoxFiesta2 = findViewById<CheckBox>(R.id.versionFiesta2)
        var checkBoxPrime = findViewById<CheckBox>(R.id.versionPrime)
        var checkBoxPrime2 = findViewById<CheckBox>(R.id.versionPrime2)
    }
}
