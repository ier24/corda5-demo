package com.r3.developers.apples.states

import com.r3.developers.apples.contracts.BasketOfApplesContract
import java.security.PublicKey
import net.corda.v5.ledger.utxo.BelongsToContract
import net.corda.v5.ledger.utxo.ContractState

@BelongsToContract(BasketOfApplesContract::class)
class BasketOfApples(
    private val description: String,
    private val farm: PublicKey,
    private val owner: PublicKey,
    private val weight: Int,
    private val participants: List<PublicKey>
) : ContractState {
    override fun getParticipants(): List<PublicKey> = participants

    fun changeOwner(buyer: PublicKey): BasketOfApples {
        val participants = listOf(farm, buyer)
        return BasketOfApples(description, farm, buyer, weight, participants)
    }
}