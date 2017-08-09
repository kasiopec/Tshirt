package com.example.kasiopec.tshirtapp;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.kasiopec.tshirtapp.models.TshirtModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private ListView tShirtListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new JSONTask().execute("http://mock-shirt-backend.getsandbox.com/shirts");

        tShirtListView = (ListView) findViewById(R.id.tShirtListView);


    }

    //class to handle JSON connection on the background task
    public class JSONTask extends AsyncTask<String, String, List<TshirtModel>>{

        @Override
        protected List<TshirtModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader breader = null;

            try{
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream inputStream = connection.getInputStream();
                breader = new BufferedReader( new InputStreamReader(inputStream));

                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = breader.readLine()) != null){
                    buffer.append(line);
                }

                String jsonString = buffer.toString();
                JSONArray parentArray = new JSONArray(jsonString);

                List<TshirtModel> tShirtModelList = new ArrayList<>();


                StringBuffer tShirtListString = new StringBuffer();

                for (int i = 0; i < parentArray.length(); i++) {

                    JSONObject jasonTshirtObj = parentArray.getJSONObject(i);

                    TshirtModel tshirtModel = new TshirtModel();
                    tshirtModel.setId(jasonTshirtObj.getInt("id"));
                    tshirtModel.setPrice(jasonTshirtObj.getInt("price"));
                    tshirtModel.setColour(jasonTshirtObj.getString("colour"));
                    tshirtModel.setPicture(jasonTshirtObj.getString("picture"));
                    tshirtModel.setSize(jasonTshirtObj.getString("size"));
                    tshirtModel.setName(jasonTshirtObj.getString("name"));

                    if(jasonTshirtObj.has("quantity")){
                        tshirtModel.setQuantity(jasonTshirtObj.getInt("quantity"));
                    }else{
                        tshirtModel.setQuantity(0);
                    }

                    //add JSON object to the array list
                    tShirtModelList.add(tshirtModel);

                }
                return tShirtModelList;





            }catch (MalformedURLException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    connection.disconnect();
                }
                try {
                    if(breader !=null){
                        breader.close();
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
            //if there is no connection
            return null;
        }

        @Override
        protected void onPostExecute(List<TshirtModel> s) {
            super.onPostExecute(s);
            //ListView add here

            TshirAdapter adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, s);

            tShirtListView.setAdapter(adapter);
        }
    }

    public class TshirAdapter extends ArrayAdapter{

        private final LayoutInflater inflater;
        private List<TshirtModel> tshirtModelList;
        private int resource;

        public TshirAdapter(@NonNull Context context, @LayoutRes int resource, List<TshirtModel> objects) {
            super(context, resource, objects);
            tshirtModelList= objects;
            this.resource = resource;
            inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null){
                convertView = inflater.inflate(resource, null);
            }

            TextView name;
            TextView colour;
            TextView size;

            name = (TextView) convertView.findViewById(R.id.textView_Name);
            colour = (TextView) convertView.findViewById(R.id.textView_Colour);
            size = (TextView) convertView.findViewById(R.id.textView_Size);

            name.setText(tshirtModelList.get(position).getName());
            colour.setText(tshirtModelList.get(position).getColour());
            size.setText(tshirtModelList.get(position).getSize());

            return convertView;


        }
    }

}


