package com.r3.corda.finance.ethereum.types

import com.r3.corda.finance.obligation.types.OffLedgerPayment
import com.r3.corda.finance.ethereum.flows.MakeEthPayment
import net.corda.core.identity.Party

/**
 * Terms specific to settling with ETH. In this case, parties must agree on:
 * - which ethereum address the payment must be made to
 * - which servers should be used to check the payment was successful
 *
 * The terms can be updated with:
 * - the hash of the ethereum transaction when the ethereum payment is submitted
 * - a payment settlementStatus
 */
data class EthSettlement(
        override val accountToPay: String,
        override val settlementOracle: Party,
        override val paymentFlow: Class<MakeEthPayment<*>> = MakeEthPayment::class.java
) : OffLedgerPayment<MakeEthPayment<*>> {
    override fun toString(): String {
        return "Pay ETH address $accountToPay and use $settlementOracle as settlement Oracle."
    }
}