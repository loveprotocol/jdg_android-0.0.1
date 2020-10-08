package com.imhc2.plumboard

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

class KotlinTest {

    @Test
    fun test() {
        var done = getBirth("650991600")
        println(done)
    }

    fun getBirth(birth: String): String? {
        val t = java.lang.Long.parseLong(birth + "000")
        val simpleDate = SimpleDateFormat("yyyyMMdd", Locale.KOREA)
        simpleDate.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        return simpleDate.format(t)
    }

    @Test
    fun subStringTest() {
        var a: Int? = 123
        var s: Any? = when (a) {
            in 10..200 -> when (a) {
                in 100..150 -> println("유흐?")
                else -> ""
            }
            is Int -> println("a는 Int 타입")
            123 -> println("a는 123임")
            else -> null

        }
    }

    @Test
    fun decimalTest(){
        val data = 0.014
        val d = data * 100
        println(d)
        if (d == Math.floor(d)) {
            println(d.toInt().toString() + "%")
        } else {
            println(d.toString() + "%")
        }
    }


}