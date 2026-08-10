package com.stockcomp.user

/**
 * A trusted identity from an external provider.
 *
 * The provider and external subject ID form the stable account key. A verified email is bootstrap data only.
 */
data class ExternalIdentity(
    val provider: IdentityProvider,
    val externalSubjectId: String,
    val verifiedEmail: String? = null,
)

enum class IdentityProvider {
    GOOGLE,
}
