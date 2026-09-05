package com.accend.app.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AccendDao_Impl implements AccendDao {
  private final RoomDatabase __db;

  private final EntityUpsertionAdapter<UserEntity> __upsertionAdapterOfUserEntity;

  private final EntityUpsertionAdapter<DayProgressEntity> __upsertionAdapterOfDayProgressEntity;

  private final EntityUpsertionAdapter<WeeklyXpEntity> __upsertionAdapterOfWeeklyXpEntity;

  private final EntityUpsertionAdapter<QuoteEntity> __upsertionAdapterOfQuoteEntity;

  private final EntityUpsertionAdapter<AchievementEntity> __upsertionAdapterOfAchievementEntity;

  public AccendDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__upsertionAdapterOfUserEntity = new EntityUpsertionAdapter<UserEntity>(new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `users` (`id`,`displayName`,`joinDate`,`currentDay`,`totalXp`,`chosenSkillTrack`,`onboardingComplete`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindLong(3, entity.getJoinDate());
        statement.bindLong(4, entity.getCurrentDay());
        statement.bindLong(5, entity.getTotalXp());
        statement.bindString(6, entity.getChosenSkillTrack());
        final int _tmp = entity.getOnboardingComplete() ? 1 : 0;
        statement.bindLong(7, _tmp);
      }
    }, new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `users` SET `id` = ?,`displayName` = ?,`joinDate` = ?,`currentDay` = ?,`totalXp` = ?,`chosenSkillTrack` = ?,`onboardingComplete` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getDisplayName());
        statement.bindLong(3, entity.getJoinDate());
        statement.bindLong(4, entity.getCurrentDay());
        statement.bindLong(5, entity.getTotalXp());
        statement.bindString(6, entity.getChosenSkillTrack());
        final int _tmp = entity.getOnboardingComplete() ? 1 : 0;
        statement.bindLong(7, _tmp);
        statement.bindString(8, entity.getId());
      }
    });
    this.__upsertionAdapterOfDayProgressEntity = new EntityUpsertionAdapter<DayProgressEntity>(new EntityInsertionAdapter<DayProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `DayProgressEntity` (`userId`,`dayNumber`,`pillarId`,`subTaskId`,`completed`,`completedAt`,`notes`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayProgressEntity entity) {
        statement.bindString(1, entity.getUserId());
        statement.bindLong(2, entity.getDayNumber());
        statement.bindString(3, entity.getPillarId());
        statement.bindString(4, entity.getSubTaskId());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getCompletedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCompletedAt());
        }
        statement.bindString(7, entity.getNotes());
      }
    }, new EntityDeletionOrUpdateAdapter<DayProgressEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `DayProgressEntity` SET `userId` = ?,`dayNumber` = ?,`pillarId` = ?,`subTaskId` = ?,`completed` = ?,`completedAt` = ?,`notes` = ? WHERE `userId` = ? AND `dayNumber` = ? AND `pillarId` = ? AND `subTaskId` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DayProgressEntity entity) {
        statement.bindString(1, entity.getUserId());
        statement.bindLong(2, entity.getDayNumber());
        statement.bindString(3, entity.getPillarId());
        statement.bindString(4, entity.getSubTaskId());
        final int _tmp = entity.getCompleted() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getCompletedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getCompletedAt());
        }
        statement.bindString(7, entity.getNotes());
        statement.bindString(8, entity.getUserId());
        statement.bindLong(9, entity.getDayNumber());
        statement.bindString(10, entity.getPillarId());
        statement.bindString(11, entity.getSubTaskId());
      }
    });
    this.__upsertionAdapterOfWeeklyXpEntity = new EntityUpsertionAdapter<WeeklyXpEntity>(new EntityInsertionAdapter<WeeklyXpEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `weekly_xp` (`userId`,`weekNumber`,`xp`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeeklyXpEntity entity) {
        statement.bindString(1, entity.getUserId());
        statement.bindLong(2, entity.getWeekNumber());
        statement.bindLong(3, entity.getXp());
      }
    }, new EntityDeletionOrUpdateAdapter<WeeklyXpEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `weekly_xp` SET `userId` = ?,`weekNumber` = ?,`xp` = ? WHERE `userId` = ? AND `weekNumber` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final WeeklyXpEntity entity) {
        statement.bindString(1, entity.getUserId());
        statement.bindLong(2, entity.getWeekNumber());
        statement.bindLong(3, entity.getXp());
        statement.bindString(4, entity.getUserId());
        statement.bindLong(5, entity.getWeekNumber());
      }
    });
    this.__upsertionAdapterOfQuoteEntity = new EntityUpsertionAdapter<QuoteEntity>(new EntityInsertionAdapter<QuoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `quotes` (`id`,`text`,`author`,`category`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getText());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getCategory());
      }
    }, new EntityDeletionOrUpdateAdapter<QuoteEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `quotes` SET `id` = ?,`text` = ?,`author` = ?,`category` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QuoteEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getText());
        statement.bindString(3, entity.getAuthor());
        statement.bindString(4, entity.getCategory());
        statement.bindLong(5, entity.getId());
      }
    });
    this.__upsertionAdapterOfAchievementEntity = new EntityUpsertionAdapter<AchievementEntity>(new EntityInsertionAdapter<AchievementEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `achievements` (`id`,`name`,`description`,`icon`,`unlocked`,`unlockedAt`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AchievementEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getIcon());
        final int _tmp = entity.getUnlocked() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getUnlockedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getUnlockedAt());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<AchievementEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `achievements` SET `id` = ?,`name` = ?,`description` = ?,`icon` = ?,`unlocked` = ?,`unlockedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AchievementEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getDescription());
        statement.bindString(4, entity.getIcon());
        final int _tmp = entity.getUnlocked() ? 1 : 0;
        statement.bindLong(5, _tmp);
        if (entity.getUnlockedAt() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getUnlockedAt());
        }
        statement.bindString(7, entity.getId());
      }
    });
  }

  @Override
  public Object upsertUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfUserEntity.upsert(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertProgress(final DayProgressEntity progress,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfDayProgressEntity.upsert(progress);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertWeeklyXp(final WeeklyXpEntity weeklyXp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfWeeklyXpEntity.upsert(weeklyXp);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertQuotes(final List<QuoteEntity> quotes,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfQuoteEntity.upsert(quotes);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAchievements(final List<AchievementEntity> achievements,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfAchievementEntity.upsert(achievements);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserEntity> observeUser() {
    final String _sql = "SELECT * FROM users WHERE id = 'local-user'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDisplayName = CursorUtil.getColumnIndexOrThrow(_cursor, "displayName");
          final int _cursorIndexOfJoinDate = CursorUtil.getColumnIndexOrThrow(_cursor, "joinDate");
          final int _cursorIndexOfCurrentDay = CursorUtil.getColumnIndexOrThrow(_cursor, "currentDay");
          final int _cursorIndexOfTotalXp = CursorUtil.getColumnIndexOrThrow(_cursor, "totalXp");
          final int _cursorIndexOfChosenSkillTrack = CursorUtil.getColumnIndexOrThrow(_cursor, "chosenSkillTrack");
          final int _cursorIndexOfOnboardingComplete = CursorUtil.getColumnIndexOrThrow(_cursor, "onboardingComplete");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpDisplayName;
            _tmpDisplayName = _cursor.getString(_cursorIndexOfDisplayName);
            final long _tmpJoinDate;
            _tmpJoinDate = _cursor.getLong(_cursorIndexOfJoinDate);
            final int _tmpCurrentDay;
            _tmpCurrentDay = _cursor.getInt(_cursorIndexOfCurrentDay);
            final int _tmpTotalXp;
            _tmpTotalXp = _cursor.getInt(_cursorIndexOfTotalXp);
            final String _tmpChosenSkillTrack;
            _tmpChosenSkillTrack = _cursor.getString(_cursorIndexOfChosenSkillTrack);
            final boolean _tmpOnboardingComplete;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfOnboardingComplete);
            _tmpOnboardingComplete = _tmp != 0;
            _result = new UserEntity(_tmpId,_tmpDisplayName,_tmpJoinDate,_tmpCurrentDay,_tmpTotalXp,_tmpChosenSkillTrack,_tmpOnboardingComplete);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DayProgressEntity>> observeDay(final int day) {
    final String _sql = "SELECT * FROM dayprogressentity WHERE userId = 'local-user' AND dayNumber = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, day);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"dayprogressentity"}, new Callable<List<DayProgressEntity>>() {
      @Override
      @NonNull
      public List<DayProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "dayNumber");
          final int _cursorIndexOfPillarId = CursorUtil.getColumnIndexOrThrow(_cursor, "pillarId");
          final int _cursorIndexOfSubTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "subTaskId");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DayProgressEntity> _result = new ArrayList<DayProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayProgressEntity _item;
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final int _tmpDayNumber;
            _tmpDayNumber = _cursor.getInt(_cursorIndexOfDayNumber);
            final String _tmpPillarId;
            _tmpPillarId = _cursor.getString(_cursorIndexOfPillarId);
            final String _tmpSubTaskId;
            _tmpSubTaskId = _cursor.getString(_cursorIndexOfSubTaskId);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new DayProgressEntity(_tmpUserId,_tmpDayNumber,_tmpPillarId,_tmpSubTaskId,_tmpCompleted,_tmpCompletedAt,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<DayProgressEntity>> observeAllProgress() {
    final String _sql = "SELECT * FROM dayprogressentity WHERE userId = 'local-user'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"dayprogressentity"}, new Callable<List<DayProgressEntity>>() {
      @Override
      @NonNull
      public List<DayProgressEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfDayNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "dayNumber");
          final int _cursorIndexOfPillarId = CursorUtil.getColumnIndexOrThrow(_cursor, "pillarId");
          final int _cursorIndexOfSubTaskId = CursorUtil.getColumnIndexOrThrow(_cursor, "subTaskId");
          final int _cursorIndexOfCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "completed");
          final int _cursorIndexOfCompletedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "completedAt");
          final int _cursorIndexOfNotes = CursorUtil.getColumnIndexOrThrow(_cursor, "notes");
          final List<DayProgressEntity> _result = new ArrayList<DayProgressEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DayProgressEntity _item;
            final String _tmpUserId;
            _tmpUserId = _cursor.getString(_cursorIndexOfUserId);
            final int _tmpDayNumber;
            _tmpDayNumber = _cursor.getInt(_cursorIndexOfDayNumber);
            final String _tmpPillarId;
            _tmpPillarId = _cursor.getString(_cursorIndexOfPillarId);
            final String _tmpSubTaskId;
            _tmpSubTaskId = _cursor.getString(_cursorIndexOfSubTaskId);
            final boolean _tmpCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfCompleted);
            _tmpCompleted = _tmp != 0;
            final Long _tmpCompletedAt;
            if (_cursor.isNull(_cursorIndexOfCompletedAt)) {
              _tmpCompletedAt = null;
            } else {
              _tmpCompletedAt = _cursor.getLong(_cursorIndexOfCompletedAt);
            }
            final String _tmpNotes;
            _tmpNotes = _cursor.getString(_cursorIndexOfNotes);
            _item = new DayProgressEntity(_tmpUserId,_tmpDayNumber,_tmpPillarId,_tmpSubTaskId,_tmpCompleted,_tmpCompletedAt,_tmpNotes);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<QuoteEntity>> observeQuotes() {
    final String _sql = "SELECT * FROM quotes ORDER BY id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"quotes"}, new Callable<List<QuoteEntity>>() {
      @Override
      @NonNull
      public List<QuoteEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfAuthor = CursorUtil.getColumnIndexOrThrow(_cursor, "author");
          final int _cursorIndexOfCategory = CursorUtil.getColumnIndexOrThrow(_cursor, "category");
          final List<QuoteEntity> _result = new ArrayList<QuoteEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QuoteEntity _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final String _tmpAuthor;
            _tmpAuthor = _cursor.getString(_cursorIndexOfAuthor);
            final String _tmpCategory;
            _tmpCategory = _cursor.getString(_cursorIndexOfCategory);
            _item = new QuoteEntity(_tmpId,_tmpText,_tmpAuthor,_tmpCategory);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<AchievementEntity>> observeAchievements() {
    final String _sql = "SELECT * FROM achievements ORDER BY unlocked DESC, id";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"achievements"}, new Callable<List<AchievementEntity>>() {
      @Override
      @NonNull
      public List<AchievementEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfIcon = CursorUtil.getColumnIndexOrThrow(_cursor, "icon");
          final int _cursorIndexOfUnlocked = CursorUtil.getColumnIndexOrThrow(_cursor, "unlocked");
          final int _cursorIndexOfUnlockedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "unlockedAt");
          final List<AchievementEntity> _result = new ArrayList<AchievementEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AchievementEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpIcon;
            _tmpIcon = _cursor.getString(_cursorIndexOfIcon);
            final boolean _tmpUnlocked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUnlocked);
            _tmpUnlocked = _tmp != 0;
            final Long _tmpUnlockedAt;
            if (_cursor.isNull(_cursorIndexOfUnlockedAt)) {
              _tmpUnlockedAt = null;
            } else {
              _tmpUnlockedAt = _cursor.getLong(_cursorIndexOfUnlockedAt);
            }
            _item = new AchievementEntity(_tmpId,_tmpName,_tmpDescription,_tmpIcon,_tmpUnlocked,_tmpUnlockedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
