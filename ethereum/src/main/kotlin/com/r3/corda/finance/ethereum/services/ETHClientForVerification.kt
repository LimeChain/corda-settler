package com.r3.corda.finance.ethereum.services

import java.net.URI

/** Whoever is verifying will specify the server they want to use. */
class ETHClientForVerification(override val nodeUri: URI) : ReadOnlyETHClient