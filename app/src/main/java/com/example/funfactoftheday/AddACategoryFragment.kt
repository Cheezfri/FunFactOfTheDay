package com.example.funfactoftheday

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.funfactoftheday.databinding.FragmentAddACategoryBinding
import timber.log.Timber


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddACategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddACategoryFragment : DialogFragment(){
    private var param1: String? = null
    private var param2: String? = null

    internal lateinit var listener: NoticeDialogListener
    private var _binding:FragmentAddACategoryBinding? = null
    private val binding get() = _binding!!

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentAddACategoryBinding)
        fun onDialogNegativeClick(dialog: DialogFragment)
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
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentAddACategoryBinding.inflate(LayoutInflater.from(context))
            val builder = AlertDialog.Builder(it)

            // Inflate and set the layout for the dialog.
            // Pass null as the parent view because it's going in the dialog
            // layout.
            builder.setView(binding.root)
                // Add action buttons.
                .setPositiveButton("Submit",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Sign in the user.
                        listener.onDialogPositiveClick(this, binding)
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_a_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryText = binding.etCategoryName.text.toString().trim()
        if(categoryText.isEmpty()){
            binding.etCategoryName.error = "Please Enter a Category!"
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddACategoryFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddACategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}