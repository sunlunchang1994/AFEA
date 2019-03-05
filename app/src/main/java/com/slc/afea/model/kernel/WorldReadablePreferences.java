package com.slc.afea.model.kernel;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class WorldReadablePreferences implements SharedPreferences {
    private static final String NAME_SHARED_PREFS = "shared_prefs";
    private static final String NAME_SHARED_PREFS_SUFFIX = ".xml";
    private Context mContext;
    private EditorWrapper mEditorWrapper;
    private Handler mHandler;
    private OnPreferencesCommitedListener mOnPreferencesCommitedListener;
    private OnSharedPreferenceChangeCommitedListener mOnSharedPreferenceChangeCommitedListener;
    private Runnable mPreferencesCommitedRunnable = new Runnable() {
        public void run() {
            if (WorldReadablePreferences.this.mOnPreferencesCommitedListener != null) {
                WorldReadablePreferences.this.mOnPreferencesCommitedListener.onPreferencesCommited();
                WorldReadablePreferences.this.mOnPreferencesCommitedListener = null;
            }
        }
    };
    private SharedPreferences mPrefs;
    private String mPrefsName;
    private boolean mSelfAttrChange;
    private Runnable mSharedPreferenceChangeCommitedRunnable = new Runnable() {
        public void run() {
            if (WorldReadablePreferences.this.mOnSharedPreferenceChangeCommitedListener != null) {
                WorldReadablePreferences.this.mOnSharedPreferenceChangeCommitedListener.onSharedPreferenceChangeCommited();
            }
        }
    };

    public class EditorWrapper implements Editor {
        private Editor mEditor;

        public EditorWrapper(Editor editor) {
            this.mEditor = editor;
        }

        @Override
        public EditorWrapper putString(String key, String value) {
            this.mEditor.putString(key, value);
            return this;
        }

        @Override
        public EditorWrapper putStringSet(String key, Set<String> values) {
            this.mEditor.putStringSet(key, values);
            return this;
        }

        @Override
        public EditorWrapper putInt(String key, int value) {
            this.mEditor.putInt(key, value);
            return this;
        }

        public EditorWrapper putLong(String key, long value) {
            this.mEditor.putLong(key, value);
            return this;
        }

        @Override
        public EditorWrapper putFloat(String key, float value) {
            this.mEditor.putFloat(key, value);
            return this;
        }

        @Override
        public EditorWrapper putBoolean(String key, boolean value) {
            this.mEditor.putBoolean(key, value);
            return this;
        }

        @Override
        public EditorWrapper remove(String key) {
            this.mEditor.remove(key);
            return this;
        }

        @Override
        public EditorWrapper clear() {
            this.mEditor.clear();
            return this;
        }

        @Override
        public boolean commit() {
            return commit(null);
        }

        public boolean commit(OnPreferencesCommitedListener listener) {
            WorldReadablePreferences.this.mOnPreferencesCommitedListener = listener;
            return this.mEditor.commit();
        }

        @Override
        public void apply() {
            throw new UnsupportedOperationException("apply() not supported. Use commit() instead.");
        }
    }

    public interface OnPreferencesCommitedListener {
        void onPreferencesCommited();
    }

    public interface OnSharedPreferenceChangeCommitedListener {
        void onSharedPreferenceChangeCommited();
    }

    public WorldReadablePreferences(Context ctx, String prefsName) {
        this.mContext = ctx;
        this.mPrefsName = prefsName;
        this.mPrefs = ctx.getSharedPreferences(this.mPrefsName, 0);
        this.mHandler = new Handler();
        maybePreCreateFile();
        fixPermissions(true);
        registerFileObserver();
    }

    @Override
    public boolean contains(String key) {
        return this.mPrefs.contains(key);
    }

    @SuppressLint({"CommitPrefEdits"})
    public EditorWrapper edit() {
        if (this.mEditorWrapper == null) {
            this.mEditorWrapper = new EditorWrapper(this.mPrefs.edit());
        }
        return this.mEditorWrapper;
    }

    @Override
    public Map<String, ?> getAll() {
        return this.mPrefs.getAll();
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        return this.mPrefs.getBoolean(key, defValue);
    }

    @Override
    public float getFloat(String key, float defValue) {
        return this.mPrefs.getFloat(key, defValue);
    }

    @Override
    public int getInt(String key, int defValue) {
        return this.mPrefs.getInt(key, defValue);
    }

    @Override
    public long getLong(String key, long defValue) {
        return this.mPrefs.getLong(key, defValue);
    }

    @Override
    public String getString(String key, String defValue) {
        return this.mPrefs.getString(key, defValue);
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValues) {
        return this.mPrefs.getStringSet(key, defValues);
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        this.mPrefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        this.mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void setOnSharedPreferenceChangeCommitedListener(OnSharedPreferenceChangeCommitedListener listener) {
        this.mOnSharedPreferenceChangeCommitedListener = listener;
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void maybePreCreateFile() {
        try {
            File sharedPrefsFolder = new File(this.mContext.getDataDir().getAbsolutePath() + File.separator + NAME_SHARED_PREFS);
            Log.e("maybePreCreateFile", this.mContext.getDataDir().getAbsolutePath() + File.separator + NAME_SHARED_PREFS);
            if (!sharedPrefsFolder.exists()) {
                sharedPrefsFolder.mkdir();
                sharedPrefsFolder.setExecutable(true, false);
                sharedPrefsFolder.setReadable(true, false);
            }
            File f = new File(sharedPrefsFolder.getAbsolutePath() + File.separator + this.mPrefsName + NAME_SHARED_PREFS_SUFFIX);
            if (!f.exists()) {
                f.createNewFile();
                f.setReadable(true, false);
            }
        } catch (Exception e) {
            Log.e("GravityBox", "Error pre-creating prefs file " + this.mPrefsName + ": " + e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void fixPermissions(boolean force) {
        File dataDir = new File(this.mContext.getDataDir().getAbsolutePath());
        if (dataDir.exists()) {
            dataDir.setExecutable(true, false);
            dataDir.setReadable(true, false);
            File sharedPrefsFolder = new File(dataDir.getAbsolutePath() + File.separator + NAME_SHARED_PREFS);
            if (sharedPrefsFolder.exists()) {
                sharedPrefsFolder.setExecutable(true, false);
                sharedPrefsFolder.setReadable(true, false);
                File f = new File(sharedPrefsFolder.getAbsolutePath() + File.separator + this.mPrefsName + NAME_SHARED_PREFS_SUFFIX);
                if (f.exists()) {
                    boolean z;
                    if (force) {
                        z = false;
                    } else {
                        z = true;
                    }
                    this.mSelfAttrChange = z;
                    f.setReadable(true, false);
                }
            }
        }
    }

    private void registerFileObserver() {
        new FileObserver(this.mContext.getDataDir() + File.separator + NAME_SHARED_PREFS, 12) {
            public void onEvent(int event, String path) {
                if ((event & 4) != 0) {
                    WorldReadablePreferences.this.onFileAttributesChanged(path);
                }
                if ((event & 8) != 0) {
                    WorldReadablePreferences.this.onFileUpdated(path);
                }
            }
        }.startWatching();
    }

    private void fixPermissions() {
        fixPermissions(false);
    }

    private void onFileAttributesChanged(String path) {
        if (path != null && path.endsWith(this.mPrefsName + NAME_SHARED_PREFS_SUFFIX)) {
            if (this.mSelfAttrChange) {
                this.mSelfAttrChange = false;
            } else {
                fixPermissions();
            }
        }
    }

    private void onFileUpdated(String path) {
        if (path != null && path.endsWith(this.mPrefsName + NAME_SHARED_PREFS_SUFFIX)) {
            if (this.mOnPreferencesCommitedListener != null) {
                postOnPreferencesCommited();
            } else if (this.mOnSharedPreferenceChangeCommitedListener != null) {
                postOnSharedPreferenceChangeCommited();
            }
        }
    }

    private void postOnPreferencesCommited() {
        this.mHandler.removeCallbacks(this.mPreferencesCommitedRunnable);
        this.mHandler.postDelayed(this.mPreferencesCommitedRunnable, 100);
    }

    private void postOnSharedPreferenceChangeCommited() {
        this.mHandler.removeCallbacks(this.mSharedPreferenceChangeCommitedRunnable);
        this.mHandler.postDelayed(this.mSharedPreferenceChangeCommitedRunnable, 100);
    }
}
