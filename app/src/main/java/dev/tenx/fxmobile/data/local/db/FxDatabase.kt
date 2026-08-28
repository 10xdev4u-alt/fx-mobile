package dev.tenx.fxmobile.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SessionEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = true
)
abstract class FxDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun messageDao(): MessageDao

    companion object {
        const val NAME = "fx_database"
    }
}
