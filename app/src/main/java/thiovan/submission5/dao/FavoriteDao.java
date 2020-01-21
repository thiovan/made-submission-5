package thiovan.submission5.dao;

import android.database.Cursor;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import thiovan.submission5.models.Movie;
import thiovan.submission5.models.TvShow;

@Dao
public interface FavoriteDao {
    @Insert
    void insertMovie(Movie movie);

    @Query("SELECT * FROM movie_table")
    List<Movie> getMovieList();

    @Query("SELECT * FROM movie_table WHERE id = :id")
    Movie findMovie(String id);

    @Delete
    void deleteMovie(Movie movie);

    @Insert
    void insertTvShow(TvShow tvShow);

    @Query("SELECT * FROM tv_show_table")
    List<TvShow> getTvShowList();

    @Query("SELECT * FROM tv_show_table WHERE id = :id")
    TvShow findTvShow(String id);

    @Delete
    void deleteTvShow(TvShow movie);

    @Query("SELECT * FROM movie_table")
    Cursor getMovieListCursor();
}
