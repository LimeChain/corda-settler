package com.r3.corda.finance.obligation.oracle.services

import com.r3.corda.finance.obligation.types.DigitalCurrency
import com.r3.corda.finance.obligation.oracle.flows.VerifySettlement
import com.r3.corda.finance.obligation.states.Obligation
import com.r3.corda.finance.ethereum.types.EthPayment
import com.typesafe.config.ConfigFactory
import net.corda.core.crypto.SecureHash
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken
import java.net.URI

@CordaService
class EthOracleService(val services: AppServiceHub) : SingletonSerializeAsToken() {

    private val configFileName = "eth.conf"

    private fun checkObligeeReceivedPayment(
            ethPayment: EthPayment<DigitalCurrency>,
            obligation: Obligation<DigitalCurrency>
    ): Boolean {
//        val ethClient = serviceHub.cordaService(ETHService::class.java).client
//        val result = ethClient.transaction(ethPayment.paymentReference)
//
//        val destinationCorrect = results.all { it.destination == obligation.settlementMethod?.accountToPay }
//        val amountCorrect = results.all { it.amount == ethPayment.amount.toETHAmount() }
//        val referenceCorrect = results.all { it.invoiceId == SecureHash.sha256(obligation.linearId.id.toString()).toString() }
//        val hasSucceeded = results.all { it.hasSucceeded() }
//        return destinationCorrect && amountCorrect && referenceCorrect && hasSucceeded
        return true
    }

    fun hasPaymentSettled(
            ethPayment: EthPayment<DigitalCurrency>,
            obligation: Obligation<DigitalCurrency>
    ): VerifySettlement.VerifyResult {

        val receivedPayment = checkObligeeReceivedPayment(ethPayment, obligation)

//        return when {
//            receivedPayment -> VerifySettlement.VerifyResult.SUCCESS
//            !receivedPayment -> VerifySettlement.VerifyResult.TIMEOUT
//            else -> throw IllegalStateException("Shouldn't happen!")
//        }

        return VerifySettlement.VerifyResult.SUCCESS

    }
}
