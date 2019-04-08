package com.example.dflet.scripttanklogindemo;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewExtractsActivity extends AppCompatActivity {

    private ArrayList<String> usernames;
    private ArrayList<String> filenames;
    private HashMap<String, Map.Entry<String, String>> user_db_keys;
    private PublisherFileListAdapter adapter;
    private ListView list;
    private static User m_User;
    protected ScriptTankApplication myApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_extracts);
        myApp = (ScriptTankApplication)this.getApplicationContext();
        m_User = myApp.getM_User();
        myApp.setCurrActivity(this);
        list = findViewById(R.id.fileList);
        user_db_keys = new HashMap<String, Map.Entry<String, String>>();
        usernames = new ArrayList<>();
        filenames = new ArrayList<>();
        adapter = new PublisherFileListAdapter(this,
              usernames, filenames);
        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        grabUsersFiles().addOnCompleteListener(new OnCompleteListener<HashMap<String, Object>>() {
            @Override
            public void onComplete(@NonNull Task<HashMap<String, Object>> task) {
                if (task.isSuccessful()) {
                    HashMap<String, Object> results = task.getResult();
                    ArrayList<String> keys = (ArrayList<String>)results.get("ids");
                    ArrayList<String> usernamesTemp = (ArrayList<String>)results.get("usernames");
                    System.out.println(keys);
                    System.out.println(usernamesTemp);
                    Object obj = results.get("files");
                    for (String name : keys) {
                        int pos = keys.indexOf(name);
                        String user_name = usernamesTemp.get(pos);
                        ArrayList<String> fileNamesPerUser = ((ArrayList<ArrayList>)obj).
                                get(pos);
                        System.out.println(fileNamesPerUser);
                        if (fileNamesPerUser == null)
                            continue;
                        int num_of_files = fileNamesPerUser.size();
                        int count = 0;
                        while (count < num_of_files) {
                            String file = fileNamesPerUser.get(count);
                            System.out.println(file);
                            usernames.add(user_name);
                            filenames.add(file);
                            AbstractMap.SimpleEntry simpleEntry = new AbstractMap.SimpleEntry<>(user_name, name);
                            System.out.println(simpleEntry);
                            if (simpleEntry != null) {
                                user_db_keys.put(file,
                                        simpleEntry);
                            }
                            count++;
                        }
                    }
                    updateUserAdapter();
                } else {
                    Exception e = task.getException();
                    if (e instanceof FirebaseFunctionsException) {
                        FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                        FirebaseFunctionsException.Code code = ffe.getCode();
                        Object details = ffe.getDetails();
                    }
                    System.err.println(e);
                }
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    String filename = filenames.get(i) + ".pdf";
                    String serverPath = "Files/" +
                            user_db_keys.get(filenames.get(i)).getValue() +"/"+filename;
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + filename;
                    File file = new File(path);
                    FileOutputStream f = new FileOutputStream(file);
                    FirebaseStorage fs = FirebaseStorage.getInstance("gs://scripttankdemo.appspot.com");
                    StorageReference fRef = fs.getReference().child(serverPath);
                    fRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            System.out.println("File Received");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println("FAILURE TO DOWNLOAD");
                            return;
                        }
                    });
                    Toast.makeText(ViewExtractsActivity.this, "Downloading File...",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });

    }

    private void updateUserAdapter() {
        adapter.notifyDataSetChanged();
    }


    private Task<HashMap<String, Object>> grabUsersFiles() {
        Map<String, Object> data = new HashMap<>();
        data.put("push", true);

        FirebaseFunctions ff = FirebaseFunctions.getInstance();

        return ff
                .getHttpsCallable("grabUsersFiles")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, HashMap<String, Object>>() {
                    @Override
                    public HashMap<String, Object> then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HashMap<String, Object> result = (HashMap<String, Object>) task.getResult().getData();
                        return result;
                    }
                });
    }
}


