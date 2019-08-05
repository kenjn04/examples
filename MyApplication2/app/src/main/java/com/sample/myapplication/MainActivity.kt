package com.sample.myapplication

import android.accounts.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        account()
    }

    private fun account() {
        val am = AccountManager.get(this)
        val accounts = am.getAccountsByType("com.google")
        Log.d("aaabbbccc", "${accounts.size}")
        for (account in accounts) {
            Log.d("aaabbbccc", "${account}")
        }
        am.addAccount("com.google", null, null, null, this,
            AccountManagerCallback<Bundle> { future ->
                try {
                    val bundle = future.result
                    val accountName = bundle.getString(AccountManager.KEY_ACCOUNT_NAME)
                    Log.d("aaabbbccc2", "${accountName}")
                    val accounts = am.accounts
                    Log.d("aaabbbccc", "${accounts.size}")
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: AuthenticatorException) {
                    e.printStackTrace()
                }
            }, null
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("aaabbbccc3", "")

    }
}
