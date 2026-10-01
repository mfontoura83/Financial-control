package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.BankAccountDao
import com.example.data.local.dao.BankStatementItemDao
import com.example.data.local.dao.CashTransactionDao
import com.example.data.local.model.BankAccount
import com.example.data.local.model.BankStatementItem
import com.example.data.local.model.CashTransaction

@Database(
    entities = [
        BankAccount::class,
        CashTransaction::class,
        BankStatementItem::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bankAccountDao(): BankAccountDao
    abstract fun cashTransactionDao(): CashTransactionDao
    abstract fun bankStatementItemDao(): BankStatementItemDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fincontrol_master.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
