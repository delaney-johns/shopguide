package com.ait.todorecyclervewdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ait.todorecyclervewdemo.R
import com.ait.todorecyclervewdemo.ScrollingActivity
import com.ait.todorecyclervewdemo.data.AppDatabase
import com.ait.todorecyclervewdemo.data.Item
import com.ait.todorecyclervewdemo.touch.TodoTouchHelperCallback
import kotlinx.android.synthetic.main.new_item_dialog.view.*
import kotlinx.android.synthetic.main.item_row.view.*
import java.util.*


class ItemAdapter : RecyclerView.Adapter<ItemAdapter.ViewHolder>, TodoTouchHelperCallback {

    var itemList = mutableListOf<Item>()


    val context: Context
    constructor(context: Context, listItems: List<Item>){
        this.context = context

        itemList.addAll(listItems)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemRow = LayoutInflater.from(context).inflate(
            R.layout.item_row, parent, false
        )
        return ViewHolder(itemRow)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var item = itemList.get(holder.adapterPosition)


        holder.cbIsPurchased.text = item.name
        holder.cbIsPurchased.isChecked = item.done
        holder.tvPrice.text = item.price
        holder.tvCategory.text = item.category
        holder.tvDescr.text = item.descr

        if (holder.tvCategory.text.toString().toLowerCase() == "bakery") {
            holder.ivCategory.setImageResource(R.drawable.bakery)
        } else if (holder.tvCategory.text.toString().toLowerCase() == "clothing") {
            holder.ivCategory.setImageResource(R.drawable.clothing)
        } else if (holder.tvCategory.text.toString().toLowerCase() == "deli") {
            holder.ivCategory.setImageResource(R.drawable.deli)
        }else if (holder.tvCategory.text.toString().toLowerCase() == "frozen") {
            holder.ivCategory.setImageResource(R.drawable.frozen)
        }else  holder.ivCategory.setImageResource(R.drawable.produce)
        //holder.tvCategory.text = holder.spinner.getItemAtPosition(position).toString()

        holder.btnDelete.setOnClickListener {
            deleteItem(holder.adapterPosition)
        }


        holder.cbIsPurchased.setOnClickListener {
            item.done = holder.cbIsPurchased.isChecked
            updateItem(item)
        }

        holder.btnEdit.setOnClickListener {
            (context as ScrollingActivity).showEditItemDialog(item, holder.adapterPosition)
        }
    }

    fun deleteItem(index: Int){
        Thread{
            AppDatabase.getInstance(context).itemDao().deleteItem(itemList[index])
            (context as ScrollingActivity).runOnUiThread {
                itemList.removeAt(index)
                notifyItemRemoved(index)
            }
        }.start()
    }

    fun addItem(item: Item) {
        itemList.add(item)
        notifyItemInserted(itemList.lastIndex)
    }

    fun updateItem(item: Item) {
        Thread {
            AppDatabase.getInstance(context).itemDao().updateItem(item)
        }.start()
    }

    fun updateItemOnPosition(item: Item, index: Int) {
        itemList.set(index, item)
        notifyItemChanged(index)
    }

    override fun onDismissed(position: Int) {
        deleteItem(position)
    }

    override fun onItemMoved(fromPosition: Int, toPosition: Int) {
        Collections.swap(itemList, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
    }


    fun deleteAllItems() {
        Thread {
            AppDatabase.getInstance(context).itemDao().deleteAllItem()

            (context as ScrollingActivity).runOnUiThread {
                itemList.clear()
                notifyDataSetChanged()
            }
        }.start()
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cbIsPurchased = itemView.cbIsPurchased
        val tvPrice = itemView.tvPrice
        var tvCategory = itemView.tvCategory
        val tvDescr = itemView.tvDescr
        val btnDelete = itemView.btnDelete
        val btnEdit = itemView.btnEdit
        //val spinner = itemView.spinnerCategory
        val ivCategory = itemView.ivCategory

    }


}