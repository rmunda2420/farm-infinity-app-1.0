// Not in use
package com.farminfinity.farminfinity.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.CFTheme;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.ui.api.CFDropCheckoutPayment;
import com.cashfree.pg.ui.api.CFPaymentComponent;
import com.farminfinity.farminfinity.R;

public class DropCheckoutActivity extends AppCompatActivity implements CFCheckoutResponseCallback {
    String orderId = null;
    String paymentSessionId = null;
    CFSession.Environment cfEnvironment = CFSession.Environment.PRODUCTION;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drop_checkout);
        orderId = getIntent().getStringExtra("ORDER_ID");
        paymentSessionId = getIntent().getStringExtra("PAYMENT_SESSION_ID");
        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException e) {
            e.printStackTrace();
        }

        doDropCheckoutPayment();
    }

    @Override
    public void onPaymentVerify(String s) {
        Log.e("onPaymentVerify", "verifyPayment triggered");
        // Start verifying your payment
    }

    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String s) {
        Log.e("onPaymentFailure " + orderId, cfErrorResponse.getMessage());
    }

    public void doDropCheckoutPayment() {
        try {
            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(cfEnvironment)
                    .setPaymentSessionID(paymentSessionId)
                    .setOrderId(orderId)
                    .build();
            CFPaymentComponent cfPaymentComponent = new CFPaymentComponent.CFPaymentComponentBuilder()
                    // Shows only Card and UPI modes
                    .add(CFPaymentComponent.CFPaymentModes.CARD)
                    .add(CFPaymentComponent.CFPaymentModes.UPI)
                    .build();
            // Replace with your application's theme colors
            CFTheme cfTheme = new CFTheme.CFThemeBuilder()
                    .setNavigationBarBackgroundColor("#fc2678")
                    .setNavigationBarTextColor("#ffffff")
                    .setButtonBackgroundColor("#fc2678")
                    .setButtonTextColor("#ffffff")
                    .setPrimaryTextColor("#000000")
                    .setSecondaryTextColor("#000000")
                    .build();
            CFDropCheckoutPayment cfDropCheckoutPayment = new CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFUIPaymentModes(cfPaymentComponent)
                    .setCFNativeCheckoutUITheme(cfTheme)
                    .build();
            CFPaymentGatewayService gatewayService = CFPaymentGatewayService.getInstance();
            gatewayService.doPayment(DropCheckoutActivity.this, cfDropCheckoutPayment);
        } catch (CFException exception) {
            exception.printStackTrace();
        }
    }
}