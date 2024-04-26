package com.example.funfactoftheday

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.funfactoftheday.databinding.FragmentAddAFactBinding
import timber.log.Timber

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddAFactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddAFactFragment : DialogFragment() {

//    setFragmentResultListener("CategoryToDialogFragmentRequestKey") { requestKey, bundle ->
//        categoryName = bundle.getString("CategoryToDialogBundleKey").toString()
//        Timber.e("In AddAFactFrag, categoryName: $categoryName")
//    } //This one

    private var categoryName: String? = null

    internal lateinit var listener: NoticeDialogListener
    private var _binding: FragmentAddAFactBinding? = null
    private val binding get() = _binding!!

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddAFactBinding, category: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(ARG_PARAM1)
        }
    }

    // Override the Fragment.onAttach() method to instantiate the
    // NoticeDialogListener.
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface.
        try {
            // Instantiate the NoticeDialogListener so you can send events to
            // the host.
            listener = context as NoticeDialogListener
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface. Throw exception.
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentAddAFactBinding.inflate(LayoutInflater.from(context))
            val builder = AlertDialog.Builder(it)

            // Inflate and set the layout for the dialog.
            // Pass null as the parent view because it's going in the dialog
            // layout.
            builder.setView(binding.root)
                // Add action buttons.
                .setPositiveButton("Submit",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Sign in the user.
                        listener.onDialogPositiveClick(this, binding, categoryName!!)
//                       Timber.e("Submit Clicked, Fact Name: ${binding.etFactName.text} Cat Name: ${binding.etCategoryName.text}")
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        Timber.e("Cancel Clicked")
                        listener.onDialogNegativeClick(this)
//                        getDialog()!!.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factText = binding.etFactName.text.toString().trim()
        if(factText.isEmpty()){
            binding.etFactName.error = "Please Enter a Fact!"
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddAFact.
         */
        @JvmStatic
        fun newInstance(categoryName: String) =
            AddAFactFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, categoryName)
                }
            }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}