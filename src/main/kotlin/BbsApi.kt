package com.example

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class ThreadItem(val id: Int, val title: String, val createdAt: String)

@Serializable
data class PostItem(val id: Int, val threadId: Int, val name: String, val text: String, val time: String)

private val json = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
}

private val dataDir: Path = Paths.get("data")
private val threadsFile: Path = dataDir.resolve("threads.json")
private val postsFile: Path = dataDir.resolve("posts.json")

private val threadSeq = AtomicInteger(1)
private val postSeq = AtomicInteger(1)

private val threads = mutableListOf<ThreadItem>()
private val posts = mutableListOf<PostItem>()

private fun now(): String =
    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

private fun ensureDataDir() {
    if (!Files.exists(dataDir)) Files.createDirectories(dataDir)
}

private fun loadAll() {
    ensureDataDir()

    // threads
    if (threadsFile.exists()) {
        runCatching {
            val loaded: List<ThreadItem> = json.decodeFromString(threadsFile.readText(Charsets.UTF_8))
            threads.clear()
            threads.addAll(loaded)
        }
    }

    // posts
    if (postsFile.exists()) {
        runCatching {
            val loaded: List<PostItem> = json.decodeFromString(postsFile.readText(Charsets.UTF_8))
            posts.clear()
            posts.addAll(loaded)
        }
    }

    // 連番を復元（最大ID+1）
    val nextThreadId = (threads.maxOfOrNull { it.id } ?: 0) + 1
    val nextPostId = (posts.maxOfOrNull { it.id } ?: 0) + 1
    threadSeq.set(nextThreadId)
    postSeq.set(nextPostId)
}

private fun saveThreads() {
    ensureDataDir()
    threadsFile.writeText(json.encodeToString(threads), Charsets.UTF_8)
}

private fun savePosts() {
    ensureDataDir()
    postsFile.writeText(json.encodeToString(posts), Charsets.UTF_8)
}

fun Application.configureBbsApi() {

    // ★起動時にファイルから復元
    loadAll()

    routing {

        // スレ一覧
        get("/api/threads") {
            call.respond(threads.sortedByDescending { it.id })
        }

        // 新規スレ作成（Content-Type: text/plain）
        post("/api/threads") {
            val titleRaw = call.receiveText().trim()
            val title = titleRaw.take(40)
            if (title.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "title required")
                return@post
            }

            val t = ThreadItem(
                id = threadSeq.getAndIncrement(),
                title = title,
                createdAt = now()
            )
            threads.add(t)

            // ★保存
            saveThreads()

            call.respond(HttpStatusCode.Created, t)
        }

        // 投稿一覧（threadId必須）
        get("/api/posts") {
            val threadId = call.request.queryParameters["threadId"]?.toIntOrNull()
            if (threadId == null) {
                call.respond(HttpStatusCode.BadRequest, "threadId required")
                return@get
            }
            call.respond(posts.filter { it.threadId == threadId })
        }

        // 新規投稿（application/x-www-form-urlencoded）
        post("/api/posts") {
            val params = call.receiveParameters()
            val threadId = params["threadId"]?.toIntOrNull()
            val name = (params["name"] ?: "名無しさん").trim().ifBlank { "名無しさん" }.take(20)
            val text = (params["text"] ?: "").trim().take(200)

            if (threadId == null) {
                call.respond(HttpStatusCode.BadRequest, "threadId required")
                return@post
            }
            if (text.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, "text required")
                return@post
            }

            val p = PostItem(
                id = postSeq.getAndIncrement(),
                threadId = threadId,
                name = name,
                text = text,
                time = now()
            )
            posts.add(p)

            // ★保存
            savePosts()

            call.respond(HttpStatusCode.Created, p)
        }
    }
}