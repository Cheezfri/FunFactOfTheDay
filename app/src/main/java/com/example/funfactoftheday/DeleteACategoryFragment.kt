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
import com.example.funfactoftheday.databinding.FragmentDeleteACategoryBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

/**
 * A simple [Fragment] subclass.
 * Use the [DeleteACategoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DeleteACategoryFragment : DialogFragment() {

    private var categoryName: String? = null

    internal lateinit var listener:NoticeDialogListener
    private var _binding: FragmentDeleteACategoryBinding? = null
    private val binding get() = _binding!!

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment, binding: FragmentDeleteACategoryBinding, category: String)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            categoryName = it.getString(ARG_PARAM1)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
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
            _binding = FragmentDeleteACategoryBinding.inflate(LayoutInflater.from(context))
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
                .setPositiveButton("Delete Category",
                    DialogInterface.OnClickListener{ dialog, id ->
                        listener.onDialogPositiveClick(this, binding, categoryName!!)
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener{ dialog, id ->
                        listener.onDialogNegativeClick(this)
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DeleteACataegory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            DeleteACategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}