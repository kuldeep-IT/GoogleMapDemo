package com.rkuldeep.googlemapdemo
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

fun makePostApiCall(apiUrl: String, requestBody: String): String {
    var result: String = ""

    try {
        val url = URL(apiUrl)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.setRequestProperty("Content-Type", "application/json") // Change this if you have a different content type

        // Send the request body if there is any
        if (requestBody.isNotEmpty()) {
            connection.doOutput = true
            val outputStream: OutputStream = connection.outputStream
            outputStream.write(requestBody.toByteArray())
            outputStream.flush()
            outputStream.close()
        }

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()

            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }

            bufferedReader.close()
            result = stringBuilder.toString()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return result
}