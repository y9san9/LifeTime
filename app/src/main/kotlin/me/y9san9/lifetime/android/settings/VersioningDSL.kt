package me.y9san9.lifetime.android.settings

class UpgradeContext(
    private val settings: MainSettings
) {
    var version: Int
        get() = settings.loadVersion()
        set(value) = settings.saveVersion(value)

    inline fun case(
        value: Int,
        result: Int = value + 1,
        block: () -> Unit
    ) {
        if (version == value) {
            block()
            version = result
        }
    }
}

inline fun MainSettings.upgrade(
    block: UpgradeContext.() -> Unit
) = UpgradeContext(this).apply(block)
