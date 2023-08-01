package com.r3.developers.apples.workflows

import com.r3.developers.apples.contracts.AppleCommands
import com.r3.developers.apples.states.BasketOfApples
import java.time.Instant
import java.time.temporal.ChronoUnit
import net.corda.v5.application.flows.ClientRequestBody
import net.corda.v5.application.flows.ClientStartableFlow
import net.corda.v5.application.flows.CordaInject
import net.corda.v5.application.marshalling.JsonMarshallingService
import net.corda.v5.application.membership.MemberLookup
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.ledger.common.NotaryLookup
import net.corda.v5.ledger.utxo.UtxoLedgerService

class PackageApplesFlow : ClientStartableFlow {

    @CordaInject
    lateinit var jsonMarshallingService: JsonMarshallingService

    @CordaInject
    lateinit var memberLookup: MemberLookup

    @CordaInject
    lateinit var notaryLookup: NotaryLookup

    @CordaInject
    lateinit var utxoLedgerService: UtxoLedgerService

    private data class PackApplesRequest(
        val appleDescription: String,
        val weight: Int
    )

    @Suspendable
    override fun call(requestBody: ClientRequestBody): String {
        val request = requestBody.getRequestBodyAs(
            jsonMarshallingService,
            PackApplesRequest::class.java
        )
        val appleDescription = request.appleDescription
        val weight = request.weight
        val notaryInfo = notaryLookup.notaryServices.single()
        val myKey = memberLookup.myInfo().ledgerKeys.first()

        val basket = BasketOfApples(
            description = appleDescription,
            farm = myKey,
            owner = myKey,
            weight = weight,
            participants = listOf(myKey)
        )

        val transaction = utxoLedgerService.createTransactionBuilder()
            .setNotary(notaryInfo.name)
            .addOutputState(basket)
            .addCommand(AppleCommands.PackBasket())
            .addSignatories(myKey)
            .setTimeWindowUntil(Instant.now().plus(1, ChronoUnit.DAYS))
            .toSignedTransaction()

        return try {
            utxoLedgerService.finalize(transaction, listOf()).toString()
        } catch (e: Exception) {
            "Flow failed, message: ${e.message}"
        }
    }
}