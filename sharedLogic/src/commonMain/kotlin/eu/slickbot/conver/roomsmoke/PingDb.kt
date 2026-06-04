package eu.slickbot.conver.roomsmoke

import androidx.room3.ConstructedBy
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import kotlinx.coroutines.flow.Flow

@Entity
data class Ping(
  @PrimaryKey val id: Long,
  val note: String,
)

@Dao
interface PingDao {
  @Insert suspend fun insert(ping: Ping)
  @Query("SELECT * FROM Ping ORDER BY id DESC") fun observeAll(): Flow<List<Ping>>
}

// Room KMP requires an expect RoomDatabaseConstructor referenced via @ConstructedBy
// for non-Android targets; KSP generates the actual per platform.
@Suppress("NO_ACTUAL_FOR_EXPECT", "KotlinNoActualForExpect")
expect object PingDbConstructor : RoomDatabaseConstructor<PingDb> {
  override fun initialize(): PingDb
}

@Database(entities = [Ping::class], version = 1, exportSchema = true)
@ConstructedBy(PingDbConstructor::class)
abstract class PingDb : RoomDatabase() {
  abstract fun pingDao(): PingDao
}
