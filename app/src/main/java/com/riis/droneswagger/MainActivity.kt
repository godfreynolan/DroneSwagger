package com.riis.droneswagger

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.riis.droneswagger.api.common.DroneApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openapitools.client.infrastructure.ApiClient

class MainActivity : AppCompatActivity() {

    private lateinit var droneApi: DroneApi
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var button3: Button
    private lateinit var button4: Button
    private lateinit var resultsView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        resultsView = findViewById(R.id.resultsView)


        // Initialize the API client
        val apiClient = ApiClient("http://10.0.0.10:8080/")
        droneApi = DroneApi(apiClient.baseUrl, apiClient.client)

        button1.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val droneId = "123"
                val droneAction = "takeoff" // or whatever action you want to execute
                try {
                    val response = droneApi.droneIdActionsPutWithHttpInfo(droneId, droneAction)
                    resultsView.text = response.statusCode.toString()
                } catch (e: Exception) {
                    Log.e("ERRORS", "Error calling API: ${e.message}")
                }
            }
        }

        button2.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val droneId = "123"
                try {
                    val response = droneApi.droneIdGetWithHttpInfo(droneId)
                    resultsView.text = response.statusCode.toString()
                } catch (e: Exception) {
                    Log.e("ERRORS", "Error calling API: ${e.message}")
                }
            }
        }

        button3.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val droneId = "123"
                try {
                    val response = droneApi.droneIdZoomPutWithHttpInfo(droneId)
                    resultsView.text = response.statusCode.toString()
                } catch (e: Exception) {
                    Log.e("ERRORS", "Error calling API: ${e.message}")
                }
            }
        }

        button4.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val droneId = "123"
                try {
                    val response = droneApi.droneIdZoomDeleteWithHttpInfo(droneId)
                    resultsView.text = response.statusCode.toString()
                } catch (e: Exception) {
                    Log.e("ERRORS", "Error calling API: ${e.message}")
                }
            }
        }

    }
}