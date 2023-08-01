package com.r3.developers.apples.contracts

import com.r3.developers.apples.contracts.AppleCommands.Issue
import com.r3.developers.apples.contracts.AppleCommands.Redeem
import com.r3.developers.apples.states.AppleStamp
import net.corda.v5.ledger.utxo.Contract
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction

class AppleStampContract : Contract {
    /**
     * the verify method is automatically triggered when your transaction is executed.
     * It verifies that the transaction components are following the restrictions implemented inside the contract’s verify method.
     */
    override fun verify(transaction: UtxoLedgerTransaction) {
        when (val command = transaction.commands.first()) {
            is Issue -> verifyIssue(transaction)
            is Redeem -> verifyRedeem(transaction)
            else -> {
                // Unrecognised Command type
                throw IllegalArgumentException("Incorrect type of AppleStamp commands: ${command::class.java.name}")
            }
        }
    }

    private fun verifyIssue(transaction: UtxoLedgerTransaction) {
        val output = transaction.getOutputStates(AppleStamp::class.java).first()
        require(transaction.outputContractStates.size == 1) {
            "This transaction should only have one AppleStamp state as output"
        }
        require(output.stampDesc.isNotBlank()) {
            "The output AppleStamp state should have clear description of the type of redeemable goods"
        }
    }

    private fun verifyRedeem(transaction: UtxoLedgerTransaction) {
        val input = transaction.getInputStates(AppleStamp::class.java)
        require(input.size == 1) {
            "This transaction should only have one AppleStamp state as input"
        }
        require(transaction.signatories.contains(input.first().owner)) {
            "The owner of the input AppleStamp state must be a signatory to the transaction"
        }
    }

}
