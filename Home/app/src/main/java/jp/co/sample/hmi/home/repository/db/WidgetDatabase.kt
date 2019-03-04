package jp.co.sample.hmi.home.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.arch.persistence.db.SupportSQLiteDatabase
import jp.co.sample.hmi.home.common.WidgetIdProvider

@Database(entities = arrayOf(WidgetItemInfo::class), version = 2)
abstract class WidgetDatabase : RoomDatabase() {

    abstract fun widgetDao(): WidgetDao

    companion object {

        @Volatile
        private var INSTANCE: WidgetDatabase? = null
        fun getDatabase(context: Context): WidgetDatabase {
            if (INSTANCE == null) {
                synchronized(WidgetDatabase::class.java) {
                    if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context.applicationContext,
                                    WidgetDatabase::class.java, "widget_database"
                                )
                                .fallbackToDestructiveMigration()
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

                val widgetDao = INSTANCE!!.widgetDao()
                WidgetIdProvider.initialize(widgetDao.getAll())

                // If you want to keep the data through app restarts,
                // comment out the following line.
                PopulateDbAsync(widgetDao).execute()
            }
        }

        /**
         * Populate the database in the background.
         * If you want to start with more words, just add them.
         */
        private class PopulateDbAsync internal constructor(private val widgetDao: WidgetDao) : AsyncTask<Unit, Unit, Unit>() {

            // TODO: how to initialize
            override fun doInBackground(vararg params: Unit?) {
                widgetDao.deleteAll()

                /*
                val packageName1 = "com.android.chrome"
                val className1 = "org.chromium.chrome.browser.searchwidget.SearchWidgetProvider"
                val widget1 = WidgetItemInfo(packageName1, className1,  0,  0,  0)
                widgetDao.insert(widget1)
                */

                val packageName2 = "com.google.android.apps.messaging"
                val className2 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget2 = WidgetItemInfo(packageName2, className2,  0,  1,  0)
                widgetDao.insert(widget2)

                val widget3 = WidgetItemInfo(packageName2, className2,  0,  2,  0)
                widgetDao.insert(widget3)

                /*
                val packageName4 = "com.google.android.apps.messaging"
                val className4 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget4 = WidgetItemInfo(packageName2, className2,  0,  3,  0)
                widgetDao.insert(widget4)
                */
            }
        }

    }
}