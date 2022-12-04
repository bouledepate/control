package com.amir.ctrl.fragments.supervisor

import android.app.ProgressDialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.amir.ctrl.MainActivity
import com.amir.ctrl.R
import com.amir.ctrl.data.Report
import com.amir.ctrl.databinding.FragmentReportDetailBinding
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class ReportDetailFragment : Fragment() {

    private var _binding: FragmentReportDetailBinding? = null
    private val mBinding get() = _binding!!
    private val parentActivity get() = requireActivity() as MainActivity
    private lateinit var report: Report

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)
        report = arguments?.getParcelable("report")!!
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
        loadReportImage()
        insertReportData()
    }

    private fun loadReportImage() {
        val progressDialog = ProgressDialog(this.context)
        progressDialog.setMessage("Получение изображения...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        val storage = report.image?.let { FirebaseStorage.getInstance().reference.child(it) }
        val localTempFile = File.createTempFile("member_report_image", "jpeg")
        storage?.getFile(localTempFile)?.addOnSuccessListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            val bitmap = BitmapFactory.decodeFile(localTempFile.absolutePath)
            insertImage(bitmap, localTempFile.absolutePath)
        }?.addOnFailureListener {
            if (progressDialog.isShowing) progressDialog.dismiss()
            parentActivity.showToast("Ошибка при попытке загрузки изображения", Toast.LENGTH_SHORT)
            findNavController().navigateUp()
        }
    }

    private fun insertImage(image: Bitmap, path: String) {
        val ei = ExifInterface(path)
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

    private fun insertReportData() {
        mBinding.datetime.text = report.date
        mBinding.geolocation.text = report.geolocation
    }

    private fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}