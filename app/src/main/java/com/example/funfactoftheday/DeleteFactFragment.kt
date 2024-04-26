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
import com.example.funfactoftheday.database.models.CategoryModel
import com.example.funfactoftheday.database.models.FactModel
import com.example.funfactoftheday.databinding.FragmentDeleteFactBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteFactFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteFactFragment : DialogFragment() {
    // TODO: Rename and change types of parameters
    private var toDelete: Array<FactModel>? = null
    private var categoryName: String = ""

    internal lateinit var listener: NoticeDialogListener
    private var _binding: FragmentDeleteFactBinding? = null
    private val binding get() = _binding!!

    interface NoticeDialogListener{
        fun onDialogPositiveClick(dialog: DialogFragment, toDelete: Array<FactModel>?, category: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            toDelete = it.getParcelableArray("parcelable_array_key")?.map {
                it as FactModel
            }?.toTypedArray()
            categoryName = it.getString("category_name").toString()
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
            throw ClassCastException((context.toString() +
                    " must implement NoticeDialogListener"))
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            _binding = FragmentDeleteFactBinding.inflate(LayoutInflater.from(context))
            val builder = AlertDialog.Builder(it)

            // Inflate and set the layout for the dialog.
            // Pass null as the parent view because it's going in the dialog
            // layout.
            builder.setView(binding.root)
                // Add action buttons.
                .setPositiveButton("Delete Facts",
                    DialogInterface.OnClickListener { dialog, id ->
                        // Sign in the user.
                        listener.onDialogPositiveClick(this, toDelete, categoryName)
//                       Timber.e("Submit Clicked, Fact Name: ${binding.etFactName.text} Cat Name: ${binding.etCategoryName.text}")

                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
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
        return inflater.inflate(R.layout.fragment_delete_fact, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeleteFactFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Array<FactModel>, param2: String) =
            DeleteFactFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArray("parcelable_array_key", param1)
                    putString("category_name", param2)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}