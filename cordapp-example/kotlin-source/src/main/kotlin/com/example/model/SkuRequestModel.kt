package com.example.model

/**
 * Created by cordadev1 on 4/4/2018.
 */
import net.corda.core.serialization.CordaSerializable
import java.time.LocalDateTime

@CordaSerializable
data class SkuRequestModel (val requestID:String,
                            val responseID : String,
                            val skuID:String,
                            val itemAvailability : String,
                            val requestTime: LocalDateTime)