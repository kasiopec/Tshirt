package com.example.kasiopec.tshirtapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kasiopec.tshirtapp.models.BasketModel;
import com.example.kasiopec.tshirtapp.models.TshirtModel;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

public class BasketActivity extends AppCompatActivity {

    TshirAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        BasketModel basket = new BasketModel();
        List<TshirtModel> tList = basket.getBasketList();


        ListView basketList = (ListView) findViewById(R.id.listViewBasket);
        basketList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tList.remove(basketList.indexOfChild(view));
                restartActivity();
            }
        });
        TextView totalPrice = (TextView) findViewById(R.id.textView_totalP);
        totalPrice.setText("Total price: "+String.valueOf(basket.countTotalPrice()));

        Button makeOrder = (Button) findViewById(R.id.button_makeOrder);
        makeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(tList !=  null){
            adapter = new TshirAdapter(getApplicationContext(), R.layout.tshirt_list_layout, tList);
            basketList.setAdapter(adapter);
        }


        if(basket.getBasketList()!= null){
            System.out.println("Here is what in the basket: "+basket.getBasketList());

        }



    }

    public void restartActivity(){
        Intent mIntent = getIntent();
        finish();
        startActivity(mIntent);
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
