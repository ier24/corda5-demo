package com.r3.developers.apples.contracts

import net.corda.v5.ledger.utxo.Contract
import net.corda.v5.ledger.utxo.transaction.UtxoLedgerTransaction

class AppleStampContract : Contract {
    override fun verify(transaction: UtxoLedgerTransaction) {
        TODO("Not yet implemented")
    }

}
