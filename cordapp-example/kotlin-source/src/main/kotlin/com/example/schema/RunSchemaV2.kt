package com.example.schema

import com.example.model.RunModel
import com.example.state.RunState
import net.corda.core.crypto.entropyToKeyPair
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.serialization.CordaSerializable
import java.math.BigInteger
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.persistence.*

/**
 * The family of schemas for IOUState.
 */
object RunSchema

/**
 * An RunState schema.
 */
/**
 * First version of a cash contract ORM schema that maps all fields of the [Cash] contract state as it stood
 * at the time of writing.
 */
@CordaSerializable
object RunSchemaV2 : MappedSchema(schemaFamily = RunSchema.javaClass, version = 1, mappedTypes = listOf(RunStateV2::class.java)) {
    @Entity
    @Table(name = "RUN_DETAILS")
    class RunStateV2(
            /** X500Name of owner party **/
            @Column(name = "runID",nullable = false,length = 100)
            var runID:String,
            @Column(name = "responseID",nullable = false,length = 100)
            var responseID:String,
            @Column(name = "skuId",nullable = false,length = 100)
            var skuId:String,
            @Column(name = "runnerID",nullable = false,length = 100)
            var runnerID:String,
            @Column(name = "runnerName",nullable = false,length = 100)
            var runnerName:String,
            @Column(name = "storeName",nullable = false,length = 100)
            var storeName:String,
            @Column(name = "runnerPosition",nullable = false,length = 100)
            var runnerPosition:String,
            @Column(name = "statusChangeTimeStamp",length = 100)
            var requestTime: LocalDateTime,
            @Column(name = "sender",length = 100)
            var sender:String,
            @Column(name = "recepient",nullable = false,length = 100)
            var recepient:String


    ) : PersistentState(){constructor() : this(runID="",responseID = "",skuId = "",runnerID = "",runnerName = "",storeName = "",runnerPosition = "",requestTime = LocalDateTime.MIN,sender = "", recepient = ""   )}


}