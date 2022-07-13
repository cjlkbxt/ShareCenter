package ai.leqi.lib_share_center.webchromeclient;

import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;

public class CustomWebChromeClient extends WebChromeClient {

    private static final String TAG = "CustomWebChromeClient";

    public CustomWebChromeClient() {
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.d(TAG, consoleMessage.message());
        return super.onConsoleMessage(consoleMessage);
    }

}
