package com.example.kasiopec.tshirtapp.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kasiopec on 8/11/2017.
 */

public class BasketModel implements Serializable{

    private static List<TshirtModel> basketList;


    public List<TshirtModel> getBasketList() {
        return basketList;
    }


    public void removeFromBasket(long i)
    {
        basketList.remove(i);

    }

    public static List<TshirtModel> getCart() {
        if(basketList == null) {
            basketList = new ArrayList<TshirtModel>();
        }

        return basketList;
    }

    public int countTotalPrice(){
        int sum =0;
         if(basketList == null){
             return sum;
         }else{
             for (int i = 0; i < basketList.size(); i++) {
                 sum = sum + basketList.get(i).getPrice();
             }
             return sum;
         }

    }




}
