package com.amir.ctrl.fragments.member

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.location.Geocoder
import android.location.Location
import android.media.ExifInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.amir.ctrl.MainActivity
import com.amir.ctrl.data.Report
import com.amir.ctrl.data.retrofit.ApiService
import com.amir.ctrl.databinding.FragmentReportPreviewBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.FileInputStream
import java.util.*


class ReportPreviewFragment : Fragment() {

    private var _binding: FragmentReportPreviewBinding? = null
    private val mBinding get() = _binding!!
    private val parentActivity get() = requireActivity() as MainActivity

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportPreviewBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(parentActivity.baseContext)
        loadImage()
        mBinding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
        mBinding.submitButton.setOnClickListener {
            sendReport()
        }
    }

    private fun loadImage() {
        val image = BitmapFactory.decodeFile(parentActivity.getUserImage())
        val ei = ExifInterface(parentActivity.getUserImage())

        getGeolocation()

        val orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        var rotatedBitmap: Bitmap? = null
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap = rotateImage(image, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap = rotateImage(image, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap = rotateImage(image, 270)
            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap = image
            else -> rotatedBitmap = image
        }
        mBinding.imagePreview.setImageBitmap(rotatedBitmap)
    }

    private fun getGeolocation() {
        val retrofitClient = ApiService()
        val geocoder = Geocoder(parentActivity.applicationContext, Locale.getDefault())
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val latitude = location?.latitude
                val longitude = location?.longitude
                if (latitude != null && longitude !== null) {
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val country = addresses[0].countryName;
                    val area = addresses[0].adminArea;
                    val locality = addresses[0].locality;
                    val subLocality = addresses[0].subLocality;
                    val name = addresses[0].featureName
//                    val a = addresses[0].getAddressLine(0)
                    val address = "$country, $area, $locality, $subLocality, $name."
                    mBinding.geolocation.text = address
                }

                retrofitClient.getTimezone(latitude.toString(), longitude.toString()) {
                    if (it != null) {
                        mBinding.datetime.text = it.formatted
                    }
                }
            }
    }

    private fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    private fun sendReport() {
        enableLoadingBar()

        val database = parentActivity.database.reference
        val storage = FirebaseStorage.getInstance().reference

        val uuid = FirebaseAuth.getInstance().currentUser?.uid

        val currentReportImage = FileInputStream(File(parentActivity.getUserImage()))
        val currentReportImagePath = "images/${uuid}/${UUID.randomUUID()}.jpeg"

        val report = Report(
            uuid,
            mBinding.datetime.text.toString(),
            mBinding.geolocation.text.toString(),
            currentReportImagePath
        )

        storage.child(currentReportImagePath)
            .putStream(currentReportImage).addOnSuccessListener {
                if (uuid != null) {
                    database.child("reports").child(uuid).get().addOnSuccessListener {
                        val id = it.childrenCount
                        database.child("reports").child(uuid).child(id.toString()).setValue(report)
                            .addOnSuccessListener {
                                parentActivity.showToast(
                                    "Отчёт успешно загружен",
                                    Toast.LENGTH_SHORT
                                )
                                findNavController().navigateUp()
                            }.addOnFailureListener { handleFailedResult() }
                    }.addOnFailureListener { handleFailedResult() }
                }
            }.addOnFailureListener { handleFailedResult() }
    }

    private fun handleFailedResult() {
        parentActivity.showToast("Ошибка при загрузке отчёта", Toast.LENGTH_SHORT)
        findNavController().navigateUp()
    }

    private fun enableLoadingBar() {
        mBinding.cancelButton.visibility = View.INVISIBLE
        mBinding.submitButton.visibility = View.INVISIBLE
        mBinding.loadingBar.visibility = View.VISIBLE
    }
}