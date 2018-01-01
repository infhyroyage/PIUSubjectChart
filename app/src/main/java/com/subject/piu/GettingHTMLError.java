package com.subject.piu;

public enum GettingHTMLError {
    // 通信不可能エラー
    CONNECTION,

    // 通信遮断エラー
    INTERRUPT,

    // 存在しないURLエラー
    URL,

    // その他(システムエラー)
    OTHER,
}
