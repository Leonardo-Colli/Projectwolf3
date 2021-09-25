package com.example.project_wolf_3;

import android.app.Application;

import com.stripe.android.PaymentConfiguration;

public class Pago extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_51JZJIlDgaelGiJo8nxbAIkhV2f8pwAoTkZGMDD6Phyo7ETCfMlTOapBpLy1ajaMxMUr2SAvA1ODLBbXEyZpEHNYk00ubUgB1lD"
        );
    }
}