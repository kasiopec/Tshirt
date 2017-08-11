package com.example.kasiopec.tshirtapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kasiopec.tshirtapp.models.BasketModel;
import com.example.kasiopec.tshirtapp.models.TshirtModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class TshirtDetails extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tshirt_details);

        final List<TshirtModel> cart = BasketModel.getCart();

        TextView name;
        TextView price;
        TextView size;
        TextView colour;
        TextView quantity;
        ImageView tImage;


        size = (TextView) findViewById(R.id.textView_size_details);
        price = (TextView) findViewById(R.id.textView_price_details);
        colour = (TextView) findViewById(R.id.textView_colour_details);
        name = (TextView) findViewById(R.id.textView_name_details);
        quantity = (TextView) findViewById(R.id.textView_quant_details);
        tImage = (ImageView) findViewById(R.id.tshirtImage);

        TshirtModel tShirt = (TshirtModel) getIntent().getSerializableExtra("selectedTshirtObj");

        Integer priceValue = tShirt.getPrice();
        String sizeValue = tShirt.getSize();
        String colourValue = tShirt.getColour();
        String nameValue = tShirt.getName();
        Integer quantityValue = tShirt.getQuantity();
        String pictureValue = tShirt.getPicture();

        name.setText(nameValue);
        price.setText("Price: "+String.valueOf(priceValue));
        size.setText("Size: "+sizeValue);
        colour.setText("Colour:"+colourValue);
        quantity.setText("Quantity: "+String.valueOf(quantityValue));

        //load picture on background and assign  to the image view
        new ImageLoadTask(pictureValue, tImage).execute();

        Button addToBasket = (Button) findViewById(R.id.button_addTB);

        addToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cart.add(tShirt);
                System.out.println("ADDED THIS STUFF: "+tShirt.toString());
                System.out.println("AND HERE IS WHAT IN THE BASKET NOW:"+cart.contains(tShirt));

            }
        });








    }
    //Loading image from the URL that was in the JSON file, in the async task, to avoid UI freezes
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }

}
