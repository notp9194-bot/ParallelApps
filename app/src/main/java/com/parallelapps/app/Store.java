package com.parallelapps.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Store {
    public static class Clone {
        public String name, pkg, label;
    }

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences("parallel", Context.MODE_PRIVATE);
    }

    public static List<Clone> load(Context c) {
        List<Clone> list = new ArrayList<>();
        try {
            JSONArray a = new JSONArray(sp(c).getString("clones", "[]"));
            for (int i = 0; i < a.length(); i++) {
                JSONObject o = a.getJSONObject(i);
                Clone k = new Clone();
                k.name = o.getString("name");
                k.pkg = o.getString("pkg");
                k.label = o.optString("label", "");
                list.add(k);
            }
        } catch (Exception ignored) { }
        return list;
    }

    public static void save(Context c, List<Clone> list) {
        JSONArray a = new JSONArray();
        try {
            for (Clone k : list) {
                JSONObject o = new JSONObject();
                o.put("name", k.name);
                o.put("pkg", k.pkg);
                o.put("label", k.label);
                a.put(o);
            }
        } catch (Exception ignored) { }
        sp(c).edit().putString("clones", a.toString()).apply();
    }

    public static boolean has(Context c, String pkg) {
        for (Clone k : load(c)) if (k.pkg.equals(pkg)) return true;
        return false;
    }

    public static boolean add(Context c, String baseName, String pkg) {
        if (has(c, pkg)) return false;
        List<Clone> list = load(c);
        Clone k = new Clone();
        k.name = baseName + " 2";
        k.pkg = pkg;
        k.label = "Parallel copy (work profile)";
        list.add(k);
        save(c, list);
        return true;
    }

    public static void remove(Context c, int index) {
        List<Clone> list = load(c);
        if (index >= 0 && index < list.size()) list.remove(index);
        save(c, list);
    }

    public static void rename(Context c, int index, String name) {
        List<Clone> list = load(c);
        if (index >= 0 && index < list.size()) list.get(index).name = name;
        save(c, list);
    }

    public static boolean onboarded(Context c) { return sp(c).getBoolean("onboarded", false); }
    public static void setOnboarded(Context c) { sp(c).edit().putBoolean("onboarded", true).apply(); }
    public static boolean appLock(Context c) { return sp(c).getBoolean("applock", false); }
    public static void setAppLock(Context c, boolean v) { sp(c).edit().putBoolean("applock", v).apply(); }
    public static int theme(Context c) { return sp(c).getInt("theme", 2); } // 0 light 1 dark 2 system
    public static void setTheme(Context c, int v) { sp(c).edit().putInt("theme", v).apply(); }
}
