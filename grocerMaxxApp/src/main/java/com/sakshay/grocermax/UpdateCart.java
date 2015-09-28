package com.sakshay.grocermax;

import android.content.Context;
import android.os.AsyncTask;

import com.sakshay.grocermax.api.MyApi;
import com.sakshay.grocermax.bean.CartDetail;
import com.sakshay.grocermax.exception.GrocermaxBaseException;
import com.sakshay.grocermax.preference.MySharedPrefs;
import com.sakshay.grocermax.utils.Constants;
import com.sakshay.grocermax.utils.UrlsConstants;
import com.sakshay.grocermax.utils.UtilityMethods;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by manish.verma on 22-09-2015.
 */
public class UpdateCart extends AsyncTask<String, Void, Void> {
    MyApi myApi ;
    JSONArray products;
    String url;
    public  UpdateCart(MyApi myApi, JSONArray products)
    {
        this.myApi = myApi;
        this.products = products;
    }

    public UpdateCart(MyApi myApi, String url)
    {
        this.myApi = myApi;
        this.url = url;
    }

    protected Void doInBackground(String... x) {
        if(url!=null && !url.equalsIgnoreCase(""))
        {
            myApi.reqViewCart(url);
        }else {
            goToCart();

        }return null;
    }


    public void goToCart() {
        try {

            if (MySharedPrefs.INSTANCE.getUserId() == null || MySharedPrefs.INSTANCE.getUserId().equals("")) {              //user not login
                    try {
                        String url;
                        if (MySharedPrefs.INSTANCE.getQuoteId() == null || MySharedPrefs.INSTANCE.getQuoteId().equals(""))     //user not login and going to view cart(in this case quote id will generate)
                        {
                            System.out.println("without quote json=" + products.toString());
                            url = UrlsConstants.ADD_TO_CART_GUEST_URL + "products="
                                    + URLEncoder.encode(products.toString(), "UTF-8");
                            myApi.reqBackGroundAddToCartGuest(url);
                        } else                                                                                 //user not login and going to view cart for 2nd or more than 2nd time
                        {         //using there again guest URL even we already have quote id as it is generated before to deleted all items in cart.
                            System.out.println("without quote json=" + products.toString());

                            url = UrlsConstants.ADD_TO_CART_GUEST_URL
                                    + MySharedPrefs.INSTANCE.getUserId() + "&quote_id=" + MySharedPrefs.INSTANCE.getQuoteId() + "&products="
                                    + URLEncoder.encode(products.toString(), "UTF-8");
                            myApi.reqBackGroundAddToCartGuest(url);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else {                                                        //user login
                if (MySharedPrefs.INSTANCE.getQuoteId() != null && !(MySharedPrefs.INSTANCE.getQuoteId().equals("")))       //user not login and added products and has viewed cart (so that quite id generated by guest URL)
                {
                    String url;
                    if (MySharedPrefs.INSTANCE.getQuoteId() == null || MySharedPrefs.INSTANCE.getQuoteId().equals("")) {
                        System.out.println("without quote json=" + products.toString());
                        url = UrlsConstants.ADD_TO_CART_URL
                                + MySharedPrefs.INSTANCE.getUserId() + "&products="
                                + URLEncoder.encode(products.toString(), "UTF-8");
                    } else {
                        System.out.println("with quote json=" + products.toString());
                        url = UrlsConstants.ADD_TO_CART_URL
                                + MySharedPrefs.INSTANCE.getUserId() + "&quote_id=" + MySharedPrefs.INSTANCE.getQuoteId() + "&products="
                                + URLEncoder.encode(products.toString(), "UTF-8");
                    }
                              //user already login OR going to login.
                    myApi.reqBackgroundAddToCartNewProduct(url);
                }
            }
        } catch (NullPointerException e) {
            new GrocermaxBaseException("BaseActivity", "goToCart", e.getMessage(), GrocermaxBaseException.NULL_POINTER, "nodetail");
        } catch (Exception e) {
            new GrocermaxBaseException("BaseActivity", "goToCart", e.getMessage(), GrocermaxBaseException.EXCEPTION, "nodetail");
        }

    }
};