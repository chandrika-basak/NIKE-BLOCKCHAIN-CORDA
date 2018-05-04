package com.example.model

import net.corda.core.serialization.CordaSerializable
import java.time.LocalDateTime

/**
 * Created by cordadev1 on 4/16/2018.
 */
@CordaSerializable
data class RunModel (var runID:String,
                     val responseID:String,
                     var skuID:String,
                     val runnerID:String,
                     val runnerName:String,
                     var storeName:String,
                     var runnerPosition:String,
                     var statusChangeTimeStamp: LocalDateTime
    )