package br.com.rads.imworking.util;

import android.content.Context;
import android.text.format.Time;
import android.util.Log;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.rads.imworking.model.Check;

/**
 * Created by rafael_2 on 26/11/13.
 */
public class DataManager {

    private static final String TAG = "DataManager";
    private static DataManager instance = new DataManager();

    private DataManager() {
    }

    public static DataManager getInstance() {
        return instance;
    }

    public void saveCheck(Context context, Check check) {

        String filePath = check.getFilePath();
        String jsonFile = loadJSONFile(context, filePath);
        JSONObject jsonCheck = createJsonForCheck(jsonFile, check);
        saveJSON(context, filePath, jsonCheck);

    }

    private void saveJSON(Context context, String filePath, JSONObject jsonCheck) {

        FileOutputStream out = null;

        try {
            String jsonAsString = jsonCheck.toString();
            out = context.openFileOutput(filePath,
                    Context.MODE_PRIVATE);
            out.write(jsonAsString.getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "file not found to write");
        } catch (IOException e) {
            Log.d(TAG, "io error");
        } finally {
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private JSONObject createJsonForCheck(String jsonFile, Check check) {

        JSONObject root = null;

        String day = String.valueOf(check.getCheckIn().monthDay);

        try {

            JSONArray array;

            if (jsonFile == null || jsonFile.isEmpty()) {
                root = new JSONObject();
                array = new JSONArray();
            } else {
                root = new JSONObject(jsonFile);
                if (root.has(day)) {
                    array = root.getJSONArray(day);
                } else {
                    array = new JSONArray();
                }
            }

            if (array.length() == 0) {
                array.put(check.jsonValue());
            } else {
                Check lastCheck = Check.valueOf((JSONObject) array.get(array.length() - 1));
                if (lastCheck.getCheckOut() == null) {
                    array.put(array.length() - 1, check.jsonValue());
                } else {
                    array.put(check.jsonValue());
                }
            }

            root.put(day, array);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return root;
    }

    private String loadJSONFile(Context context, String filename) {

        StringBuilder sb = new StringBuilder();

        try {

            FileInputStream in = context.openFileInput(filename);
            byte[] buffer = new byte[1024];

            while ((in.read(buffer)) != -1) {
                sb.append(new String(buffer));
            }

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Arquivo não econtrado: " + e.toString());
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Erro ao carregar arquivo: " + e.toString());
            return null;
        }

        return sb.toString();
    }

    public List<Check> loadChecks(Context context, Time day) {
        List<Check> checks = new ArrayList<Check>();

        String fileNameOfChecksFile = day.year + "-" + day.month;
        String jsonFile = loadJSONFile(context, fileNameOfChecksFile);

        if (jsonFile != null && !jsonFile.isEmpty()) {
            try {
                JSONObject rootJSON = new JSONObject(jsonFile);
                JSONArray checksArray = rootJSON.getJSONArray(String.valueOf(day.monthDay));
                for (int i = 0; i < checksArray.length(); i++) {

                    JSONObject json = checksArray.getJSONObject(i);

                    Check check = Check.valueOf(json);
                    checks.add(check);

                }

            } catch (JSONException e) {
                Log.e(TAG, "Erro ao fazer Parse no JSON: " + e.toString() + "\n para o dia: " + day.toString());
            }
        }

        return checks;
    }

    public void clearData(Context context, Time day) {

        String file = day.year + "-" + day.month;
        File dir = new File(context.getFilesDir(), file);

        if (dir.exists()) {
            dir.delete();
            Log.d(TAG, dir.getAbsolutePath() + " is directory...deleting");
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
            Log.d(TAG, "Directory deleted");
        }

    }

    public long getHoursWorked(Context context, Time workedDay) {
        long hoursInMillis = 0;
//        List<Check> checksForWorkedDay = loadChecks(context, workedDay);
//        for (Check check : checksForWorkedDay) {
//            hoursInMillis += check.differenceBetweenInAndOut();
//            Log.d(TAG, "check=" + check.toString());
//        }

        return hoursInMillis;
    }
}
