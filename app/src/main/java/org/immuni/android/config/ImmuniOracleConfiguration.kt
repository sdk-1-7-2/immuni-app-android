package org.immuni.android.config

import android.content.Context
import android.content.Intent
import org.immuni.android.ui.forceupdate.ForceUpdateActivity
import com.bendingspoons.concierge.ConciergeManager
import com.bendingspoons.oracle.OracleConfiguration
import com.bendingspoons.sesame.Sesame
import okhttp3.CertificatePinner
import org.immuni.android.ImmuniApplication
import org.immuni.android.R
import org.immuni.android.util.log
import org.koin.core.KoinComponent
import org.koin.core.inject

class ImmuniOracleConfiguration(val context: Context) : OracleConfiguration, KoinComponent {

    val concierge: ConciergeManager by inject()
    private val sesame = Sesame(OracleSesameConfiguration())

    override fun endpoint(): String {
        return context.getString(R.string.oracle_base_url)
    }

    override fun concierge(): ConciergeManager {
        return concierge
    }

    override fun showForceUpdate(minVersionCode: Int) {
        log("ForceUpdate! Min version is $minVersionCode")
        // avoid to open the activity while the app is in background
        if(ImmuniApplication.lifecycleObserver.isInForeground &&
            !ForceUpdateActivity.isOpen) {
            val context =
                ImmuniApplication.appContext
            val intent = Intent(context, ForceUpdateActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            context.startActivity(intent)
        }
    }

    override fun sesame(): Sesame? {
        return sesame
    }

    override fun certificatePinner(): CertificatePinner? {
        return CertificatePinner.Builder()
            .add(
            "*.ascolto-onlus.org",
            "sha256/OgRj8cQiFRmMxno/YNLtTKsHIJfP+EacJiMfoRfpLe8="
            ).build()
    }

    override fun encryptStore() = false
}