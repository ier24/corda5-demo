package com.r3.developers.apples.states

import com.r3.developers.apples.contracts.AppleStampContract
import java.security.PublicKey
import java.util.UUID
import net.corda.v5.ledger.utxo.BelongsToContract
import net.corda.v5.ledger.utxo.ContractState

@BelongsToContract(AppleStampContract::class)
class AppleStamp(
    val id: UUID,
    val stampDesc: String,
    val issuer: PublicKey,
    val owner: PublicKey,
    /**
     * The PublicKeys referred to in the participants property are the ledgerKeys of the participants expected to store the states.
     * https://docs.r3.com/en/platform/corda/5.0/developing-applications/basic-cordapp/state.html
     */
    private val participants: List<PublicKey>
) : ContractState {
    override fun getParticipants(): List<PublicKey> = participants
}