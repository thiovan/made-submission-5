package thiovan.submission5.databases;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import thiovan.submission5.dao.FavoriteDao;
import thiovan.submission5.models.Movie;
import thiovan.submission5.models.TvShow;

@Database(entities = {Movie.class, TvShow.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DB = "mydb";

    public abstract FavoriteDao getFavoriteDao();
}
