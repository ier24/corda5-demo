package com.r3.developers.apples.contracts

import com.r3.developers.apples.states.AppleStamp
import com.r3.developers.apples.states.BasketOfApples
import net.corda.v5.ledger.utxo.Contract
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction

class BasketOfApplesContract : Contract {
    override fun verify(transaction: UtxoLedgerTransaction) {
        when (val command = transaction.commands.first()) {
            is AppleCommands.PackBasket -> verifyPackBasket(transaction)
            is AppleCommands.Redeem -> verifyRedeem(transaction)
            else -> {
                throw IllegalArgumentException("Incorrect type of AppleStamp commands: ${command::class.java.name}")
            }
        }
    }

    private fun verifyPackBasket(transaction: UtxoLedgerTransaction) {
        val output = transaction.getOutputStates(BasketOfApples::class.java).first()
        require(transaction.outputContractStates.size == 1) {
            "This transaction should only output one BasketOfApples state"
        }
        require(output.description.isNotBlank()) {
            "The output of the BasketOfApples state should have a clear description of the apple product"
        }
        require(output.weight > 0) {
            "The output of the BasketOfApples state should have a non-zero weight"
        }
    }

    private fun verifyRedeem(transaction: UtxoLedgerTransaction) {
        require(transaction.inputContractStates.size == 2) {
            "This transaction should consume two states"
        }

        val stampInputs = transaction.getInputStates(AppleStamp::class.java)
        val basketInputs = transaction.getInputStates(BasketOfApples::class.java)

        require(stampInputs.isNotEmpty() && basketInputs.isNotEmpty()) {
            "This transaction should have exactly one AppleStamp and one BasketOfApples input state"
        }
        require(stampInputs.single().issuer == basketInputs.single().farm) {
            "The issuer of the Apple stamp should be the producing farm of this basket of apple"
        }
        require(basketInputs.single().weight > 0) {
            "The basket of apple has to weigh more than 0"
        }
    }
}
