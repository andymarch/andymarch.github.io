package uk.co.andymarch.blogposter.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Holds the GitHub personal access token in an AES-encrypted SharedPreferences
 * file backed by a key in the Android Keystore. The token never leaves the
 * device except as an `Authorization` header on requests to api.github.com.
 */
class TokenStore(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun clearToken() {
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    fun hasToken(): Boolean = !getToken().isNullOrBlank()

    companion object {
        // Must match the exclusion rules in xml/backup_rules.xml and data_extraction_rules.xml.
        private const val PREFS_FILE_NAME = "secret_shared_prefs"
        private const val KEY_TOKEN = "github_token"
    }
}
