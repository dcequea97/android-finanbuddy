package com.example.finanbuddy.utils.ext

fun Double.format(decimals: Int) = "%.${decimals}f".format(this)
