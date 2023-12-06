package com.swaarm.sdkxmple;

import static java.util.Optional.*;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.swaarm.sdk.SwaarmAnalytics;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BillingActivity extends Activity {

    private static final String LOG_TAG = "SW_BILLING";

    private static final String IN_APP_PRODUCT_ID = "123";
    private static final String SUBSCRIPTION_PRODUCT_ID = "111";

    private final List<ProductDetails> products = new ArrayList<>();
    private String eventId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing);

        BillingClient billingClient = BillingClient.newBuilder(this)
                .setListener((billingResult, purchases) -> {
                    try {
                        processBillingChange(billingResult, purchases);
                    } catch (Exception e) {
                        setStatus("Error: " + e.getMessage());
                    }
                })
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d(LOG_TAG, "Disconnected");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    products.clear();
                    loadProducts(billingClient, BillingClient.ProductType.INAPP, IN_APP_PRODUCT_ID)
                            .thenAccept(products::addAll)
                            .thenCompose(s -> loadProducts(billingClient, BillingClient.ProductType.SUBS, SUBSCRIPTION_PRODUCT_ID))
                            .thenAccept(products::addAll)
                            .thenAccept((v) -> setStatus("Products loaded: " + products.size()));
                }
            }
        });

        findViewById(com.swaarm.sdkxmple.R.id.purchaseProduct).setOnClickListener(arg0 ->
                handlePurchase(billingClient, IN_APP_PRODUCT_ID)
        );

        findViewById(R.id.purchaseSubscription).setOnClickListener(arg0 ->
                handlePurchase(billingClient, SUBSCRIPTION_PRODUCT_ID)
        );
    }

    private void processBillingChange(BillingResult billingResult, List<Purchase> purchases) {
        if (isPurchaseCompleted(billingResult, purchases)) {
            for (Purchase purchase : purchases) {
                String productId = purchase.getProducts().get(0);
                findProduct(productId).flatMap(this::findProductPrice)
                        .ifPresent(money -> {
                            SwaarmAnalytics.purchase(
                                    eventId,
                                    money.getAmount(),
                                    money.getCurrency(),
                                    productId,
                                    purchase.getPurchaseToken(),
                                    ""
                            );
                            setStatus("Purchase success, purchase event sent");
                        });
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(LOG_TAG, "User canceled");
        } else {
            String msg = "Purchase failed. code:" + billingResult.getResponseCode() + " (" + billingResult.getDebugMessage() + ")";
            Log.d(LOG_TAG, msg);
            setStatus(msg);
        }
    }

    private boolean isPurchaseCompleted(BillingResult billingResult, List<Purchase> purchases) {
        return billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null && !purchases.isEmpty();
    }

    private Optional<Money> findProductPrice(ProductDetails productDetails) {
        List<ProductDetails.SubscriptionOfferDetails> subscription = productDetails.getSubscriptionOfferDetails();
        if (subscription != null && !subscription.isEmpty()) {
            ProductDetails.PricingPhase pricePhase = subscription.get(0).getPricingPhases().getPricingPhaseList().get(0);
            return of(
                    new Money(
                            pricePhase.getPriceCurrencyCode(),
                            extractPrice(pricePhase.getPriceCurrencyCode(), pricePhase.getFormattedPrice())
                    )
            );
        }

        ProductDetails.OneTimePurchaseOfferDetails product = productDetails.getOneTimePurchaseOfferDetails();
        if (product != null) {
            return of(
                    new Money(
                            product.getPriceCurrencyCode(),
                            extractPrice(product.getPriceCurrencyCode(), product.getFormattedPrice())
                    )
            );
        }
        return empty();
    }

    private Double extractPrice(String currencyCode, String formattedPrice) {
        String currencySymbol = Currency.getInstance(currencyCode).getSymbol();
        return Double.valueOf(formattedPrice.replace(currencySymbol, "").trim());
    }

    private void handlePurchase(BillingClient billingClient, String subscriptionProductId) {
        if (!isBillingReady(billingClient)) {
            setStatus("Billing not initialized");
            return;
        }
        eventId = ((EditText) findViewById(R.id.eventId)).getText().toString();
        findProduct(subscriptionProductId).ifPresent(product ->
                launchPurchaseFlow(product, billingClient)
        );
    }

    private void setStatus(String text) {
        ((TextView) findViewById(R.id.billingStatus)).setText(text);
    }

    private boolean isBillingReady(BillingClient billingClient) {
        return billingClient.isReady() && !products.isEmpty();
    }

    private void launchPurchaseFlow(
            ProductDetails productDetails,
            BillingClient billingClient
    ) {

        List<BillingFlowParams.ProductDetailsParams> details = new ArrayList<>();
        BillingFlowParams.ProductDetailsParams.Builder productDetailsParam = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails);

        findOfferToken(productDetails).ifPresent(productDetailsParam::setOfferToken);
        details.add(productDetailsParam.build());

        BillingFlowParams params = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(details)
                .build();
        billingClient.launchBillingFlow(this, params);
    }

    private Optional<String> findOfferToken(ProductDetails productDetails) {
        if (productDetails.getSubscriptionOfferDetails() == null) {
            return empty();
        }
        for (ProductDetails.SubscriptionOfferDetails offerDetails : productDetails.getSubscriptionOfferDetails()) {
            return of(offerDetails.getOfferToken());
        }
        return empty();
    }

    public Optional<ProductDetails> findProduct(String id) {
        for (ProductDetails product : products) {
            if (product.getProductId().equals(id)) {
                return of(product);
            }
        }
        return empty();
    }

    private CompletableFuture<List<ProductDetails>> loadProducts(
            BillingClient billingClient,
            String type,
            String id
    ) {
        List<QueryProductDetailsParams.Product> productQuery = new ArrayList<>();
        productQuery.add(QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId(id)
                .setProductType(type).build()
        );
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(productQuery)
                .build();

        CompletableFuture<List<ProductDetails>> future = new CompletableFuture<>();
        billingClient.queryProductDetailsAsync(
                params,
                (billingResult, productDetails) -> future.complete(productDetails)
        );

        return future;
    }

    private static class Money {
        private String currency;
        private Double amount;

        public Money(String currency, Double amount) {
            this.currency = currency;
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public Double getAmount() {
            return amount;
        }
    }
}