package com.amir.ctrl.fragments.supervisor

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.navigateUp
import com.amir.ctrl.MainActivity
import com.amir.ctrl.data.User
import com.amir.ctrl.databinding.FragmentRegisterUserBinding
import com.bouledepate.caffy.core.storage.MainStorage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.oAuthProvider
import org.intellij.lang.annotations.JdkConstants.CalendarMonth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class RegisterUserFragment : Fragment() {

    private var _binding: FragmentRegisterUserBinding? = null
    private val mBinding get() = _binding!!

    private val parentActivity get() = requireActivity() as MainActivity

    private var uniqueIin: Boolean = true

    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterUserBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }

        mBinding.submitButton.setOnClickListener {
            registerMember()
        }

        val date = DatePickerDialog.OnDateSetListener { datePicker, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            updateDatetimeField()
        }

        mBinding.birthday.setOnClickListener {
            this.context?.let { context ->
                DatePickerDialog(
                    context,
                    date,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
        }

        mBinding.iin.setOnFocusChangeListener { view, b ->
            if (!b) {
                val iin = (view as AppCompatEditText).text.toString()
                if (iin.isBlank()) {
                    view.error = "Нужно указать ИИН!"
                } else {
                    validateIin()
                }
            }
        }
    }

    private fun updateDatetimeField() {
        val format = "MM/dd/yy";
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        mBinding.birthday.setText(dateFormat.format(calendar.time))
    }

    private fun registerMember() {
        if (validateForm()) {
            val auth = FirebaseAuth.getInstance()
            hideForm()
            auth.createUserWithEmailAndPassword(
                mBinding.email.text.toString(),
                mBinding.password.text.toString()
            ).addOnSuccessListener {
                val firebaseUser = it.user
                val user = prepareUser(firebaseUser?.uid)
                firebaseUser?.uid?.let { uid ->
                    parentActivity.database.reference.child("users").child(uid).setValue(user)
                        .addOnSuccessListener {
                            auth.signOut()
                            auth.signInWithEmailAndPassword(
                                MainStorage.email(parentActivity.applicationContext),
                                MainStorage.password(parentActivity.applicationContext)
                            ).addOnSuccessListener {
                                parentActivity.showToast("Пользователь успешно зарегистрирован.", Toast.LENGTH_SHORT)
                                findNavController().navigateUp()
                            }
                        }
                }
            }.addOnFailureListener {
                parentActivity.showToast("При регистрации произошла ошибка. Проверьте данные", Toast.LENGTH_SHORT)
                showForm()
            }
        }
    }

    private fun hideForm() {
        mBinding.registerFormBody.visibility = View.INVISIBLE
        mBinding.loading.visibility = View.VISIBLE
    }

    private fun showForm() {
        mBinding.registerFormBody.visibility = View.VISIBLE
        mBinding.loading.visibility = View.INVISIBLE
    }

    private fun prepareUser(uid: String?): User {
        return User(
            uid,
            mBinding.email.text.toString(),
            mBinding.firstName.text.toString(),
            mBinding.secondName.text.toString(),
            mBinding.lastName.text.toString(),
            mBinding.iin.text.toString(),
            mBinding.phone.text.toString(),
            mBinding.birthday.text.toString(),
            mBinding.address.text.toString()
        )
    }

    private fun validateForm(): Boolean {
        var validation = true;

        if (mBinding.email.text.isBlank()) {
            mBinding.email.error = "Нужно указать электронную почту!"
            validation = false
        }

        if (mBinding.password.text.isBlank()) {
            mBinding.password.error = "Нужно указать пароль!"
            validation = false
        }

        if (mBinding.iin.text.isBlank()) {
            mBinding.iin.error = "Нужно указать ИИН!"
            validation = false
        }

        if (!uniqueIin) {
            validation = false
        }

        return validation
    }

    private fun validateIin() {
        parentActivity.database.reference.child("users").get().addOnSuccessListener {
            it.children.forEach {
                it.children.forEach {
                    if (it.key == "iin") {
                        if (it.getValue(String::class.java) == mBinding.iin.text.toString()) {
                            uniqueIin = false
                            mBinding.iin.error = "Данный ИИН уже зарегистрирован."
                        } else {
                            uniqueIin = true
                        }
                    }
                }
            }
        }
    }
}