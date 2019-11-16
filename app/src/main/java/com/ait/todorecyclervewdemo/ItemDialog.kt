package com.ait.todorecyclervewdemo

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import com.ait.todorecyclervewdemo.ScrollingActivity.Companion.KEY_TODO
import com.ait.todorecyclervewdemo.data.Item
import kotlinx.android.synthetic.main.activity_scrolling.*
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
                "The activity does not implement the TodoHandlerInterface"
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


        isEditMode = ((arguments != null) && (arguments!!.containsKey(KEY_TODO)))

        if (isEditMode) {
            builder.setTitle("Edit item")
            var item = (arguments?.getSerializable(KEY_TODO) as Item)
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
                etName.error = "This field can not be empty"
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
            KEY_TODO
        ) as Item
        itemToEdit.name = etName.text.toString()
        itemToEdit.descr = etDescr.text.toString()
        itemToEdit.price = etPrice.text.toString()
        itemToEdit.category = ret

        itemHandler.itemUpdated(itemToEdit)
    }


}