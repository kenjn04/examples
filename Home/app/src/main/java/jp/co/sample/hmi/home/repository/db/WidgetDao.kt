package jp.co.sample.hmi.home.repository.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import android.arch.persistence.room.OnConflictStrategy.REPLACE

@Dao
interface WidgetDao {
    @Query("SELECT * FROM widgets")
    fun getAll(): LiveData<List<WidgetItemInfo>>

    @Insert(onConflict = REPLACE)
    fun insert(widget: WidgetItemInfo)

    @Delete
    fun delete(widget: WidgetItemInfo)

    @Query("DELETE FROM widgets")
    fun deleteAll()

    @Update
    fun update(vararg widgets: WidgetItemInfo)
}