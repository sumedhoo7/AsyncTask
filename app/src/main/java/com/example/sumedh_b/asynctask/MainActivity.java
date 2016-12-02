package com.example.sumedh_b.asynctask;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String MyTag = MainActivity.class.getSimpleName();
    private ProgressDialog dia;
    private ListView list;
    private static String url = "http://api.androidhive.info/contacts/";
    ArrayList<HashMap<String, String>> conList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conList = new ArrayList<>();
        list = (ListView) findViewById(R.id.list);
        new GetContacts().execute();
    }

    private class GetContacts extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dia = new ProgressDialog(MainActivity.this);
            dia.setMessage("Please wait...");
            dia.setCancelable(false);
            dia.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();
            String jsonStr = sh.makeServiceCall(url);
            Log.e(MyTag, "Response from url: " + jsonStr);
            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    JSONArray contacts = jsonObj.getJSONArray("contacts");
                    for (int i = 0; i < contacts.length(); i++)
                    {
                        JSONObject c = contacts.getJSONObject(i);
                        String id = c.getString("id");
                        String name = c.getString("name");
                        String email = c.getString("email");
                        JSONObject phone = c.getJSONObject("phone");
                        String mobile = phone.getString("mobile");
                        HashMap<String, String> contact = new HashMap<>();
                        contact.put("id", id);
                        contact.put("name", name);
                        contact.put("email", email);
                        contact.put("mobile", mobile);
                        conList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(MyTag, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(MyTag, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (dia.isShowing())
                dia.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, conList,
                    R.layout.list_item, new String[]{"name", "email",
                    "mobile"}, new int[]{R.id.name,
                    R.id.email, R.id.mobile});
            list.setAdapter(adapter);
        }
    }
}