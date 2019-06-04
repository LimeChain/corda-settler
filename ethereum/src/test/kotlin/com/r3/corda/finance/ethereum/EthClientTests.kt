//package com.r3.corda.finance.ripple

//import org.junit.Test
//import java.math.BigDecimal
//import java.net.URI
//import kotlin.test.assertEquals
//import kotlin.test.assertFailsWith
//import net.corda.core.contracts.Amount as CordaAmount
//
//class TestXRPClient(override val nodeUri: URI, override val secret: String, override val address: AccountID) : ReadWriteXRPClient
//
//import java.math.BigDecimal
//import java.math.BigInteger
//
//import org.web3j.protocol.Web3j
//import org.web3j.protocol.http.HttpService
//import org.web3j.protocol.core.methods.response.EthGetBalance
//import org.web3j.protocol.core.methods.response.TransactionReceipt
//import org.web3j.protocol.core.DefaultBlockParameterName
//
//import org.web3j.tx.Transfer
//import org.web3j.utils.Convert
//
//import org.web3j.crypto.Credentials
//import org.web3j.crypto.WalletUtils
//
//import java.io.BufferedWriter
//import java.io.File
//import java.io.FileWriter


//class EthClientTests {

//    val password = "Lime1234Chain5678"
//    val jsonString = "{\"version\":3,\"id\":\"ecb51768-8564-498a-bb11-3a5a5c8dc0bb\",\"address\":\"2bafc482bd227dfd5ba250521a00be3a4cc88bbd\",\"crypto\":{\"ciphertext\":\"e0511415792dfa7221ba1b8f32b8ec98e1410f45e612e2100df1aceddfdb22bd\",\"cipherparams\":{\"iv\":\"7ffa2af08f502c63d57e62440ad77539\"},\"cipher\":\"aes-128-ctr\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"salt\":\"8051a5df1c02eb3eba81d2920fbb84b76b948a1248bbba62ffff684e733948cf\",\"n\":131072,\"r\":8,\"p\":1},\"mac\":\"be23fe0e261ba38892581d80afd0c86563748377b5cc702b6ed3285a13cceff6\"}}"
//    val recipient = "0x49DcF6C5513475e21369c02BF149d1e4CcDD09Cb"
//    val web3j = Web3j.build(HttpService("https://ropsten.infura.io/e7a6b9997e804bc6a91b8c8d6f1fd7d1"))

//    companion object {
//        @JvmStatic
//        private val nodeUri = URI("http://s.altnet.rippletest.net:51234")
//        // Credentials for an account on the XRPService test net.
//        @JvmStatic
//        private val client = TestXRPClient(
//                nodeUri = nodeUri,
//                secret = "ssn8cYYksFFexYq97sw9UnvLnMKgh",
//                address = AccountID.fromString("rNmkj4AtjEHJh3D9hMRC4rS3CXQ9mX4S4b")
//        )
//    }
//
//
//    @Test
//    fun `get account balance`() {
//        println("=======================")
//        println("||   CHECK BALANCE   ||")
//        println("=======================")
//
//        val ethGetBalance: EthGetBalance = web3j.ethGetBalance(recipient, DefaultBlockParameterName.LATEST).sendAsync().get();
//
//        val weiBalance: BigInteger = ethGetBalance.getBalance()
//        println("WEI: " + weiBalance)
//
//        val ethBalance: BigDecimal = Convert.fromWei(weiBalance.toString(), Convert.Unit.ETHER)
//        println("ETHERS: " + ethBalance)
//    }
//
//    @Test
//    fun `send funds to ethereum address`() {
//        println("=======================")
//        println("||     SEND FUNDS    ||")
//        println("=======================")
//
//        val amount: BigInteger = Convert.toWei("1", Convert.Unit.GWEI).toBigInteger()
//        println("amount: " + amount)
//
//        val tempWalletFile: File = File("file.tmp")
//        val writer: BufferedWriter = BufferedWriter(FileWriter(tempWalletFile))
//        writer.write(jsonString)
//        writer.close()
//        tempWalletFile.deleteOnExit()
//
//        val credentials: Credentials = WalletUtils.loadCredentials(password, tempWalletFile)
//        val transactionReceipt: TransactionReceipt = Transfer.sendFunds(web3j, credentials, recipient, BigDecimal(amount), Convert.Unit.WEI).send()
//
//        println("txHash: " + transactionReceipt.transactionHash)
//    }
//
//
//    @Test
//    fun `get account info`() {
//        val client = XRPClientForVerification(nodeUri = nodeUri)
//        val accountId = AccountID.fromString("r3kmLJN5D28dHuH8vZNUZpMC43pEHpaocV")
//        client.accountInfo(accountId)
//    }
//
//    private fun createAndSignTx(sequenceNumber: UInt32): SignedTransaction {
//        val payment = client.createPayment(
//                sequence = sequenceNumber,
//                source = AccountID.fromString("rNmkj4AtjEHJh3D9hMRC4rS3CXQ9mX4S4b"),
//                destination = AccountID.fromString("ra6mzL1Xy9aN5eRdjzn9CHTMwcczG1uMpN"),
//                amount = Amount.fromString("10000"),
//                fee = Amount.fromString("1000"),
//                linearId = Hash256.fromHex("B55A46422EC5BD69F21BF6C365EC86091D3C3DF73D4004A0A27FFDD6D719F8E5")
//        )
//        println(payment.amount())
//        return client.signPayment(payment)
//    }
//
//    @Test
//    fun `create, sign and submit payment successfully`() {
//        val sequenceNumber = client.nextSequenceNumber(AccountID.fromString("rNmkj4AtjEHJh3D9hMRC4rS3CXQ9mX4S4b"))
//        val signedTransaction = createAndSignTx(sequenceNumber)
//        println(client.submitTransaction(signedTransaction))
//    }
//
//    @Test
//    fun `create, sign and submit payment with incorrect sequence number`() {
//        val signedTransaction = createAndSignTx(UInt32(1))
//        assertFailsWith<IncorrectSequenceNumberException> { client.submitTransaction(signedTransaction) }
//    }
//
//    @Test
//    fun `get transaction info for valid transaction id`() {
//        println(client.transaction("06B7AE6CF95A6181E14635383247FB379428309F02A8279D6FD38BA268F89F12"))
//    }
//
//    @Test
//    fun `get transaction info for invalid transaction id`() {
//        assertFailsWith<TransactionNotFoundException> {
//            client.transaction("8921B02CE76A711594601B7DD7D52FB126EBED2109FCC1979346373F26406114")
//        }
//    }
//
//    @Test
//    fun `check server state`() {
//        println(client.serverState())
//    }
//
//    @Test
//    fun `get ledger current index`() {
//        println(client.ledgerIndex())
//    }
//
//    @Test
//    fun `corda to ripple amount`() {
//        val oneDrop = CordaAmount.fromDecimal(BigDecimal("0.000001"), XRP)
//        var cordaAmount = CordaAmount.zero(XRP)
//        (1..1000000).forEach { cordaAmount += oneDrop }
//        val xrpAmount = cordaAmount.toXRPAmount()
//        val normalisedCordaAmount = cordaAmount.displayTokenSize * BigDecimal.valueOf(cordaAmount.quantity)
//        assertEquals(normalisedCordaAmount.toLong(), xrpAmount.toLong())
//    }
//
//}*/
