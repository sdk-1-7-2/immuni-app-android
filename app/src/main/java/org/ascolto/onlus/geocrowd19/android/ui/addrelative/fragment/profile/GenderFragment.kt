package org.ascolto.onlus.geocrowd19.android.ui.addrelative.fragment.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import org.ascolto.onlus.geocrowd19.android.R
import com.bendingspoons.base.extensions.hideKeyboard
import kotlinx.android.synthetic.main.add_relative_gender_fragment.*
import kotlinx.android.synthetic.main.add_relative_gender_fragment.next
import org.ascolto.onlus.geocrowd19.android.db.entity.Gender
import org.ascolto.onlus.geocrowd19.android.ui.addrelative.RelativeInfo

class GenderFragment : RelativeContentFragment(R.layout.add_relative_gender_fragment) {
    override val nextButton: View
        get() = next

    override fun onResume() {
        super.onResume()
        this.view?.hideKeyboard()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        male.setOnClickListener {
            validate(true)
        }

        female.setOnClickListener {
            validate(true)
        }

        nextButton.setOnClickListener {
            viewModel.onNextTap(NicknameFragment::class.java)
        }
    }

    override fun onUserInfoUpdate(userInfo: RelativeInfo) {
        updateUI(userInfo.gender)
        validate(false)
    }

    private fun validate(updateModel: Boolean = true): Boolean {
        val valid = male.isChecked || female.isChecked
        nextButton.isEnabled = valid
        if(valid && updateModel) updateModel(when {
            male.isChecked -> Gender.MALE
            else -> Gender.FEMALE
        })
        return valid
    }

    private fun updateModel(gender: Gender) {
        viewModel.userInfo()?.let {
            viewModel.updateUserInfo(it.copy(gender = gender))
        }
    }

    private fun updateUI(gender: Gender?) {
        when(gender) {
            Gender.MALE -> {
                male.isChecked = true
                female.isChecked = false
            }
            Gender.FEMALE  -> {
                male.isChecked = false
                female.isChecked = true
            }
            else -> {
                male.isChecked = false
                female.isChecked = false
            }
        }
    }
}
