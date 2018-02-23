package com.jcescobanez.fruits;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        query();
    }

    private Dialog loadingDialog;
    RequestQueue queue;

    public void query(){
        queue = Volley.newRequestQueue(this);
        loadingDialog = ProgressDialog.show(MainActivity.this, "", "Loading...");
        String url = "http://bshscare.com/app/api/android_api/get_fruits";

        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                loadingDialog.dismiss();

                try {

                    Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();

                    String status = new JSONObject(response).getString("status");

                    if (status.equals("failed")|status.equals("FAILED")){

                        Toast.makeText(MainActivity.this, "No data found.", Toast.LENGTH_LONG).show();

                    }
                    else{

                        data = new JSONObject(response).getJSONArray("data").toString();

                        showList();


                    }

                } catch (JSONException e) {

                    e.printStackTrace();

                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loadingDialog.dismiss();

                Toast.makeText(MainActivity.this, "Error connecting to the server. Check your connection or try again.", Toast.LENGTH_LONG).show();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", et_username.getText().toString());
//                params.put("password", et_password.getText().toString());

                return params;
            }
        };

        req.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        queue.add(req);
    }



    JSONArray array = null;
    ListView list;
    ArrayList<HashMap<String, String>> statusList;

    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "fruitname";
    private static final String TAG_PRICE = "fruitprice";


    protected void showList(){

        ListAdapter adapter;
        list = (ListView) findViewById(R.id.listView);
        statusList = new ArrayList<HashMap<String, String>>();

        try {
            array = new JSONArray(data);

            for(int i=0;i<array.length();i++) {
                JSONObject c = array.getJSONObject(i);


                String str_id = "ID: "+c.getString(TAG_ID);
                String str_name = "Fruit Name: " +c.getString(TAG_NAME);
                String str_price = "Fruit Price: " + c.getString(TAG_PRICE);
                HashMap<String, String> value = new HashMap<String, String>();

                value.put(TAG_ID, str_id);
                value.put(TAG_NAME, str_name);
                value.put(TAG_PRICE, str_price);

                statusList.add(value);


            }

            adapter = new SimpleAdapter(
                    MainActivity.this, statusList, R.layout.list_fruits,
                    new String[]{TAG_ID,TAG_NAME,TAG_PRICE},
                    new int[]{R.id.id,R.id.name, R.id.price}
            );

            list.setAdapter(adapter);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
