/*
package com.example.flow

import co.paralleluniverse.fibers.Suspendable


import com.example.contract.SkuTransferContract
import com.example.model.SkuRequestModel
import com.example.model.SkuResponseModel
import com.example.state.SkuRequestState
import com.example.state.SkuResponseState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndContract
import net.corda.core.contracts.TransactionState
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.node.services.Vault
import net.corda.core.node.services.queryBy
import net.corda.core.node.services.vault.QueryCriteria
import net.corda.core.node.services.vault.builder
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

import net.corda.core.utilities.unwrap
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


*/
/**
 * Created by cordadev1 on 4/4/2018.
 *//*


*/
/**
 * This flow allows network participant to broadcast request for an SKU.
 * Request details are present in [SkuResponseState]
 *
 * In our flow, the [Acceptor] always accepts a valid request.
 *
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 *//*

object SkuBlockConfirmationFlow {
    @InitiatingFlow
    @StartableByRPC
    class Initiator(val responseID: String, val status : String) : FlowLogic<SignedTransaction>() {
        */
/**
         * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
         * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
         *//*

        companion object {
            object GENERATING_TRANSACTION : Step("Generating transaction based on new IOU.")
            object VERIFYING_TRANSACTION : Step("Verifying contract constraints.")
            object SIGNING_TRANSACTION : Step("Signing transaction with our private key.")
            object GATHERING_SIGS : Step("Gathering the counterparty's signature.") {
                override fun childProgressTracker() = CollectSignaturesFlow.tracker()
            }

            object FINALISING_TRANSACTION : Step("Obtaining notary signature and recording transaction.") {
                override fun childProgressTracker() = FinalityFlow.tracker()
            }

            fun tracker() = ProgressTracker(
                    GENERATING_TRANSACTION,
                    VERIFYING_TRANSACTION,
                    SIGNING_TRANSACTION,
                    GATHERING_SIGS,
                    FINALISING_TRANSACTION
            )
        }

        override val progressTracker = tracker()

        */
/**
         * The flow logic is encapsulated within the call() method.
         *//*

        @Suspendable
        override fun call():SignedTransaction  {
            System.out.println("***** Inside SKU ledger writing  Flow Initiator I am :"+serviceHub.myInfo.legalIdentities.first())
            logger.info("***** Inside SKU ledger writing flow Flow Initiator I am :"+serviceHub.myInfo.legalIdentities.first())
            // Obtain a reference to the notary we want to use.
            val notary = serviceHub.networkMapCache.notaryIdentities[0]

            val exp1 = builder { SkuResponseState.SkuResSchemaV1.SkuResEntity::responseID.equal(responseID) }
            val skuResponseCriteria = QueryCriteria.VaultCustomQueryCriteria(exp1)
            val unconsumedCriteria = QueryCriteria.VaultQueryCriteria(Vault.StateStatus.UNCONSUMED)

            val unconsumedSkuResponseStateAndRef = serviceHub.vaultService.queryBy<SkuResponseState>(skuResponseCriteria.and(unconsumedCriteria)).states.singleOrNull()
            val recipient = unconsumedSkuResponseStateAndRef!!.state.data.sender
            val signList = listOf<Party>(serviceHub.myInfo.legalIdentities.first())+ recipient
            val skuResponseModel = unconsumedSkuResponseStateAndRef.state.data.skuResponseModel
            skuResponseModel.status = status

            // Stage 1.
            progressTracker.currentStep = GENERATING_TRANSACTION
            // Generate an unsigned transaction.
            val outSkuResponseState = SkuResponseState(skuResponseModel,serviceHub.myInfo.legalIdentities.first(),recipient)
            val txCommand = Command(SkuTransferContract.Commands.SkuResponseStatusChange(), signList.map { it.owningKey })
            val txBuilder = TransactionBuilder(notary).withItems(unconsumedSkuResponseStateAndRef,StateAndContract(outSkuResponseState, SkuTransferContract.SKU_TRANSFER_CONTRACT_ID), txCommand)






            // Stage 2.
            progressTracker.currentStep = VERIFYING_TRANSACTION
            // Verify that the transaction is valid.
            txBuilder.verify(serviceHub)

            // Stage 3.
            progressTracker.currentStep = SIGNING_TRANSACTION
            // Sign the transaction.
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)


            // Stage 4.
            val otherPartyFlow = initiateFlow(recipient)

            progressTracker.currentStep = GATHERING_SIGS
            // Send the state to the counterparty, and receive it back with their signature.
            val fullySignedTx = subFlow(CollectSignaturesFlow(partSignedTx, setOf(otherPartyFlow) , GATHERING_SIGS.childProgressTracker()))
            progressTracker.currentStep = FINALISING_TRANSACTION
            // Notarise and record the transaction in both parties' vaults.
            // Stage 5.
            val txn= subFlow(FinalityFlow(fullySignedTx, FINALISING_TRANSACTION.childProgressTracker()))

            System.out.println("***** Request Complete for txn Id :"+txn.tx.id)
            logger.info("***** Request Complete for txn Id :"+txn.tx.id)

            return txn




        }
    }

    @InitiatedBy(Initiator::class)
    class Acceptor(val otherPartyFlow: FlowSession) : FlowLogic<SignedTransaction>() {
        @Suspendable
        override fun call(): SignedTransaction {

            val signTransactionFlow = object : SignTransactionFlow(otherPartyFlow) {
                override fun checkTransaction(stx: SignedTransaction) = requireThat {
                    System.out.println("***** Inside Response State Change  Flow  Acceptor *** when I am :" + serviceHub.myInfo.legalIdentities.first())                    //val output = stx.tx.outputs.single().data
                    logger.info("***** Inside SKU ledger writing  Flow Flow Acceptor *** when I am :" + serviceHub.myInfo.legalIdentities.first())
                    val data = (stx.tx.outputs[0].data) as SkuResponseState

                        val url1 = "http://localhost:7080/nike/inventory/updateInventory/" + data.skuResponseModel.skuID
                        val extURL = URL(url1)

                        with(extURL.openConnection() as HttpURLConnection) {
                            // optional default is GET
                            requestMethod = "GET"


                            println("\nSending 'GET' request to URL : $url1")
                            println("Response Code : $responseCode")

                            BufferedReader(InputStreamReader(inputStream)).use {
                                val response = StringBuffer()

                                var inputLine = it.readLine()
                                while (inputLine != null) {
                                    response.append(inputLine)
                                    inputLine = it.readLine()
                                }
                                println(response.toString())

                            }

                    }
                    //val output = stx.tx.outputs.single().data

                    val outputs = stx.tx.outputs
                    for(output in outputs) {
                        "This must be a Sku Request transaction." using (output.data is SkuResponseState)
                    }
                }
            }

            return subFlow(signTransactionFlow)
        }
    }
}


*/
