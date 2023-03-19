package ru.dikoresearch.warehouse.presentation.camera

import android.app.AlertDialog
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.dikoresearch.warehouse.MainActivity
import ru.dikoresearch.warehouse.R
import ru.dikoresearch.warehouse.databinding.FragmentCameraBinding
import ru.dikoresearch.warehouse.domain.camera.CameraXCaptureHelper
import ru.dikoresearch.warehouse.presentation.utils.ALLOWED_NUMBER_OF_IMAGES
import ru.dikoresearch.warehouse.presentation.utils.ORDER_NAME
import ru.dikoresearch.warehouse.presentation.utils.getAppComponent
import timber.log.Timber
import java.io.File
import kotlin.properties.Delegates

class CameraFragment: Fragment(R.layout.fragment_camera) {

    private var binding: FragmentCameraBinding by Delegates.notNull()
    private var isAbleToCapturePhoto = false

    private val viewModel: CameraViewModel by viewModels({activity as MainActivity }){
        getAppComponent().viewModelFactory
    }

    private val cameraXHelper: CameraXCaptureHelper by lazy {
        CameraXCaptureHelper(
            caller = this,
            previewView = binding.cameraPreview,
            filesDirectory = filesDirectory,
            orderName = orderName,
            onError = {
                Timber.e("Got error $it")
            },
            onPictureTaken = {file, _ ->
                viewModel.addPicture(file.absolutePath)
            }
        )
    }

    private val allowedNumberOfImages by lazy {
        arguments?.getInt(ALLOWED_NUMBER_OF_IMAGES, 1) ?: 1
    }

    private val orderName by lazy {
        arguments?.getString(ORDER_NAME) ?: "Unknown"
    }

    private val filesDirectory: File by lazy {
        File(requireContext().applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCameraBinding.inflate(inflater, container, false)

        binding.cameraOkBtn.setOnClickListener {
            viewModel.flushImagesPathsBuffer()
            findNavController().navigateUp()
        }

        binding.cameraCaptureImageButton.setOnClickListener{
            if (isAbleToCapturePhoto){
                cameraXHelper.takePicture()
            }
            else {
                showInfoDialog()
                binding.cameraCaptureImageButton.isEnabled = false
            }

        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            launch {
                viewModel.cameraScreenState.collectLatest { state ->
                    binding.cameraOkBtn.isEnabled = state.isOkBtnEnabled
                    binding.cameraImagesNumTextView.text = state.numberOfCapturedImages.toString()

                    isAbleToCapturePhoto = state.numberOfCapturedImages < allowedNumberOfImages
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cameraXHelper.start()
    }

    override fun onStop() {
        super.onStop()
        cameraXHelper.stop()
    }

    private fun showInfoDialog(){
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle(R.string.images_number_limit_reached)
        dialogBuilder.setPositiveButton(R.string.ok){dialogInterface, _ ->
            dialogInterface.dismiss()
        }
        dialogBuilder.show()
    }
}