/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hurry.custom.view.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.apache.http.Header;
import org.json.JSONObject;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.controller.WebClient;

public class ContactUsFragment extends BaseFragment {
    Context mContext;
    TextView txtContact;
    WebView news_desc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (View) inflater.inflate(
                R.layout.fragment_contact_us, container, false);
        Constants.page_type = "contact";
        txtContact = (TextView)view.findViewById(R.id.txt_contact);
        news_desc = (WebView)view.findViewById(R.id.desc);
        contact();
        return view;
    }


    private void contact() {
        RequestParams params = new RequestParams();
        WebClient.post(Constants.BASE_URL + "contact", params,
                new JsonHttpResponseHandler() {
                    public void onStart() {
                      //  ((MainActivity)mContext).showProgressDialog();
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        // Log.d("Error String", responseString);
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        // Log.d("Error", errorResponse.toString());
                    }
                    public void onSuccess(int statusCode,
                                          Header[] headers,
                                          JSONObject response) {

                        if (response != null) {
                            try{
                                String text = response.getString("result");
                                txtContact.setText(text);
                                showWebView(text);
                            }catch (Exception e){

                            }
                        }
                    };
                    public void onFinish()
                    {
                        //((MainActivity)mContext).hideProgressDialog();
                    }
                    ;
                });
    }



    public void showWebView(String title) {
        news_desc.setBackgroundColor(Color.parseColor("#ffffff"));
        news_desc.setFocusableInTouchMode(false);
        news_desc.setFocusable(false);
        news_desc.getSettings().setDefaultTextEncodingName("UTF-8");

        WebSettings webSettings = news_desc.getSettings();
        Resources res = getResources();
        int fontSize = 15;
        webSettings.setDefaultFontSize(fontSize);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = title;


        String text = "<html><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        news_desc.loadData(htmlText, mimeType, encoding);
    }



}
