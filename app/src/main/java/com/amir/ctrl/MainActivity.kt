package com.amir.ctrl

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.os.ParcelUuid
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.amir.ctrl.data.User
import com.amir.ctrl.databinding.ActivityMainBinding
import com.amir.ctrl.fragments.LoginFragment
import com.amir.ctrl.fragments.member.MemberMainFragment
import com.amir.ctrl.fragments.member.ReportPreviewFragment
import com.amir.ctrl.fragments.supervisor.SupervisorMainFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.io.File


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val mBinding get() = _binding!!
    private lateinit var userImageFilename: String
    private var loadingDialog: ProgressDialog? = null

    val database: FirebaseDatabase get() = FirebaseDatabase.getInstance()

    companion object {
        const val PERMISSION_CODE = 10
        val PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun allPermissionGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        loadAuthorizationPage()
    }

    private fun showLoadingDialog() {
        loadingDialog = ProgressDialog(this)
        loadingDialog!!.setMessage("Загружаем ваши данные...")
        loadingDialog!!.setCancelable(false)
        loadingDialog!!.show()
    }

    private fun closeLoadingDialog() {
        loadingDialog!!.dismiss()
    }

    private fun loadAuthorizationPage() {
        showLoadingDialog()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            closeLoadingDialog()
        } else {
            login()
        }
    }

    fun login() {
        val user = FirebaseAuth.getInstance().currentUser
        if (user?.uid != null)
            database.reference.child("users").child(user.uid).child("supervisor").get()
                .addOnSuccessListener {
                    var result = it.getValue(Boolean::class.java) == true
                    val bundle = bundleOf(
                        "email" to user.email,
                        "uuid" to user.uid
                    )
                    if (result) {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_supervisorMainFragment2, bundle)
                    } else {
                        findNavController(R.id.nav_host_fragment).navigate(R.id.action_loginFragment_to_memberMainFragment2, bundle)
                    }
                }.addOnCompleteListener {
                    closeLoadingDialog()
                }
    }

    fun useCamera() {
        if (allPermissionGranted()) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_CODE)
        }
    }

    fun getUserImage(): String {
        return userImageFilename
    }

    private fun openCamera() {
        val filename = "user_picture";
        val storage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(filename, ".jpeg", storage)
        userImageFilename = imageFile.absolutePath
        val imageUri =
            FileProvider.getUriForFile(baseContext, "com.amir.ctrl.fileprovider", imageFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, PERMISSION_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (allPermissionGranted()) {
                openCamera()
            } else {
                showToast(resources.getString(R.string.camera_permission_error), Toast.LENGTH_SHORT)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_CODE) {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_memberMainFragment_to_reportPreviewFragment2)
        }
    }

    fun showToast(message: String, duration: Int) {
        Toast.makeText(applicationContext, message, duration).show()
    }
}