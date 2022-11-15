package com.buy.together.util

import java.util.regex.Pattern

object RegularExpression {

    enum class Regex {
        NAME, BIRTH, PHONE, ID, PWD, NICKNAME
    }

    fun validCheck(regex: Regex, value: String): Boolean {
        val pattern = Pattern.compile(getRegex(regex))
        val matcher = pattern.matcher(value)
        return matcher.matches()
    }

    private fun getRegex(regex: Regex): String =
        when (regex) {
            Regex.NAME -> "^[가-힣]{2,10}|[a-zA-Z]{2,10}\\s[a-zA-Z]{2,10}$"
            Regex.BIRTH -> "\\d{8}"
            Regex.PHONE -> "^\\s*(010)(-|\\)|\\s)*(\\d{4,4})(-|\\s)*(\\d{4})\\s*$"
            Regex.ID -> "^[a-zA-Z0-9]{4,12}$"
            Regex.PWD -> "^[a-zA-Z0-9]{8,22}$"
            Regex.NICKNAME -> "^[가-힣]{2,8}|[a-zA-Z]{2,8}\\s[a-zA-Z]{2,8}$"
        }
}
