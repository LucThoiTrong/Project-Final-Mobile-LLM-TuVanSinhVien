package hcmute.edu.projectfinal.service;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceService {

    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    public SharedPreferenceService(Context context) {
        sharedPreferences = context.getSharedPreferences("health_tracker_app", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Phương thức thêm hoặc cập nhật key-value
    public void put(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    // Phương thức lấy giá trị của key, nếu không có trả về null
    public String get(String key) {
        return sharedPreferences.getString(key, null);
    }

    // Phương thức xóa key
    public void remove(String key) {
        editor.remove(key);
        editor.apply();
    }

    // Phương thức xóa tất cả key-value
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
