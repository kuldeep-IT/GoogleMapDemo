package com.rkuldeep.googlemapdemo.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class Utils {

    public static String bundleToJsonString(Bundle bundle) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            stringBuilder.append("\"").append(key).append("\":").append(valueToJsonString(value)).append(",");
        }

        if (stringBuilder.length() > 1) {
            // Remove the trailing comma
            stringBuilder.setLength(stringBuilder.length() - 1);
        }

        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    // Method to convert a value to its JSON string representation
    private static String valueToJsonString(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeString((String) value) + "\"";
        } else if (value instanceof Integer || value instanceof Long || value instanceof Double || value instanceof Boolean) {
            return value.toString();
        } else {
            // Handle other value types accordingly
            return "\"" + escapeString(value.toString()) + "\"";
        }
    }

    // Method to escape special characters in a string
    private static String escapeString(String value) {
        value = value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        // Handle other special characters if needed
        return value;
    }

    private static void sendWhatsApp(Activity activity, String message){
        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
        whatsappIntent.setType("text/plain");
        whatsappIntent.setPackage("com.whatsapp");
        whatsappIntent.putExtra(Intent.EXTRA_TEXT, message);
        try {
            activity.startActivity(whatsappIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity,"Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
