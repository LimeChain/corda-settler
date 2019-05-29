package com.r3.corda.finance.ethereum.types

/** Exceptions. */

open class EthereumRpcException : Exception()

class AccountNotFoundException : EthereumRpcException() {
    override fun toString() = "Account ID not found."
}

class TransactionNotFoundException : EthereumRpcException() {
    override fun toString() = "Transaction ID not found."
}

class IncorrectSequenceNumberException : EthereumRpcException() {
    override fun toString() = "The sequence number is incorrect. " +
            "It is likely that the same transaction has been submitted twice."
}

class InsufficientBalanceException : EthereumRpcException() {
    override fun toString() = "You don't have enough ETH to make the payment!"
}

class AlreadysubmittedException : EthereumRpcException() {
    override fun toString() = "The transaction has already been submitted."
}

class PaymentToSelfException : EthereumRpcException() {
    override fun toString() = "The payment is being made from and to the same account."
}