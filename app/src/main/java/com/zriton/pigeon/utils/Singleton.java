package com.zriton.pigeon.utils;

import com.zriton.pigeon.data.model.ModelMessage;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Infinia on 10-03-2016.
 */
public class Singleton {

    public HashMap<String, ArrayList<ModelMessage>> getMessageHashMap() {
        return mMessageHashMap;
    }

    public void setMessageHashMap(HashMap<String, ArrayList<ModelMessage>> pMessageHashMap) {
        mMessageHashMap = pMessageHashMap;
    }

    private HashMap<String,ArrayList<ModelMessage>> mMessageHashMap = new HashMap<>();

    private static Singleton sSingleton;
    private Singleton()
    {

    }

    public static synchronized Singleton getInstance() {
        if (sSingleton == null) //if none created
            sSingleton = new Singleton(); //create one
        return sSingleton; //return it
    }
    public void print(String s) {
        System.out.println(s);
    }

}
