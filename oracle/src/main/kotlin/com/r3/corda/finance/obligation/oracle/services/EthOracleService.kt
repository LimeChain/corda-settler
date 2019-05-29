package com.r3.corda.finance.obligation.oracle.services

import com.r3.corda.finance.obligation.types.DigitalCurrency
import com.r3.corda.finance.obligation.oracle.flows.VerifySettlement
import com.r3.corda.finance.obligation.states.Obligation
import com.r3.corda.finance.ethereum.services.ETHClientForVerification
import com.r3.corda.finance.ethereum.types.TransactionNotFoundException
import com.r3.corda.finance.ethereum.types.EthPayment
import com.r3.corda.finance.ethereum.utilities.hasSucceeded
import com.r3.corda.finance.ethereum.utilities.toETHAmount
import com.typesafe.config.ConfigFactory
import net.corda.core.crypto.SecureHash
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import java.net.URI

@CordaService
class EthOracleService(val services: AppServiceHub) : SingletonSerializeAsToken() {

    private val configFileName = "eth.conf"
    private val nodes by lazy { ConfigFactory.parseResources(configFileName).getStringList("nodes").mapNotNull(::URI) }

    private val clientsForVerification = nodes.map { nodeUri -> ETHClientForVerification(nodeUri) }

    /** Check that the last ledger sequence has not passed. */
    private fun isPastLastLedger(payment: EthPayment<DigitalCurrency>): Boolean {
        return clientsForVerification.all { client ->
            client.ledgerIndex().ledgerCurrentIndex > payment.lastLedgerSequence
        }
    }

    private fun checkServersAreUpToDate(): Boolean {
        return clientsForVerification.all { client ->
            val serverState = client.serverState().state.serverState
            serverState in setOf("tracking", "full", "validating", "proposing")
        }
    }

    private fun checkObligeeReceivedPayment(
            ethPayment: EthPayment<DigitalCurrency>,
            obligation: Obligation<DigitalCurrency>
    ): Boolean {
        // Query all the ethereum nodes.
        val results = clientsForVerification.map { client ->
            try {
                client.transaction(ethPayment.paymentReference)
            } catch (e: TransactionNotFoundException) {
                // The transaction is not recognised by the Oracle.
                return false
            }
        }
        // All nodes should report the same result.
        val destinationCorrect = results.all { it.destination == obligation.settlementMethod?.accountToPay }
        val amountCorrect = results.all { it.amount == ethPayment.amount.toETHAmount() }
        val referenceCorrect = results.all { it.invoiceId == SecureHash.sha256(obligation.linearId.id.toString()).toString() }
        val hasSucceeded = results.all { it.hasSucceeded() }
        return destinationCorrect && amountCorrect && referenceCorrect && hasSucceeded
    }

    fun hasPaymentSettled(
            ethPayment: EthPayment<DigitalCurrency>,
            obligation: Obligation<DigitalCurrency>
    ): VerifySettlement.VerifyResult {
        val upToDate = checkServersAreUpToDate()

        if (!upToDate) {
            return VerifySettlement.VerifyResult.PENDING
        }

        val isPastLastLedger = isPastLastLedger(ethPayment)
        val receivedPayment = checkObligeeReceivedPayment(ethPayment, obligation)

        return when {
            // Payment received. Boom!
            receivedPayment && !isPastLastLedger -> VerifySettlement.VerifyResult.SUCCESS
            // Return success even if the deadline is passed.
            receivedPayment && isPastLastLedger -> VerifySettlement.VerifyResult.SUCCESS
            // Payment not received. Maybe the reference is wrong or it was sent to the wrong address.
            // This situation will need to be sorted out manually for now...
            !receivedPayment && isPastLastLedger -> VerifySettlement.VerifyResult.TIMEOUT
            // If the deadline is not yet up then we are still pending.
            !receivedPayment && !isPastLastLedger -> VerifySettlement.VerifyResult.PENDING
            else -> throw IllegalStateException("Shouldn't happen!")
        }
    }
}
