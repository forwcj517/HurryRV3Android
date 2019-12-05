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
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.hurry.custom.R;
import com.hurry.custom.common.Constants;
import com.hurry.custom.view.activity.CameraOrderActivity;
import com.hurry.custom.view.dialog.ChooseTypeDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hurry.custom.view.activity.HomeActivity.INTENT_REQUEST_GET_IMAGES;

public class HomeFragment extends BaseFragment implements  View.OnClickListener{


    Context mContext;
    @BindView(R.id.rl_station) RelativeLayout rlStation;
    @BindView(R.id.rl_city) RelativeLayout rlCity;
    @BindView(R.id.rl_international) RelativeLayout rlInternational;

    private static final String[] INITIAL_PERMS={
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int INITIAL_REQUEST=1337;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = (View) inflater.inflate(
                R.layout.fragment_home, container, false);
        position = 2;
        mContext = getActivity();
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case INITIAL_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED  && ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED ) {
                        Constants.ORDER_TYPE = Constants.CAMERA_OPTION;
                        Intent intent = new Intent(getActivity(), CameraOrderActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(mContext, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == INTENT_REQUEST_GET_IMAGES) {
            }
        }
    }


    private void initView(){
        rlStation.setOnClickListener(this);
        rlCity.setOnClickListener(this);
        rlInternational.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_station:
                ChooseTypeDialog.show(getActivity(), "station");
                break;
            case R.id.rl_city:
                ChooseTypeDialog.show(getActivity(), "city");
                break;
            case R.id.rl_international:
                ChooseTypeDialog.show(getActivity(), "international");
                break;

        }
    }


}