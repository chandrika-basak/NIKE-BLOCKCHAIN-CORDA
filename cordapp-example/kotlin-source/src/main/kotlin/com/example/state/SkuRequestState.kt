package com.example.state

/**
 * Created by cordadev1 on 4/4/2018.
 */
import com.example.model.SkuRequestModel
import net.corda.core.contracts.LinearState
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.crypto.entropyToKeyPair
import net.corda.core.identity.AbstractParty
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import net.corda.core.schemas.QueryableState
import java.math.BigInteger
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table




data class SkuRequestState(
        val skuRequestModel: SkuRequestModel,
        val sender: Party,
        val recipient: Party,
        override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState,QueryableState {



    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf(sender).distinct() + recipient

    /** Tells the vault to track a state if we are one of the parties involved. */
    //fun isRelevant(ourKeys: Set<PublicKey>) = ourKeys.intersect(participants.flatMap { it.owningKey.keys }).isNotEmpty()

    override fun generateMappedObject(schema: MappedSchema) = SkuRequestState.SkuReqSchemaV1.SkuReqEntity(this)

    override fun supportedSchemas(): Iterable<MappedSchema> = listOf(SkuRequestState.SkuReqSchemaV1)

    object SkuReqSchemaV1 : MappedSchema(SkuRequestState::class.java,1, listOf(SkuRequestState.SkuReqSchemaV1.SkuReqEntity::class.java)){

        @Entity
        @Table(name ="SKU_MARKETPLACE_AVAILABILITY"
                //uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("requestDd")))
        )
        class SkuReqEntity(skurequestState: SkuRequestState) : PersistentState(){

            @Column(name = "responseID",nullable = false,length = 100)
            var responseID:String = skurequestState.skuRequestModel.responseID
            @Column(name = "requestID",nullable = false,length = 100)
            var requestId:String = skurequestState.skuRequestModel.requestID
            @Column(name = "skuID",nullable = false,length = 100)
            var skuId:String= skurequestState.skuRequestModel.skuID
            @Column(name = "itemAvailability",nullable = false,length = 100)
            var itemStatus:String= skurequestState.skuRequestModel.itemAvailability
            @Column(name = "requestTime",length = 100)
            var requestTime:LocalDateTime= skurequestState.skuRequestModel.requestTime
            @Column(name = "sender",length = 100)
            var sender:String?= skurequestState.sender.name.toString()
            @Column(name = "recepient",nullable = false,length = 100)
            var recepient:String?= skurequestState.recipient.toString()




            //dummy constructor
            constructor() : this(SkuRequestState(SkuRequestModel("01","AM08RED","abc","xyz",
                    LocalDateTime.now()),
                    Party(CordaX500Name("Nike", "London", "GB"),entropyToKeyPair(BigInteger.valueOf(60)).public),
                    Party(CordaX500Name("Nike", "London", "GB"),entropyToKeyPair(BigInteger.valueOf(60)).public)
                    ))




        }
    }
}