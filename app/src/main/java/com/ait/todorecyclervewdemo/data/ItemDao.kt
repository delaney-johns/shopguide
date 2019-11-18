package com.ait.todorecyclervewdemo.data

import android.content.ClipData
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ItemDao {

        @Query("SELECT * FROM item")
        fun getAllItem() : List<Item>

        @Query("""SELECT * FROM item WHERE category='Bakery'""")
        fun getBakeryItems() : List<Item>

        @Query("""SELECT * FROM item WHERE category='Clothing'""")
        fun getClothingItems() : List<Item>

        @Query("""SELECT * FROM item WHERE category='Deli'""")
        fun getDeliItems() : List<Item>

        @Query("""SELECT * FROM item WHERE category='Frozen'""")
        fun getFrozenItems() : List<Item>

        @Query("""SELECT * FROM item WHERE category='Produce'""")
        fun getProduceItems() : List<Item>

        @Query("""SELECT * FROM item WHERE category=:cat""")
        fun getItemsForCategory(cat: String) : List<Item>

        @Insert
        fun insertItem(item: Item) : Long

        @Delete
        fun deleteItem(item: Item)

        @Update
        fun updateItem(item: Item)

        @Query("DELETE FROM item")
        fun deleteAllItem()

}