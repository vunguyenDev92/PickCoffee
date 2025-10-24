@file:Suppress("ktlint:standard:import-ordering")

package com.example.androidmycoffee.data.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {
    private var billingClient: BillingClient? = null
    private val _purchaseState = MutableStateFlow<PurchaseState>(PurchaseState.Loading)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                scope.launch {
                    handlePurchase(purchase)
                }
            }
        } else {
            Log.w("BillingManager", "Purchase failed or cancelled. Code: ${billingResult.responseCode}")
            _purchaseState.value = PurchaseState.NotPurchased
        }
    }

    fun initialize() {
        val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
            .enableOneTimeProducts()
            .build()

        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(pendingPurchasesParams)
            .build()

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d("BillingManager", "BillingClient setup finished.")
                    queryPurchases()
                } else {
                    Log.e("BillingManager", "BillingClient setup failed: ${billingResult.debugMessage}")
                    _purchaseState.value = PurchaseState.NotPurchased
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w("BillingManager", "BillingClient service disconnected.")
                _purchaseState.value = PurchaseState.NotPurchased
            }
        })
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        Log.d("BillingManager", "launchPurchaseFlow called with productId: $productId")

        if (billingClient?.isReady == false) {
            Log.e("BillingManager", "BillingClient is not ready.")
            return
        }

        Log.d("BillingManager", "BillingClient is ready, creating product list")

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        Log.d("BillingManager", "Starting queryProductDetailsAsync")

        scope.launch {
            billingClient?.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
                Log.d("BillingManager", "queryProductDetailsAsync callback - ResponseCode: ${billingResult.responseCode}")

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetailsList = queryProductDetailsResult.productDetailsList

                    Log.d("BillingManager", "Product details list size: ${productDetailsList?.size ?: 0}")

                    if (productDetailsList != null && productDetailsList.isNotEmpty()) {
                        val productDetails = productDetailsList[0]
                        Log.d("BillingManager", "Product found: ${productDetails.productId}")

                        val productDetailsParamsList = listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build(),
                        )

                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                        scope.launch(Dispatchers.Main) {
                            Log.d("BillingManager", "Launching billing flow on Main thread")
                            val launchResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
                            Log.d("BillingManager", "Billing flow launched - ResponseCode: ${launchResult?.responseCode}")
                        }
                    } else {
                        Log.e("BillingManager", "Product details list is empty")
                    }

                    val unfetchedProductList = queryProductDetailsResult.unfetchedProductList
                    if (unfetchedProductList != null && unfetchedProductList.isNotEmpty()) {
                        for (unfetchedProduct in unfetchedProductList) {
                            Log.w("BillingManager", "UnFetched product: ${unfetchedProduct.productId}, Type: ${unfetchedProduct.productType}")
                        }
                    }
                } else {
                    Log.e("BillingManager", "Failed to query product details. Code: ${billingResult.responseCode}, Message: ${billingResult.debugMessage}")
                }
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                scope.launch {
                    billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            Log.d("BillingManager", "Purchase acknowledged successfully.")
                            _purchaseState.value = PurchaseState.Purchased
                        } else {
                            Log.e("BillingManager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                        }
                    }
                }
            } else {
                _purchaseState.value = PurchaseState.Purchased
            }
        }
    }

    private fun queryPurchases() {
        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        scope.launch {
            billingClient?.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasPremium = purchases.any { it.products.contains(PREMIUM_PRODUCT_ID) }
                    _purchaseState.value = if (hasPremium) PurchaseState.Purchased else PurchaseState.NotPurchased
                    if (hasPremium) Log.d("BillingManager", "User has premium.")
                } else {
                    Log.e("BillingManager", "Failed to query purchases: ${billingResult.debugMessage}")
                    _purchaseState.value = PurchaseState.NotPurchased
                }
            }
        }
    }

    fun restorePurchases() {
        queryPurchases()
    }

    fun destroy() {
        billingClient?.endConnection()
        billingClient = null
    }

    companion object {
        const val PREMIUM_PRODUCT_ID = "android.test.purchased"
    }
}

sealed class PurchaseState {
    object Loading : PurchaseState()
    object NotPurchased : PurchaseState()
    object Purchased : PurchaseState()
}
