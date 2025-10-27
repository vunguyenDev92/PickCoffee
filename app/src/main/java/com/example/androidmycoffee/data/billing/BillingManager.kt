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

    private var isInitialized = false

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                scope.launch {
                    handlePurchase(purchase)
                }
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "User canceled the purchase")
            // Don't change state to NotPurchased, keep current state
        } else {
            Log.w(TAG, "Purchase failed or cancelled. Code: ${billingResult.responseCode}")
            // Only set to NotPurchased if we haven't verified any purchases yet
            if (_purchaseState.value is PurchaseState.Loading ||
                _purchaseState.value is PurchaseState.Purchasing
            ) {
                queryPurchases() // Re-check instead of assuming NotPurchased
            }
        }
    }

    fun initialize() {
        if (isInitialized) {
            Log.d(TAG, "BillingManager already initialized")
            return
        }

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
                    Log.d(TAG, "BillingClient setup finished.")
                    isInitialized = true
                    // Automatically query purchases after setup
                    queryPurchases()
                } else {
                    Log.e(TAG, "BillingClient setup failed: ${billingResult.debugMessage}")
                    _purchaseState.value = PurchaseState.NotPurchased
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "BillingClient service disconnected.")
                isInitialized = false
                // Try to reconnect
                scope.launch {
                    kotlinx.coroutines.delay(3000)
                    if (!isInitialized) {
                        initialize()
                    }
                }
            }
        })
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        Log.d(TAG, "launchPurchaseFlow called with productId: $productId")
        _purchaseState.value = PurchaseState.Purchasing(productId)

        if (billingClient?.isReady == false) {
            Log.e(TAG, "BillingClient is not ready.")
            _purchaseState.value = PurchaseState.Error("Billing service not ready")
            return
        }

        Log.d(TAG, "BillingClient is ready, creating product list")

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        Log.d(TAG, "Starting queryProductDetailsAsync")

        scope.launch {
            billingClient?.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
                Log.d(TAG, "queryProductDetailsAsync callback - ResponseCode: ${billingResult.responseCode}")

                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val productDetailsList = queryProductDetailsResult.productDetailsList

                    Log.d(TAG, "Product details list size: ${productDetailsList.size}")

                    if (productDetailsList.isNotEmpty()) {
                        val productDetails = productDetailsList[0]
                        Log.d(TAG, "Product found: ${productDetails.productId}")

                        val productDetailsParamsList = listOf(
                            BillingFlowParams.ProductDetailsParams.newBuilder()
                                .setProductDetails(productDetails)
                                .build(),
                        )

                        val billingFlowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(productDetailsParamsList)
                            .build()

                        scope.launch(Dispatchers.Main) {
                            Log.d(TAG, "Launching billing flow on Main thread")
                            val launchResult = billingClient?.launchBillingFlow(activity, billingFlowParams)
                            Log.d(TAG, "Billing flow launched - ResponseCode: ${launchResult?.responseCode}")
                        }
                    } else {
                        Log.e(TAG, "Product details list is empty")
                        _purchaseState.value = PurchaseState.Error("Product not found")
                    }

                    val unfetchedProductList = queryProductDetailsResult.unfetchedProductList
                    if (unfetchedProductList.isNotEmpty()) {
                        for (unfetchedProduct in unfetchedProductList) {
                            Log.w(TAG, "UnFetched product: ${unfetchedProduct.productId}, Type: ${unfetchedProduct.productType}")
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to query product details. Code: ${billingResult.responseCode}, Message: ${billingResult.debugMessage}")
                    _purchaseState.value = PurchaseState.Error("Failed to load product details")
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
                            Log.d(TAG, "Purchase acknowledged successfully.")
                            _purchaseState.value = PurchaseState.Purchased
                        } else {
                            Log.e(TAG, "Failed to acknowledge purchase: ${billingResult.debugMessage}")
                        }
                    }
                }
            } else {
                _purchaseState.value = PurchaseState.Purchased
            }
        }
    }

    private fun queryPurchases() {
        if (billingClient?.isReady != true) {
            Log.w(TAG, "Cannot query purchases, billing client not ready")
            return
        }

        val queryPurchasesParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        scope.launch {
            billingClient?.queryPurchasesAsync(queryPurchasesParams) { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasPremium = purchases.any {
                        it.products.contains(PREMIUM_PRODUCT_ID) &&
                            it.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                    _purchaseState.value = if (hasPremium) {
                        Log.d(TAG, "User has premium.")
                        PurchaseState.Purchased
                    } else {
                        Log.d(TAG, "User does not have premium.")
                        PurchaseState.NotPurchased
                    }
                } else {
                    Log.e(TAG, "Failed to query purchases: ${billingResult.debugMessage}")
                    // Don't change state if query fails, keep current state
                }
            }
        }
    }

    fun restorePurchases() {
        Log.d(TAG, "Manually restoring purchases")
        queryPurchases()
    }

    fun destroy() {
        billingClient?.endConnection()
        billingClient = null
        isInitialized = false
    }

    // android.test.canceled -> sửa lại nếu muốn xóa premium
    companion object {
        const val PREMIUM_PRODUCT_ID = "android.test.purchased"
        private const val TAG = "BillingManager"
    }
}

sealed class PurchaseState {
    object Loading : PurchaseState()
    object NotPurchased : PurchaseState()
    object Purchased : PurchaseState()
    data class Purchasing(val productId: String) : PurchaseState()
    data class Error(val message: String) : PurchaseState()
}
