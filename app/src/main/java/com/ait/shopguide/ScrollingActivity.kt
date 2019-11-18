package com.ait.shopguide

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import com.ait.shopguide.adapter.ItemAdapter
import com.ait.shopguide.data.AppDatabase
import com.ait.shopguide.data.Item
import com.ait.shopguide.touch.ItemReyclerTouchCallback
import kotlinx.android.synthetic.main.activity_scrolling.*
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt
import android.content.Intent


class ScrollingActivity : AppCompatActivity(), ItemDialog.ItemHandler {
    lateinit var itemAdapter: ItemAdapter

    companion object {
        const val KEY_ITEM = "KEY_ITEM"
        const val KEY_STARTED = "KEY_STARTED"
        const val TAG_ITEM_DIALOG = "TAG_ITEM_DIALOG"
        const val TAG_ITEM_EDIT = "TAG_ITEM_EDIT"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)

        initRecyclerView()


        fab.setOnClickListener {
            fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fab_rotate))
            showAddItemDialog()
        }


        if (!wasStartedBefore()) {
            val diaMsg = welcomeMsg()
            diaMsg.show()
        }

        saveWasStarted()
    }


    fun saveWasStarted() {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        var editor = sharedPref.edit()
        editor.putBoolean(KEY_STARTED, true)
        editor.apply()
    }

    fun wasStartedBefore(): Boolean {
        var sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        //if not found, returns default value
        return sharedPref.getBoolean(KEY_STARTED, false)
    }


    private fun initRecyclerView() {
        Thread {
            var itemList =
                AppDatabase.getInstance(this@ScrollingActivity).itemDao().getAllItem()



            runOnUiThread {
                itemAdapter = ItemAdapter(this, itemList)
                recyclerItem.adapter = itemAdapter

                var itemDecoration = DividerItemDecoration(
                    this,
                    DividerItemDecoration.VERTICAL
                )
                recyclerItem.addItemDecoration(itemDecoration)

                val callback = ItemReyclerTouchCallback(itemAdapter)
                val touchHelper = ItemTouchHelper(callback)
                touchHelper.attachToRecyclerView(recyclerItem)
            }
        }.start()
    }

    fun showAddItemDialog() {

        ItemDialog().show(supportFragmentManager, TAG_ITEM_DIALOG)
    }

    fun saveItem(item: Item) {
        Thread {
            var newId = AppDatabase.getInstance(this).itemDao().insertItem(item)
            item.itemId = newId

            runOnUiThread {
                itemAdapter.addItem(item)/////////////////////
            }
        }.start()
    }

    var editIndex: Int = -1
    fun showEditItemDialog(itemToEdit: Item, index: Int) {

        editIndex = index

        val editDialog = ItemDialog()

        val bundle = Bundle()
        bundle.putSerializable(KEY_ITEM, itemToEdit)
        editDialog.arguments = bundle

        editDialog.show(supportFragmentManager, TAG_ITEM_EDIT)
    }

    override fun itemCreated(item: Item) {
        saveItem(item)
    }

    override fun itemUpdated(item: Item) {
        Thread {
            AppDatabase.getInstance(this@ScrollingActivity).itemDao().updateItem(item)
            runOnUiThread {
                itemAdapter.updateItemOnPosition(item, editIndex)
            }
        }.start()
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var inflater = getMenuInflater()
        inflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.title.toString()) {
            getString(R.string.delete_all) -> {
                val diaBox = askOption()
                diaBox.show()
            }
            getString(R.string.view_by_cat) -> {

                 val intent = Intent(this, CategoryViewActivity::class.java)
                this.startActivity(intent)
            }

        }
        return true
    }

    private fun askOption(): AlertDialog {

        return AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_confirmation))

            .setPositiveButton(getString(R.string.delete))
            { dialog, whichButton ->
                itemAdapter.deleteAllItems()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.negative_button_text))
            { dialog, which -> dialog.dismiss() }
            .create()
    }

    private fun menuTutorial(): AlertDialog {

        return AlertDialog.Builder(this)
            .setTitle(getString(R.string.menu_tutorial_text))

            .setPositiveButton(R.string.positive_button_text)
            { dialog, whichButton ->
                dialog.dismiss()
            }
            .create()

    }


    private fun welcomeMsg(): AlertDialog {

        return AlertDialog.Builder(this)
            .setTitle(getString(R.string.welcome_msg))

            .setPositiveButton(getString(R.string.yes))
            { dialog, whichButton ->
                fabTutorial()

            }
            .setNegativeButton(getString(R.string.welcome_negative_button)) { dialog, whichButton ->
                wasStartedBefore()
                dialog.dismiss()
            }
            .create()
    }

    private fun fabTutorial() {
       val builder = MaterialTapTargetPrompt.Builder(this)
        builder
            .setTarget(R.id.fab)
            .setPrimaryText(getString(R.string.fab_tutorial_title))
            .setSecondaryText(getString(R.string.fab_tutorial_text))
            .setAnimationInterpolator(FastOutSlowInInterpolator())
            .show()


        builder.setPromptStateChangeListener { prompt, state ->
            if (state == MaterialTapTargetPrompt.STATE_FOCAL_PRESSED || state == MaterialTapTargetPrompt.STATE_NON_FOCAL_PRESSED) {
                val menuDialog = menuTutorial()
                menuDialog.show()
            }
        }
    }


}







