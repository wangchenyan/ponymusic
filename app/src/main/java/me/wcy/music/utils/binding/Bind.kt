package me.wcy.music.utils.binding

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bind(val value: Int)