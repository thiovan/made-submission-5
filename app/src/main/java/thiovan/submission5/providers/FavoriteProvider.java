package thiovan.submission5.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Room;
import thiovan.submission5.dao.FavoriteDao;
import thiovan.submission5.databases.AppDatabase;
import thiovan.submission5.models.Movie;

public class FavoriteProvider extends ContentProvider {

    private static final String AUTHORITY = "thiovan.submission5";
    private static final int FAVORITE = 1;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private FavoriteDao favoriteDao;

    static {
        sUriMatcher.addURI(AUTHORITY, Movie.TABLE_NAME, FAVORITE);
    }

    public FavoriteProvider() {
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onCreate() {
        AppDatabase database = Room.databaseBuilder(Objects.requireNonNull(getContext()), AppDatabase.class, AppDatabase.DB)
                .allowMainThreadQueries()
                .build();
        favoriteDao = database.getFavoriteDao();
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        if (sUriMatcher.match(uri) == FAVORITE) {
            Context mContext = getContext();
            assert mContext != null;
            cursor = favoriteDao.getMovieListCursor();
            cursor.setNotificationUri(mContext.getContentResolver(), uri);

        }
        return cursor;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
