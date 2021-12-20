package net.lberrymage.accrescent.data

import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URL
import javax.inject.Inject
import javax.net.ssl.HttpsURLConnection

class DevelopersFetcherImpl @Inject constructor() : DevelopersFetcher {
    private val connection =
        URL(REPOSITORY_URL + DEVELOPERS_PATH).openConnection() as HttpsURLConnection

    override suspend fun fetchLatestDevelopers(): List<Developer> {
        connection.connect()

        val data = connection.inputStream
        val buf = ByteArray(DEFAULT_BUFFER_SIZE)
        val outBuf = ByteArrayOutputStream()

        var bytes = data.read(buf)
        while (bytes >= 0) {
            outBuf.write(buf, 0, bytes)
            bytes = data.read(buf)
        }

        connection.disconnect()

        val json = JSONObject(outBuf.toString())

        return listOf(Developer(json.getString("username"), json.getString("public_key")))
    }

    companion object {
        const val REPOSITORY_URL = "https://staging.lberrymage.net"
        const val DEVELOPERS_PATH = "/developers.json"
    }
}