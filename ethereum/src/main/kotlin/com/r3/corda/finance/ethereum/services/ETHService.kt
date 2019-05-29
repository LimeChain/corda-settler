package com.r3.corda.finance.ethereum.services

import com.typesafe.config.ConfigException
import net.corda.core.node.AppServiceHub
import net.corda.core.node.services.CordaService
import net.corda.core.serialization.SingletonSerializeAsToken

/** Provides access to a read/write ETH client, which can make and sign payment transactions. */
@CordaService
class ETHService(val services: AppServiceHub) : SingletonSerializeAsToken() {
    // Config file defaulted to this name.
    private val configFileName = "eth.conf"
    val client: ETHClientForPayment by lazy {
        try {
            ETHClientForPayment(configFileName)
        } catch (e: ConfigException) {
            throw IllegalArgumentException(e)
        }
    }
}