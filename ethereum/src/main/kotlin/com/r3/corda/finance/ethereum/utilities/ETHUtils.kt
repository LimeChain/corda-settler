package com.r3.corda.finance.ethereum.utilities

import com.r3.corda.finance.obligation.types.DigitalCurrency
import com.r3.corda.finance.ethereum.types.TransactionInfoResponse
import com.ripple.core.coretypes.hash.Hash256
import net.corda.core.contracts.Amount
import net.corda.core.crypto.SecureHash
import net.corda.finance.AMOUNT
import com.ripple.core.coretypes.Amount as ETHAmount

fun TransactionInfoResponse.hasSucceeded() = status == "success" && validated

fun Amount<*>.toETHAmount(): ETHAmount = ETHAmount.fromString(quantity.toString())

fun Int.toETHAmount(): ETHAmount = ETHAmount.fromString(toString())

fun SecureHash.toETHHash(): Hash256 = Hash256.fromHex(toString())

val DEFAULT_ETH_FEE = ETHAmount.fromString("1000")

@JvmField
val ETH: DigitalCurrency = DigitalCurrency.getInstance("ETH")

fun ETHERS(amount: Int): Amount<DigitalCurrency> = AMOUNT(amount, ETH)
fun ETHERS(amount: Long): Amount<DigitalCurrency> = AMOUNT(amount, ETH)
fun ETHERS(amount: Double): Amount<DigitalCurrency> = AMOUNT(amount, ETH)

val Int.ETH: Amount<DigitalCurrency> get() = ETHERS(this)
val Long.ETH: Amount<DigitalCurrency> get() = ETHERS(this)
val Double.ETH: Amount<DigitalCurrency> get() = ETHERS(this)