package com.r3.corda.finance.ethereum.services

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory

import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.tx.Transfer
import org.web3j.utils.Convert

import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java8.util.Optional


open class ETHClient(configFileName: String) {

    private val config: Config = ConfigFactory.parseResources(configFileName)

    val infuraUrl: String get() = config.getString("infuraUrl")
    val sender: String get() = config.getString("sender")
    val walletFile: String get() = config.getString("walletFile")
    val walletPassword: String get() = config.getString("walletPassword")
    val jsonString: String get() = config.getString("walletJson")

    val web3j = Web3j.build(HttpService(infuraUrl))

    fun getBalance(account: String): BigInteger {
        val ethGetBalance: EthGetBalance = web3j
                .ethGetBalance(account, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get()

        return ethGetBalance.getBalance()
    }

    fun sendEth(recipient: String, amount: String): String {
        val weiAmount: BigInteger = Convert.toWei(amount, Convert.Unit.GWEI).toBigInteger()

        val credentials: Credentials = WalletUtils.loadCredentials(walletPassword, walletFile)

        val transactionReceipt: TransactionReceipt = Transfer
                .sendFunds(web3j, credentials, recipient, BigDecimal(weiAmount), Convert.Unit.WEI)
                .send()

        return transactionReceipt.transactionHash
    }

    fun getTransaction(txHash: String): Optional<TransactionReceipt> {

        val ethGetTransactionReceipt: EthGetTransactionReceipt = web3j
                .ethGetTransactionReceipt(txHash)
                .sendAsync()
                .get()

        return ethGetTransactionReceipt.getTransactionReceipt()
    }

}