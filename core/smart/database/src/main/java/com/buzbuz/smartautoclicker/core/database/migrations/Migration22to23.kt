package com.buzbuz.smartautoclicker.core.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

import com.buzbuz.smartautoclicker.core.database.ACTION_TABLE

/** Adds storage for the system sound selected by a Sound action. */
object Migration22to23 : Migration(22, 23) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE `$ACTION_TABLE` ADD COLUMN `sound_uri` TEXT")
    }
}
