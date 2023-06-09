package ru.dikoresearch.warehouse.presentation.orderdetails


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.WorkManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.dikoresearch.warehouse.MainActivity
import ru.dikoresearch.warehouse.R
import ru.dikoresearch.warehouse.databinding.FragmentOrderDetailsBinding
import ru.dikoresearch.warehouse.domain.entities.OrderImage
import ru.dikoresearch.warehouse.presentation.camera.CameraViewModel
import ru.dikoresearch.warehouse.presentation.utils.*
import kotlin.properties.Delegates

class OrderDetailsFragment: Fragment(R.layout.fragment_order_details) {

    private var workManager: WorkManager by Delegates.notNull()

    private val viewModel: OrderDetailsViewModel by viewModels {
        getAppComponent().viewModelFactory
    }

    private val cameraViewModel: CameraViewModel by viewModels({activity as MainActivity }){
        getAppComponent().viewModelFactory
    }

    private var binding: FragmentOrderDetailsBinding by Delegates.notNull()
    private val adapter: OrderImagesAdapter by lazy {
        OrderImagesAdapter(
            onAddImage = {
                val bundle = bundleOf(
                    ORDER_NAME to orderName,
                    ALLOWED_NUMBER_OF_IMAGES to viewModel.getAllowedNumberOfImages()
                )
                findNavController().navigate(R.id.action_orderDetailsFragment_to_cameraFragment, bundle)
            },
            onRemoveImage = {
                viewModel.removeImage(it)
            },
            onImageClicked = {
                val bundle = bundleOf(
                    IMAGE_URL to it,
                    IMAGES_URLS_ARRAY to viewModel.getListOfImagesUrls()
                )
                findNavController().navigate(R.id.action_orderDetailsFragment_to_imagePreviewFragment, bundle)
            }

        )
    }

    private val goodsAdapter: OrderGoodsAdapter by lazy {
        OrderGoodsAdapter(
            onItemClicked = {art ->
                viewModel.showProductLocation(art)
            },
            onCheckedChanged = {index, state ->
                viewModel.setCheckedStateToGoods(index, state)
            }
        )
    }

    private val orderName by lazy {
        arguments?.getString(ORDER_NAME, "Unknown") ?: "Unknown"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        workManager = WorkManager.getInstance(requireContext().applicationContext)
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)

        binding.orderDetailsToolbar.setupWithNavController(findNavController(), AppBarConfiguration(findNavController().graph))

        val layoutManager = GridLayoutManager(requireContext(), 3)
        binding.orderImagesRecyclerView.layoutManager = layoutManager
        binding.orderImagesRecyclerView.adapter = adapter

        val goodsLayoutManger = LinearLayoutManager(requireActivity())
        binding.orderGoodsRecyclerView.layoutManager = goodsLayoutManger
        binding.orderGoodsRecyclerView.adapter = goodsAdapter

        binding.orderUploadToServerBtn.setOnClickListener {
            viewModel.uploadToServer(
                comment = binding.orderCommentEditTextView.text.toString(),
                workManager = workManager
            )
        }

        binding.orderCommentEditTextView.addTextChangedListener {
            viewModel.updateComment(it.toString())
        }

        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadOrder(orderName)
    }

    override fun onResume() {
        super.onResume()
        val l = cameraViewModel.getImagesPaths()
        if (l.isNotEmpty()){
            l.map {imagePath ->
                val orderImage = OrderImage(
                    imageName = imagePath.split("/").last(),
                    imageUri = imagePath,
                    loaded = false,
                    newImageActionHolder = false
                )
                viewModel.addImage(orderImage)
            }

        }
        cameraViewModel.refreshImagePaths()

        if (viewModel.orderDetailsState.value.comment.isNotBlank()){
            binding.orderCommentEditTextView.setText(viewModel.orderDetailsState.value.comment)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            launch {
                viewModel.showProgressBar.collectLatest {
                    if (it){
                        binding.orderDetailsProgressBar.visible()
                        binding.orderDetailsViewGroup.gone()
                    }
                    else {
                        binding.orderDetailsProgressBar.gone()
                        binding.orderDetailsViewGroup.visible()
                    }
                }
            }

            launch {
                viewModel.navigationEvent.collectLatest {
                    when(it){
                        is NavigationEvent.Navigate -> {
                            if (it.destination == NavigationConstants.IMAGE_PREVIEW_SCREEN){
                                findNavController().navigate(R.id.action_orderDetailsFragment_to_imagePreviewFragment, it.bundle)
                            }
                            if (it.destination == NavigationConstants.LOGIN_SCREEN){
                                findNavController().navigate(R.id.action_orderDetailsFragment_to_loginFragment)
                            }
                        }
                        is NavigationEvent.ShowToast -> {
                            Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }

            launch {
                viewModel.orderDetailsState.collectLatest { state ->
                    binding.orderStatusTextView.text = if(state.status == "New") getString(R.string.new_order) else getString(R.string.published_order)
                    binding.orderNameTextView.text = state.orderName
                    binding.orderUsernameTextView.text = state.username
                    binding.orderCreatedAtTextView.text = state.createdAt
                    val enableUploadButton = state.hasImagesToUpload && state.allGoodsChecked
                    binding.orderUploadToServerBtn.isEnabled = enableUploadButton

                    if (state.status != "New"){
                        binding.orderCommentEditTextView.isEnabled = false
                    }
                }
            }

            launch {
                viewModel.listOfImages.collectLatest {
                    adapter.listOfImages = it
                }
            }

            launch {
                viewModel.listOfGoods.collectLatest {
                    goodsAdapter.listOfGoods = it
                }
            }
        }
    }

}