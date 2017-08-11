package com.example.kasiopec.tshirtapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {


    private ListView tShirtListView;
    List<TshirtModel> tShirtModelList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loading data in async task to avoid UI freeze
        new JSONTask().execute("http://mock-shirt-backend.getsandbox.com/shirts");

        //Defining listview + on click listener to go for Tshirt details screen
        tShirtListView = (ListView) findViewById(R.id.tShirtListView);
        tShirtListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getBaseContext(), TshirtDetails.class);
                intent.putExtra("selectedTshirtObj", (Serializable) tShirtListView.getAdapter().getItem(i));
                startActivity(intent);
            }

        });

        //Button that redirects to the My cart screen
        Button bCart = (Button) findViewById(R.id.button_cart);
        bCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), BasketActivity.class);
                startActivity(intent);
            }
        });


        //Edit text to filter out Tshirts based on size
        EditText sizeText = (EditText) findViewById(R.id.editT_size);
        EditText colourText = (EditText) findViewById(R.id.editText_colour);

        sizeText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {

                    //Creating new arraylist for adapter via lambda expression
                    List<TshirtModel> sizeFiltredTshirts = tShirtModelList.stream().filter(p -> p.getSize().equals(sizeText.getText().toString())).
                            collect(Collectors.toList());

                    TshirAdapter adapter;
                    //Use default array list in case there was no input otherwise apply filter
                    if(sizeText.getText().toString().equals("")){
                        adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, tShirtModelList);

                    }else{
                        adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, sizeFiltredTshirts);

                    }

                    tShirtListView.setAdapter(adapter);
                    return true;
                }
                return false;
            }
        });
        //Edit text to filter out Tshirts based on colour
        colourText.setOnKeyListener(new View.OnKeyListener() {
            //adding enter click to the edittext
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                        (i == KeyEvent.KEYCODE_ENTER)) {

                    //Creating new arraylist for adapter via lambda expression
                    List<TshirtModel> colourFiltredTshirts = tShirtModelList.stream().filter(p -> p.getColour().equals(colourText.getText().toString())).
                            collect(Collectors.toList());
                    TshirAdapter adapter;

                    //Use default array list in case there was no input otherwise apply filter
                    if(colourText.getText().toString().equals("")){
                        adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, tShirtModelList);

                    }else{
                        adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, colourFiltredTshirts);

                    }

                    tShirtListView.setAdapter(adapter);
                    return true;
                }
                return false;
            }
        });






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


                //converting json object into arraylist for listview
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

            TshirAdapter adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, s);
            tShirtListView.setAdapter(adapter);
        }
    }


    //custom adapter for the listview
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


