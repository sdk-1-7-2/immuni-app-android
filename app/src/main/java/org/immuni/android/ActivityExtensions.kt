package org.immuni.android

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import android.widget.Toast
import androidx.annotation.StringRes
import org.immuni.android.util.ProgressDialogFragment

fun toast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(ImmuniApplication.appContext, message, length).show()
}

fun toast(@StringRes title: Int, length: Int = Toast.LENGTH_SHORT) {
    toast(ImmuniApplication.appContext.resources.getString(title), length)
}

fun FragmentActivity.loading(loading: Boolean) {
    if(loading) {
        val dialogFragment = ProgressDialogFragment()
        val ft = supportFragmentManager.beginTransaction()

        val prev = supportFragmentManager.findFragmentByTag("loading_dialog")
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        dialogFragment.show(ft, "loading_dialog")
    } else {
        val ft = supportFragmentManager.beginTransaction()

        val prev = supportFragmentManager.findFragmentByTag("loading_dialog")
        if (prev != null) {
            (prev as? DialogFragment)?.dismiss()
            ft.remove(prev)
        }
    }
}