package com.ait.shopguide

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ait.shopguide.ScrollingActivity.Companion.KEY_ITEM
import com.ait.shopguide.data.Item
import kotlinx.android.synthetic.main.new_item_dialog.view.*


class ItemDialog : DialogFragment(), AdapterView.OnItemSelectedListener {

    interface ItemHandler {
        fun itemCreated(item: Item)
        fun itemUpdated(item: Item)
    }

    private lateinit var itemHandler: ItemHandler


    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is ItemHandler) {
            itemHandler = context
        } else {
            throw RuntimeException(
                "The activity does not implement the ItemHandlerInterface"
            )
        }
    }

    private lateinit var etPrice: EditText
    private lateinit var etName: EditText
    private lateinit var etDescr: EditText
    private lateinit var spinner: Spinner
    var isEditMode = false

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {


        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("New item")



        val rootView = requireActivity().layoutInflater.inflate(
            R.layout.new_item_dialog, null
        )



      //  getDialog()?.getWindow()?.getAttributes()?.
        //    windowAnimations = R.style.DialogScale

        etPrice = rootView.etPrice
        etName = rootView.etName
        etDescr = rootView.etDescription
        spinner = rootView.spinnerCategory


        var spinnerAdapter = ArrayAdapter.createFromResource(
            context as ScrollingActivity,
            R.array.categories_array,
            android.R.layout.simple_spinner_item
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter

        spinner.onItemSelectedListener = this



        builder.setView(rootView)


        isEditMode = ((arguments != null) && (arguments!!.containsKey(KEY_ITEM)))

        if (isEditMode) {
            builder.setTitle("Edit item")
            var item = (arguments?.getSerializable(KEY_ITEM) as Item)
            etName.setText(item.name)
            etPrice.setText(item.price)
            etDescr.setText(item.descr)


        }

        builder.setPositiveButton("OK") { dialog, which ->

        }

        builder.setNegativeButton("Cancel")
            { dialog, id ->
              dialog.dismiss()
            }

        //dialog!!.setCancelable(false)

        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(false)
        return dialog

    }


    override fun onResume() {
        super.onResume()

        val positiveButton = (dialog as AlertDialog).getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            if (etName.text.isNotEmpty()) {
                if (isEditMode) {
                    handleItemEdit()
                } else {
                    handleItemCreate()

                }


                dialog!!.dismiss()

            } else {
                etName.error = getString(R.string.etName_error_msg)
            }
        }
    }


    private fun handleItemCreate() {
        itemHandler.itemCreated(
            Item(
                null,
                etDescr.text.toString(),
                ret,
                etName.text.toString(),
                etPrice.text.toString(),
                false
            )
        )
    }

    lateinit var ret: String
    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        var item = p0?.getItemAtPosition(p2)
        ret = item.toString()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    private fun handleItemEdit() {
        val itemToEdit = arguments?.getSerializable(
            KEY_ITEM
        ) as Item
        itemToEdit.name = etName.text.toString()
        itemToEdit.descr = etDescr.text.toString()
        itemToEdit.price = etPrice.text.toString()
        itemToEdit.category = ret

        itemHandler.itemUpdated(itemToEdit)
    }


}