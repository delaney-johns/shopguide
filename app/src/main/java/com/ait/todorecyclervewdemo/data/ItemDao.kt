package com.ait.todorecyclervewdemo.data

import android.content.ClipData
import androidx.room.*

@Dao
interface ItemDao {

        @Query("SELECT * FROM item")
        fun getAllItem() : List<Item>

        @Insert
        fun insertItem(item: Item) : Long

        @Delete
        fun deleteItem(item: Item)

        @Update
        fun updateItem(item: Item)

        @Query("DELETE FROM item")
        fun deleteAllItem()

}