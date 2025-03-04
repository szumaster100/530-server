package core.auth

import core.ServerConstants
import core.storage.AccountStorageProvider
import core.storage.InMemoryStorageProvider
import core.storage.SQLStorageProvider

object Auth {
    lateinit var authenticator: AuthProvider<*>

    lateinit var storageProvider: AccountStorageProvider

    fun configure() {
        storageProvider =
            if (ServerConstants.PERSIST_ACCOUNTS) {
                SQLStorageProvider()
            } else {
                // Use in-memory storage if persistence is not required.
                InMemoryStorageProvider()
            }

        // Initialize the authenticator.
        authenticator =
            if (ServerConstants.USE_AUTH) {
                ProductionAuthenticator().also { it.configureFor(storageProvider) }
            } else {
                DevelopmentAuthenticator().also { it.configureFor(storageProvider) }
            }
    }
}
