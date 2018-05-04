package com.example.contract

import com.example.state.IOUState
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.requireSingleCommand
import net.corda.core.contracts.requireThat
import net.corda.core.transactions.LedgerTransaction

/**
 * A implementation of a basic smart contract in Corda.

 */
open class SkuTransferContract : Contract {
    companion object {
        @JvmStatic
        val SKU_TRANSFER_CONTRACT_ID = "com.example.contract.SkuTransferContract"
    }

    /**
     * The verify() function of all the states' contracts must not throw an exception for a transaction to be
     * considered valid.
     */
    override fun verify(tx: LedgerTransaction) {
        val command = tx.getCommand<CommandData>(0)
        requireThat {
            // Generic constraints around the IOU transaction.
            //"No inputs should be consumed when issuing an IOU." using (tx.inputs.isEmpty())
            // "Only one output state should be created." using (tx.outputs.size == 1)
            //val out = tx.outputsOfType<IOUState>().single()
            //"The lender and the borrower cannot be the same entity." using (out.lender != out.borrower)
            //"All of the participants must be signers." using (command.signers.containsAll(out.participants.map { it.owningKey }))

            // IOU-specific constraints.
            // "The IOU's value must be non-negative." using (out.value > 0)
        }
    }

    /**
     * This contract only implements one command, Create.
     */
    interface Commands : CommandData {
        class SkuRequestCommand : Commands
        class SkuResponseCommand : Commands
        class ResponseLedgerWriteCommand : Commands
        class  PaymentUpdationCommand : Commands
        class InvoiceCommand :Commands
        class SkuResponseStatusChange: Commands
        class InitiateRun : Commands
        class ModifyRun :Commands



    }
}
