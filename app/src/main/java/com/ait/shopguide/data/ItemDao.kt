package com.ait.shopguide.data

import androidx.room.*

@Dao
interface ItemDao {

        @Query("SELECT * FROM item")
        fun getAllItem() : List<Item>

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