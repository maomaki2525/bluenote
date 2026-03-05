package com.example

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    // 静的ファイル（HTML/CSS）配信
    configureRouting()

    // 掲示板API（スレ作成/一覧、投稿作成/一覧）
    configureBbsApi()
}