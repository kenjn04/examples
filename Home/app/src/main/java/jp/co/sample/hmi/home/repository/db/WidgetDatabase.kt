package jp.co.sample.hmi.home.repository.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Room
import android.content.Context
import android.os.AsyncTask
import android.arch.persistence.db.SupportSQLiteDatabase
import jp.co.sample.hmi.home.common.WidgetIdProvider

@Database(entities = arrayOf(WidgetItemInfo::class), version = 3)
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

                val packageName1 = "com.android.chrome"
                val className1 = "org.chromium.chrome.browser.searchwidget.SearchWidgetProvider"
                val widget1 = WidgetItemInfo(packageName1, className1,  1,  0,  0, 1, 1)
                widgetDao.insert(widget1)

                val packageName2 = "com.google.android.apps.messaging"
                val className2 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget2 = WidgetItemInfo(packageName2, className2,  1,  1,  0, 2, 1)
                widgetDao.insert(widget2)

                val packageName3 = "com.google.android.apps.messaging"
                val className3 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget3 = WidgetItemInfo(packageName3, className3,  2,  3,  0, 1, 1)
                widgetDao.insert(widget3)

                val packageName4 = "com.google.android.apps.messaging"
                val className4 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget4 = WidgetItemInfo(packageName4, className4,  1,  0,  1, 1, 1)
                widgetDao.insert(widget4)

                val packageName5 = "com.google.android.apps.messaging"
                val className5 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget5 = WidgetItemInfo(packageName5, className5,  1,  1,  1, 1, 1)
                widgetDao.insert(widget5)

                val packageName6 = "com.google.android.apps.messaging"
                val className6 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget6 = WidgetItemInfo(packageName6, className6,  1,  2,  1, 2, 1)
                widgetDao.insert(widget6)

                val packageName7 = "com.google.android.apps.messaging"
                val className7 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget7 = WidgetItemInfo(packageName7, className7,  2,  0,  0, 3, 2)
                widgetDao.insert(widget7)

                val packageName8 = "com.google.android.apps.messaging"
                val className8 = "com.google.android.apps.messaging.widget.BugleWidgetProvider"
                val widget8 = WidgetItemInfo(packageName8, className8,  0,  0,  0, 4, 2)
                widgetDao.insert(widget8)
            }
        }

    }
}