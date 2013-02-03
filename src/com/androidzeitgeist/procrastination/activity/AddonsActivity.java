package com.androidzeitgeist.procrastination.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.vending.billing.utils.IabHelper;
import com.android.vending.billing.utils.IabResult;
import com.android.vending.billing.utils.Inventory;
import com.android.vending.billing.utils.Purchase;
import com.androidzeitgeist.procrastination.BuildConfig;
import com.androidzeitgeist.procrastination.R;
import com.androidzeitgeist.procrastination.billing.Addons;

public class AddonsActivity extends Activity implements IabHelper.OnIabSetupFinishedListener, IabHelper.QueryInventoryFinishedListener, IabHelper.OnIabPurchaseFinishedListener {
    private static final String PRODUCT_ID_WIDGET = "addon_widget";
    private static final String ACTION_CONFIGURE_WIDGET = "android.appwidget.action.APPWIDGET_CONFIGURE";

    private static final int REQUEST_CODE_PURCHASE = 1011;

    private IabHelper billingHelper;
    private int widgetId;

    private boolean isBillingSupported;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_addons);

        String publicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAm2WwYsy0sC4gCcal0bxln6nmGY50KjhEs9fvjbk6WeN612Z63HikAvg8MWkHfpPUQbz8h+5Noc3VGWLpY7/ttUp4UO1g2LkKC1ich99r/Zn5BMCzy8JQ1LX4UKVpEmZ2m4gElSb8JqntDnocD0obj9MPSWJYKfQnMYsVTenIqwtLRwoxUc+PkUexfCWhIwVyZW10SYiUTCGvwvXMNsKSRM31UbFa+bPqQwvkWTG50WAFRgUiA+DbmMJZ/VEu1Tfp9iPHHZWNuWPLLrKxR9Jo1vAWcuejqmuk8kcnGNbgUOgTcQ9pB4DxQJhrSj80LzpKaxZ9nJAjRVMzfuwhuTprrQIDAQAB";

        billingHelper = new IabHelper(this, publicKey);
        billingHelper.enableDebugLogging(BuildConfig.DEBUG, "Procrastination/BillingHelper");
        billingHelper.startSetup(this);

        if (ACTION_CONFIGURE_WIDGET.equals(getIntent().getAction())) {
            boolean isWidgetEnabled = Addons.isWidgetEnabled(this);

            widgetId = getIntent().getExtras().getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            );

            if (isWidgetEnabled) {
                setWidgetConfigurationResult();
                finish();
            }
        } else {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        updateUiState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!billingHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onBuyWidget(View view) {
        if (isBillingSupported) {
            startBillingForWidget();
        }
    }

    public void startBillingForWidget() {
        billingHelper.launchPurchaseFlow(this, PRODUCT_ID_WIDGET, REQUEST_CODE_PURCHASE, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIabSetupFinished(IabResult result) {
        isBillingSupported = result.isSuccess();

        if (isBillingSupported) {
            billingHelper.queryInventoryAsync(this);
        } else {
            findViewById(R.id.not_available).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        if (result.isFailure()) {
            return;
        }

        if (inventory.hasPurchase(PRODUCT_ID_WIDGET)) {
            Addons.enableWidget(this);
            updateUiState();
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isFailure()) {
            Toast.makeText(this, R.string.billing_failed, Toast.LENGTH_LONG).show();
            return;
        }

        if (purchase.getSku().equals(PRODUCT_ID_WIDGET)) {
            onWidgetPurchased();
            return;
        }
    }

    public void onWidgetPurchased() {
        Toast.makeText(this, R.string.billing_success, Toast.LENGTH_LONG).show();

        Addons.enableWidget(this);
        updateUiState();
    }

    private void updateUiState() {
        boolean isWidgetEnabled = Addons.isWidgetEnabled(this);

        findViewById(R.id.box_buy).setVisibility(isWidgetEnabled ? View.GONE : View.VISIBLE);
        findViewById(R.id.box_thanks).setVisibility(isWidgetEnabled ? View.VISIBLE : View.GONE);

        setWidgetConfigurationResult();
    }

    private void setWidgetConfigurationResult() {
        boolean isWidgetEnabled = Addons.isWidgetEnabled(this);

        Intent intent = new Intent();
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);

        setResult(isWidgetEnabled ? RESULT_OK : RESULT_CANCELED, intent);
    }

}
