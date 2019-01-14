package jp.co.sample.hmi.home.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.arch.persistence.db.SupportSQLiteDatabase

@Database(entities = arrayOf(WidgetItemInfo::class), version = 1)
abstract class WidgetDatabase : RoomDatabase() {

    abstract fun widgetDao(): WidgetDao

    companion object {

        @Volatile
        private var INSTANCE: WidgetDatabase? = null
        fun getDatabase(context: Context): WidgetDatabase {
            if (INSTANCE == null) {
                synchronized(WidgetDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    WidgetDatabase::class.java!!, "widget_database"
                                )
//                                .fallbackToDestructiveMigration()
                                .addCallback(widgetDatabaseCallback)
                                .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val widgetDatabaseCallback = object : RoomDatabase.Callback() {

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                PopulateDbAsync(INSTANCE!!).execute()
            }
        }

        /**
         * Populate the database in the background.
         * If you want to start with more words, just add them.
         */
        private class PopulateDbAsync internal constructor(db: WidgetDatabase) : AsyncTask<Unit, Unit, Unit>() {

            private val widgetDao = db.widgetDao()

            override fun doInBackground(vararg params: Unit?) {
                widgetDao.deleteAll()

                val packageName = "com.android.chrome"
                val className = "org.chromium.chrome.browser.searchwidget.SearchWidgetProvider"
                val widget = WidgetItemInfo(packageName, className,  0,  0,  0)

                widgetDao.insert(widget)
            }
        }

    }
}