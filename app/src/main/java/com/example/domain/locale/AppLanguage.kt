package com.example.domain.locale

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    BANGLA("bn", "Bangla", "বাংলা"),
    ENGLISH("en", "English", "English");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: BANGLA
        }
    }
}
