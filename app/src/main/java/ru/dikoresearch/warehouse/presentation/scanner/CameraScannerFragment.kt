package ru.dikoresearch.warehouse.presentation.scanner

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import ru.dikoresearch.warehouse.R
import ru.dikoresearch.warehouse.databinding.FragmentCameraScannerBinding
import ru.dikoresearch.warehouse.domain.camera.CameraXHelper
import ru.dikoresearch.warehouse.domain.camera.QrCodeAnalyzer
import ru.dikoresearch.warehouse.presentation.utils.ORDER_NAME
import timber.log.Timber
import kotlin.properties.Delegates

class CameraScannerFragment: Fragment(R.layout.fragment_camera_scanner) {

    private var binding: FragmentCameraScannerBinding by Delegates.notNull()

    private val qrCodeAnalyzer by lazy {
        QrCodeAnalyzer{
            binding.cameraScannerResultTextView.text = it
        }
    }

    private val cameraXHelper by lazy {
        CameraXHelper(
            caller = this,
            previewView = binding.cameraScannerPreview,
            imageAnalyzer = qrCodeAnalyzer,
            onError = {
                Timber.e("Got error from camera scanner $it")
            }
        )
    }



    private val permissionsRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                cameraXHelper.start()
            }
            else {
                Timber.e("No permissions granted")
            }
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraScannerBinding.inflate(inflater, container, false)

        if (!hasPermissions(requireActivity())){
            permissionsRequestLauncher.launch(
                CAMERA_PERMISSIONS_REQUIRED
            )
        }

        binding.cameraScannerManualEnterButton.setOnClickListener {
            showManualEnterDialog { orderName ->
                navigateToOrderDetails(orderName)
            }
        }

        binding.cameraScanButton.setOnClickListener{
            val orderName = binding.cameraScannerResultTextView.text.toString()
            navigateToOrderDetails(orderName)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (hasPermissions(requireActivity())){
            cameraXHelper.start()
        }
    }

    private fun navigateToOrderDetails(orderName: String){

        if (orderName.isNotEmpty()){
            val bundle = bundleOf(ORDER_NAME to orderName)
            findNavController().navigate(R.id.action_cameraScannerFragment_to_orderDetailsFragment, bundle)
        }
    }

    private fun showManualEnterDialog(onAccept: (String) -> Unit){
        activity?.apply {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle(getString(R.string.enter_order_number))
            val inputText = EditText(this)
            inputText.hint = getString(R.string.order_number)
            inputText.inputType = InputType.TYPE_CLASS_TEXT
            dialogBuilder.setView(inputText)

            dialogBuilder.setPositiveButton(R.string.ok) { _, _ ->
                val orderName = inputText.text.toString()
                onAccept(orderName)
            }

            dialogBuilder.setNegativeButton(R.string.cancel){ dialogInterface, _ ->
                    dialogInterface.cancel()
            }

            dialogBuilder.show()
        } ?: throw IllegalStateException("Fragment not attached to activity")
    }

    companion object {
        private val CAMERA_PERMISSIONS_REQUIRED = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R){
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
        else {
            arrayOf(
                Manifest.permission.CAMERA
            )
        }


        fun hasPermissions(context: Context) = CAMERA_PERMISSIONS_REQUIRED.all{
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}