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

    public DataManager() {
    }

    public static DataManager getInstance() {
        return instance;
    }

    public void saveCheck(Context context, Check check) {

        try {
            String filePath = context.getFilesDir().toString().concat(File.separator + check.getFileName());
            createFileIfDoesntExist(filePath);
            String jsonFile = loadJSONFileAsString(filePath);
            JSONObject jsonCheck = addCheckAtJsonFile(check,jsonFile);
            saveJsonCheckAtFile(jsonCheck, filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveJsonCheckAtFile(JSONObject jsonCheck, String filePath) throws IOException {
        Files.write(jsonCheck.toString().getBytes(), new File(filePath));
    }

    private JSONObject addCheckAtJsonFile(Check check,String jsonFile) {

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

    private String loadJSONFileAsString(String filename)  {
        File jsonFile = new File(filename);

        try {
            return Files.toString(jsonFile, Charsets.UTF_8);
        } catch (IOException e) {
            return null;
        }
    }


    //refatorar clean code
    public List<Check> loadChecks(Context context, Time day) {
        List<Check> checks = new ArrayList<Check>();

        String fileNameOfChecksFile = context.getFilesDir().toString().concat( File.separator + day.year + "-" + day.month);

        String jsonFile =  loadJSONFileAsString(fileNameOfChecksFile);

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

    public void createFileIfDoesntExist(String filePath) throws IOException {
        File checkFile = new File(filePath);
        if (!checkFile.exists()){
            Files.write("",checkFile,Charsets.UTF_8);
        }
    }
}
