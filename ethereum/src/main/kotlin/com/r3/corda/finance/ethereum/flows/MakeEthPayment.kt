package com.r3.corda.finance.ethereum.flows

import co.paralleluniverse.fibers.Suspendable
import com.r3.corda.finance.obligation.USD
import com.r3.corda.finance.obligation.types.Money
import com.r3.corda.finance.obligation.client.flows.MakeOffLedgerPayment
import com.r3.corda.finance.obligation.states.Obligation
import com.r3.corda.finance.obligation.types.OffLedgerPayment
import com.r3.corda.finance.obligation.types.PaymentStatus
import com.r3.corda.finance.ethereum.services.ETHService
import com.r3.corda.finance.ethereum.types.*
import com.ripple.core.coretypes.uint.UInt32
import net.corda.core.contracts.Amount
import net.corda.core.contracts.StateAndRef
import net.corda.core.crypto.SecureHash
import net.corda.core.flows.FlowException
import net.corda.core.utilities.ProgressTracker
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration


class MakeEthPayment<T : Money>(
        amount: Amount<T>,
        obligationStateAndRef: StateAndRef<Obligation<*>>,
        settlementMethod: OffLedgerPayment<*>,
        progressTracker: ProgressTracker
) : MakeOffLedgerPayment<T>(amount, obligationStateAndRef, settlementMethod, progressTracker) {

    @Suspendable
    override fun setup() {

    }

    override fun checkBalance(requiredAmount: Amount<*>) {

        // get ETHService client
        val ethClient = serviceHub.cordaService(ETHService::class.java).client

        // check the balance on the supplied ETHService address
        val balance = ethClient.getBalance(ethClient.sender)

        // account must contain at least requiredAmount
        check(balance > BigInteger(requiredAmount.quantity.toString())) {
            "You do not have enough ETH to make the payment. Needed: $requiredAmount, " +
                    "available: $balance"
        }

    }

    @Suspendable
    override fun makePayment(obligation: Obligation<*>, amount: Amount<T>): EthPayment<T> {

        // get ETHService client
        val ethClient = serviceHub.cordaService(ETHService::class.java).client

        val recipient = obligation.settlementMethod?.accountToPay.toString()
        val amountToSend = amount.quantity.toString()

        // trigger ETH transfer
        val txHash = ethClient.sendEth(recipient, amountToSend)

        // return the payment
        return EthPayment(txHash, amount, PaymentStatus.SENT)
    }

}