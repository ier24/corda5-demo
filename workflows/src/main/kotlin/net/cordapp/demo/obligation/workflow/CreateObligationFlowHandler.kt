package net.cordapp.demo.obligation.workflow

import net.corda.v5.application.flows.CordaInject
import net.corda.v5.application.flows.FlowEngine
import net.corda.v5.application.flows.InitiatedBy
import net.corda.v5.application.flows.ResponderFlow
import net.corda.v5.application.flows.SubFlow
import net.corda.v5.application.messaging.FlowSession
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.base.util.contextLogger
import net.corda.v5.ledger.utxo.UtxoLedgerService

class CreateObligationFlowHandler(private val session: FlowSession) : SubFlow<Unit> {

    private companion object {
        val log = contextLogger()
    }

    @CordaInject
    private lateinit var utxoLedgerService: UtxoLedgerService

    @Suspendable
    override fun call() {
        try {
            val finalizedSignedTransaction = utxoLedgerService.receiveFinality(session) {}
            log.info("Finalised tx: $finalizedSignedTransaction.id")
        } catch (e: Exception) {
            log.warn("Exceptionally finished responder flow", e)
        }
    }

    @InitiatedBy(CreateObligationFlow.FLOW_PROTOCOL)
    class Handler : ResponderFlow {

        @CordaInject
        private lateinit var flowEngine: FlowEngine

        @Suspendable
        override fun call(session: FlowSession) {

            val createObligationFlowHandler = CreateObligationFlowHandler(session)

            flowEngine.subFlow(createObligationFlowHandler)
        }
    }
}