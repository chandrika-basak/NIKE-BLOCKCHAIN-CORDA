package com.example.flow

import co.paralleluniverse.fibers.Suspendable


import com.example.contract.SkuTransferContract
import com.example.state.SkuRequestState
import net.corda.core.contracts.Command
import net.corda.core.contracts.StateAndContract
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.core.utilities.ProgressTracker.Step
import java.lang.Boolean.FALSE
import java.lang.Boolean.TRUE

import net.corda.core.utilities.unwrap


/**
 * Created by cordadev1 on 4/4/2018.
 */

/**
 * This flow allows network participant to broadcast request for an SKU.
 * Request details are present in [SkuRequestState]
 *
 * In our flow, the [Acceptor] always accepts a valid request.
 *
 * All methods called within the [FlowLogic] sub-class need to be annotated with the @Suspendable annotation.
 */
object ResponseLedgerWriteFlow {
    @InitiatingFlow
    @StartableByRPC
    class Initiator(val skuRequestState: SkuRequestState) : FlowLogic<SignedTransaction>() {
        /**
         * The progress tracker checkpoints each stage of the flow and outputs the specified messages when each
         * checkpoint is reached in the code. See the 'progressTracker.currentStep' expressions within the call() function.
         */
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

        /**
         * The flow logic is encapsulated within the call() method.
         */
        @Suspendable
        override fun call():SignedTransaction  {
            System.out.println("***** Inside SKU ledger writing  Flow Initiator I am :"+serviceHub.myInfo.legalIdentities.first())
            logger.info("***** Inside SKU ledger writing flow Flow Initiator I am :"+serviceHub.myInfo.legalIdentities.first())
            // Obtain a reference to the notary we want to use.
            val notary = serviceHub.networkMapCache.notaryIdentities[0]

            val signList = listOf<Party>(serviceHub.myInfo.legalIdentities.first())+ skuRequestState.sender

            // Stage 1.
            progressTracker.currentStep = GENERATING_TRANSACTION
            // Generate an unsigned transaction.

              val txCommand = Command(SkuTransferContract.Commands.SkuRequestCommand(), signList.map { it.owningKey })
            val txBuilder = TransactionBuilder(notary).withItems(StateAndContract(skuRequestState, SkuTransferContract.SKU_TRANSFER_CONTRACT_ID), txCommand)


            // Stage 2.
            progressTracker.currentStep = VERIFYING_TRANSACTION
            // Verify that the transaction is valid.
            txBuilder.verify(serviceHub)

            // Stage 3.
            progressTracker.currentStep = SIGNING_TRANSACTION
            // Sign the transaction.
            val partSignedTx = serviceHub.signInitialTransaction(txBuilder)


            // Stage 4.


                val otherPartyFlow = initiateFlow(skuRequestState.sender)

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
                    System.out.println("***** Inside SKU ledger writing  Flow  Acceptor *** when I am :"+serviceHub.myInfo.legalIdentities.first())                    //val output = stx.tx.outputs.single().data
                    logger.info("***** Inside SKU ledger writing  Flow Flow Acceptor *** when I am :"+serviceHub.myInfo.legalIdentities.first())                    //val output = stx.tx.outputs.single().data

                    val outputs = stx.tx.outputs
                    for(output in outputs) {
                        "This must be a Sku Request transaction." using (output.data is SkuRequestState)
                    }
                }
            }

            return subFlow(signTransactionFlow)
        }
    }
}

