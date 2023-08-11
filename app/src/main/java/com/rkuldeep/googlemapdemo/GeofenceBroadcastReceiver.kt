package com.rkuldeep.googlemapdemo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.rkuldeep.googlemapdemo.utils.Utils


class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val sharedPreference = context.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
        val notificationHelper = NotificationHelper(context)
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent!!.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...")
            return
        }
        val geofenceList = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList!!) {
            Log.d(TAG, "onReceive: " + geofence.requestId)
        }
        //        Location location = geofencingEvent.getTriggeringLocation();
        val transitionType = geofencingEvent.geofenceTransition
        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show()

                var bundle = Bundle()
                bundle.putString(
                    "device_id",
                    Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                )
                bundle.putString(
                    "mobile_number", sharedPreference.getString("phonenumber", "")
                )

                // Replace "your_api_url_here" and "your_request_body_here" with the actual API URL and request body
                val apiUrl = "https://us-central1-tatvic-gcp-dev-team.cloudfunctions.net/gmaps-poc"
                val requestBody = Utils.bundleToJsonString(bundle)

                // Execute the API call in the background thread
                ApiCallTask().execute(apiUrl, requestBody)

                notificationHelper.sendHighPriorityNotification(
                    "GEOFENCE_TRANSITION_ENTER", "",
                    MapsActivity::class.java
                )
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "GEOFENCE_TRANSITION_DWELL", "",
                    MapsActivity::class.java
                )
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "GEOFENCE_TRANSITION_EXIT", "",
                    MapsActivity::class.java
                )
            }
        }
    }


    private inner class ApiCallTask : AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String): String {
            val apiUrl = params[0]
            val requestBody = params[1]
            return makePostApiCall(apiUrl, requestBody)
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)

            Log.d("MY_RESPONSE", result)
        }
    }

    companion object {
        private const val TAG = "GeofenceBroadcastReceive"
    }
}