package me.y9san9.lifetime.core.annotation

@RequiresOptIn(
    level = RequiresOptIn.Level.WARNING,
    message = "This constructor is not safe for usage. Try to use other factory functions"
)
@Retention(AnnotationRetention.BINARY)
annotation class UnsafeConstructor
