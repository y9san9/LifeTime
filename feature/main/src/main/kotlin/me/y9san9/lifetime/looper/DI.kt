package me.y9san9.lifetime.looper

import app.meetacy.di.DI
import app.meetacy.di.builder.DIBuilder
import app.meetacy.di.dependency.Dependency
import me.y9san9.lifetime.looper.TimeLooper

val DI.looper: TimeLooper by Dependency
