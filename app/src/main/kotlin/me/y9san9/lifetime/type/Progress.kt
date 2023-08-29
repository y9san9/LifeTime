package me.y9san9.lifetime.type

@JvmInline
value class Progress(val int: Int) {
    init {
        require(int in MinInt..MaxInt) { "Progress $int is not in range ${Min.int}..${Max.int}" }
    }

    val float: Float get() = int.toFloat() / 100

    companion object {
        private const val MinInt = 0
        private const val MaxInt = 100

        val Min = Progress(MinInt)
        val Max = Progress(MaxInt)
    }
}

// https://en.wikipedia.org/wiki/Finite_field_arithmetic#Addition_and_subtraction

infix fun Progress.galoisPlus(int: Int): Progress {
    val resultInt = this.int + int
    if (resultInt > 100) {
        return Progress(resultInt % 100)
    }
    return Progress(resultInt)
}

infix fun Progress.galoisPlus(progress: Progress): Progress =
    galoisPlus(progress.int)
