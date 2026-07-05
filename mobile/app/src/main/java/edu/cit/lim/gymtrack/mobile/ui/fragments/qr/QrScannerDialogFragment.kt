package edu.cit.lim.gymtrack.mobile.ui.fragments.qr

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import edu.cit.lim.gymtrack.mobile.databinding.DialogQrScannerBinding
import edu.cit.lim.gymtrack.mobile.ui.fragments.dashboard.DashboardFragment

class QrScannerDialogFragment : DialogFragment() {

    private var _binding: DialogQrScannerBinding? = null
    private val binding get() = _binding!!

    private var scannerMode: String = "member"
    private var hasPermission = false

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (!granted) {
            dismissAllowingStateLoss()
        } else {
            startScanning()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        scannerMode = arguments?.getString(ARG_MODE) ?: "member"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogQrScannerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.closeScannerButton.setOnClickListener { dismissAllowingStateLoss() }
        binding.barcodeScanner.viewFinder.setLaserVisibility(false)

        hasPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            startScanning()
        } else {
            binding.scannerHint.text = "Camera permission is required to scan QR codes."
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startScanning() {
        binding.scannerHint.text = "Align the QR code inside the camera view."
        binding.barcodeScanner.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult?) {
                val text = result?.text ?: return
                binding.barcodeScanner.pause()
                (parentFragment as? DashboardFragment)?.onQrScan(text, scannerMode)
                dismissAllowingStateLoss()
            }
        })
        binding.barcodeScanner.resume()
    }

    override fun onPause() {
        _binding?.barcodeScanner?.pause()
        super.onPause()
    }

    override fun onDestroyView() {
        binding.barcodeScanner.pause()
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_MODE = "mode"

        fun newInstance(mode: String): QrScannerDialogFragment {
            return QrScannerDialogFragment().apply {
                arguments = Bundle().apply { putString(ARG_MODE, mode) }
            }
        }
    }
}
