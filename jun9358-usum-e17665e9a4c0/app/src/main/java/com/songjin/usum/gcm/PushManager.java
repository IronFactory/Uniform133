package com.songjin.usum.gcm;

import android.util.Log;

import com.songjin.usum.controllers.activities.BaseActivity;
import com.songjin.usum.entities.AlarmEntity;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class PushManager {
    private static final String TAG = "PushManager";

    public static void sendTransactionPush(ArrayList<String> userIds, String msg) {
        Log.d(TAG, "msg = " + msg);

//        final JSONArray array = new JSONArray();
//        for (String id: userIds) {
//            array.put(id);
//        }

        final String json = "{" +
                "\"for_apns\":{" +
                "\"badge\": 3," +
                "\"sound\": \"sound_file\"," +
                "\"push_alert\": true," +
                "\"message\": \"홍길동님 외 2명이 댓글을 달았습니다.\"," +
                "\"custom_field\": {" +
                "\"article_id\": \"111\"," +
                "\"comment_id\": \"222\"" +
                "}" +
                "}," +
                "\"for_gcm\":{" +
                "\"collapse\": \"articleId123\"," +
                "\"delay_while_idle\":false," +
                "\"custom_field\": {" +
                "\"msg\": \"" + msg + "\"" +
                "}" +
                "}" +
                "}";

        sendByHttp(userIds, msg);

//        for (int i = 0; i < userIds.size(); i++) {
//            Log.d(TAG, "GCM Device Id = " + userIds.get(i));
//            PushService.sendPushMessage(new ApiResponseCallback<Boolean>() {
//                @Override
//                public void onSessionClosed(ErrorResult errorResult) {
//                    Log.d(TAG, "GCM PUSH 에러 메세지 = " + errorResult.getErrorMessage());
//                    Log.d(TAG, "GCM PUSH 에러 URL = " + errorResult.getHttpStatus());
//                    Log.d(TAG, "GCM PUSH 에러 코드 = " + errorResult.getErrorCode());
//                }
//
//                @Override
//                public void onNotSignedUp() {
//                    Log.d(TAG, "GCM PUSH Not SignedUp");
//                }
//
//                @Override
//                public void onSuccess(Boolean result) {
//                    Log.d(TAG, "GCM PUSH 성공");
//                }
//            }, json, userIds.get(i));
//        }
    }


    public static void sendByHttp(final ArrayList<String> userIds, final String msg) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final JSONArray array = new JSONArray();
                    for (String id: userIds) {
                        array.put(Integer.parseInt(id));
                        searchToken(id);
                    }

                    final String json = "{" +
                            "\"for_apns\":{" +
                            "\"badge\": 3," +
                            "\"sound\": \"sound_file\"," +
                            "\"push_alert\": true," +
                            "\"message\": \"홍길동님 외 2명이 댓글을 달았습니다.\"," +
                            "\"custom_field\": {" +
                            "\"article_id\": \"111\"," +
                            "\"comment_id\": \"222\"" +
                            "}" +
                            "}," +
                            "\"for_gcm\":{" +
                            "\"collapse\": \"articleId123\"," +
                            "\"delay_while_idle\":false," +
                            "\"custom_field\": {" +
                            "\"msg\": \"" + msg + "\"" +
                            "}" +
                            "}" +
                            "}";

                    URL url = new URL("https://kapi.kakao.com/v1/push/send");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Authorization", "KakaoAK cfa39b812b10bafebb44ffc2898a0169");
//                    conn.setRequestProperty("Authorization", "KakaoAK a6a1e884f2ecbbb1dcf154a7e49e40f0");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    String urlParameters = "uuids="+ array.toString() +"&push_message=" + json;
                    Log.d(TAG, "param = " + urlParameters);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(urlParameters);
                    writer.flush();
                    writer.close();

//                    StringBuilder builder = new StringBuilder();
//                    builder.append("uuids").append("=").append(array.toString()).append("&");
//                    builder.append("push_message").append("=").append(json);
//
//                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
//                    pw.write(builder.toString());
//                    pw.flush();
//                    pw.close();

                    int code = conn.getResponseCode();
                    String message = conn.getResponseMessage();
                    Log.d(TAG, "code = " + code);
                    Log.d(TAG, "message = " + message);

                    InputStreamReader in;
                    if (code == 200)
                         in = new InputStreamReader(conn.getInputStream());
                    else
                        in = new InputStreamReader(conn.getErrorStream());
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    BufferedReader reader = new BufferedReader(in);

                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    Log.d(TAG, "res = " + response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void registerToken(final String uuid, final String deviceId, final String pushType, final String token) {
        Log.d(TAG, "registerToken = " + token);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://kapi.kakao.com/v1/push/register");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Authorization", "KakaoAK cfa39b812b10bafebb44ffc2898a0169");
//                    conn.setRequestProperty("Authorization", "KakaoAK a6a1e884f2ecbbb1dcf154a7e49e40f0");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    String urlParameters = "uuid="+ uuid + "&device_id=" + deviceId + "&push_type=" + pushType + "&push_token=" + token;
                    Log.d(TAG, "registerToken param = " + urlParameters);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(urlParameters);
                    writer.flush();
                    writer.close();

//                    urlParameters = "push_token=" + token;
//                    Log.d(TAG, "registerToken param = " + urlParameters);
//                    writer = new OutputStreamWriter(conn.getOutputStream());
//                    writer.write(urlParameters);
//                    writer.flush();
//                    writer.close();

//                    StringBuilder builder = new StringBuilder();
//                    builder.append("uuids").append("=").append(array.toString()).append("&");
//                    builder.append("push_message").append("=").append(json);
//
//                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
//                    pw.write(builder.toString());
//                    pw.flush();
//                    pw.close();

                    int code = conn.getResponseCode();
                    String message = conn.getResponseMessage();
                    Log.d(TAG, "registerToken code = " + code);
                    Log.d(TAG, "registerToken message = " + message);

                    InputStreamReader in;
                    if (code == 200)
                        in = new InputStreamReader(conn.getInputStream());
                    else
                        in = new InputStreamReader(conn.getErrorStream());
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    BufferedReader reader = new BufferedReader(in);

                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    Log.d(TAG, "registerToken res = " + response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void deregisterToken(final String uuid, final String deviceId, final String pushType) {
        Log.d(TAG, "deregisterToken = " + uuid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://kapi.kakao.com/v1/push/deregister");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Authorization", "KakaoAK cfa39b812b10bafebb44ffc2898a0169");
//                    conn.setRequestProperty("Authorization", "KakaoAK a6a1e884f2ecbbb1dcf154a7e49e40f0");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    String urlParameters = "uuid="+ uuid + "&device_id=" + deviceId + "&push_type=" + pushType;
                    Log.d(TAG, "deregisterToken param = " + urlParameters);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(urlParameters);
                    writer.flush();
                    writer.close();

//                    StringBuilder builder = new StringBuilder();
//                    builder.append("uuids").append("=").append(array.toString()).append("&");
//                    builder.append("push_message").append("=").append(json);
//
//                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
//                    pw.write(builder.toString());
//                    pw.flush();
//                    pw.close();

                    int code = conn.getResponseCode();
                    String message = conn.getResponseMessage();
                    Log.d(TAG, "deregisterToken code = " + code);
                    Log.d(TAG, "deregisterToken message = " + message);

                    InputStreamReader in;
                    if (code == 200)
                        in = new InputStreamReader(conn.getInputStream());
                    else
                        in = new InputStreamReader(conn.getErrorStream());
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    BufferedReader reader = new BufferedReader(in);

                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    Log.d(TAG, "deregisterToken res = " + response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void searchToken(final String uuid) {
        Log.d(TAG, "searchToken = " + uuid);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://kapi.kakao.com/v1/push/tokens");
                    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    conn.setRequestProperty("charset", "utf-8");
                    conn.setRequestProperty("Authorization", "KakaoAK cfa39b812b10bafebb44ffc2898a0169");
//                    conn.setRequestProperty("Authorization", "KakaoAK a6a1e884f2ecbbb1dcf154a7e49e40f0");
                    conn.setDoInput(true);
                    conn.setDoOutput(true);

                    String urlParameters = "uuid="+ uuid;
                    Log.d(TAG, "searchToken param = " + urlParameters);
                    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                    writer.write(urlParameters);
                    writer.flush();
                    writer.close();

//                    StringBuilder builder = new StringBuilder();
//                    builder.append("uuids").append("=").append(array.toString()).append("&");
//                    builder.append("push_message").append("=").append(json);
//
//                    PrintWriter pw = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(), "utf-8"));
//                    pw.write(builder.toString());
//                    pw.flush();
//                    pw.close();

                    int code = conn.getResponseCode();
                    String message = conn.getResponseMessage();
                    Log.d(TAG, "searchToken code = " + code);
                    Log.d(TAG, "searchToken message = " + message);

                    InputStreamReader in;
                    if (code == 200)
                        in = new InputStreamReader(conn.getInputStream());
                    else
                        in = new InputStreamReader(conn.getErrorStream());
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    BufferedReader reader = new BufferedReader(in);

                    while ((inputLine = reader.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //print result
                    Log.d(TAG, "searchToken res = " + response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void sendReservationPushToMe(String msg) {
        GCMIntentService.generateNotification(BaseActivity.context, new AlarmEntity(msg, GCMIntentService.PUSH_TYPE_RESERVATION));
    }

    public static void sendSchoolRankUpdatedPushToMe(String msg) {
        GCMIntentService.generateNotification(BaseActivity.context, new AlarmEntity(msg, GCMIntentService.PUSH_TYPE_SCHOOL_RANK_UPDATED));
    }
}
