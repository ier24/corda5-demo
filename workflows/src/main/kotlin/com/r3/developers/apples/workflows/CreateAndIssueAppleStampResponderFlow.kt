package com.r3.developers.apples.workflows

import net.corda.v5.application.flows.CordaInject
import net.corda.v5.application.flows.InitiatedBy
import net.corda.v5.application.flows.ResponderFlow
import net.corda.v5.application.messaging.FlowSession
import net.corda.v5.base.annotations.Suspendable
import net.corda.v5.ledger.utxo.UtxoLedgerService

/**
 * Corda5ではinitiate flowとresponder flowのペアであることをprotocolの文字列を使用して判断する
 */
@InitiatedBy(protocol = "create-and-issue-apple-stamp")
class CreateAndIssueAppleStampResponderFlow : ResponderFlow {
    @CordaInject
    lateinit var utxoLedgerService: UtxoLedgerService

    @Suspendable
    override fun call(session: FlowSession) {
        utxoLedgerService.receiveFinality(session) { transaction ->
            /**
             * 署名前にContractレベルの検証は行われる。
             * ここでは署名前に必要な追加のバリデーションロジックを記述する。
             * 例：価格が高すぎる場合に拒否するなど。
             */
        }
    }
}