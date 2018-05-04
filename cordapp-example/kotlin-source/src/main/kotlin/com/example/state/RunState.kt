package com.example.state

/**
 * Created by cordadev1 on 4/4/2018.
 */
import com.example.model.RunModel
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




public data class RunState(
        val runModel: RunModel,
        val sender: Party,
        val recipient: Party,
        override val linearId: UniqueIdentifier = UniqueIdentifier()): LinearState,QueryableState {



    /** The public keys of the involved parties. */
    override val participants: List<AbstractParty> get() = listOf(sender).distinct() + recipient

    /** Tells the vault to track a state if we are one of the parties involved. */
    //fun isRelevant(ourKeys: Set<PublicKey>) = ourKeys.intersect(participants.flatMap { it.owningKey.keys }).isNotEmpty()

    public override fun generateMappedObject(schema: MappedSchema) = RunState.RunSchemaV1.RunEntity(this)

    public override fun supportedSchemas(): Iterable<MappedSchema> = listOf(RunState.RunSchemaV1)

    public object RunSchemaV1 : MappedSchema(RunState::class.java,1, listOf(RunState.RunSchemaV1.RunEntity::class.java)){

        @Entity
        @Table(name ="RUN_DETAILS"
                //uniqueConstraints = arrayOf(UniqueConstraint(columnNames = arrayOf("requestDd")))
        )
        class RunEntity(runState: RunState) : PersistentState(){

            @Column(name = "runID",nullable = false,length = 100)
            var runID:String = runState.runModel.runID
            @Column(name = "responseID",nullable = false,length = 100)
            var responseID:String= runState.runModel.responseID
            @Column(name = "skuId",nullable = false,length = 100)
            var skuId:String= runState.runModel.skuID
            @Column(name = "runnerID",nullable = false,length = 100)
            var runnerID:String = runState.runModel.runnerID
            @Column(name = "runnerName",nullable = false,length = 100)
            var runnerName:String= runState.runModel.runnerName
            @Column(name = "storeName",nullable = false,length = 100)
            var storeName:String= runState.runModel.storeName
            @Column(name = "runnerPosition",nullable = false,length = 100)
            var runnerPosition:String= runState.runModel.runnerPosition
            @Column(name = "statusChangeTimeStamp",length = 100)
            var requestTime:LocalDateTime= runState.runModel.statusChangeTimeStamp
            @Column(name = "sender",length = 100)
            var sender:String?= runState.sender.name.toString()
            @Column(name = "recepient",nullable = false,length = 100)
            var recepient:String?= runState.recipient.toString()




            //dummy constructor
            constructor() : this(RunState(RunModel("01","AM08RED","test","test","test","test","test",
                    LocalDateTime.now()),
                    Party(CordaX500Name("Nike", "London", "GB"),entropyToKeyPair(BigInteger.valueOf(60)).public),
                    Party(CordaX500Name("Nike", "London", "GB"),entropyToKeyPair(BigInteger.valueOf(60)).public)
            ))




        }
    }
}