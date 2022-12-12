package su.gachi.services

import su.gachi.Config
import java.util.*

class LocaleService {
    private val bundles = mutableMapOf<String, ResourceBundle>()

    init {
        Config.locales.forEach { locale ->
            bundles[locale] = ResourceBundle.getBundle("Translations", Locale(locale))
        }
    }

    fun translate(phrase: String, locale: String): String {
        return try {
            bundles[locale]!!.getString(phrase)
        } catch(err: Exception) {
            defaultTranslate(phrase)
        }
    }

    private fun defaultTranslate(phrase: String): String {
        return try {
            bundles[Config.defaultLocale]!!.getString(phrase)
        } catch(err: Exception) {
            "NO_LOCALE: $phrase"
        }
    }
}