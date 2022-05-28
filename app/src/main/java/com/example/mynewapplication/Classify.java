package com.example.mynewapplication;

import android.graphics.Bitmap;

import com.example.mynewapplication.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Classify {

    protected ArrayList<Integer> predict_index_list ;
    protected ArrayList<Float> predict_value_forEach_index_list;
    Bitmap[]img;
    int array_position;
    android.content.Context mContext;

    public Classify(Bitmap[]img, int array_position, android.content.Context mContext){
        this.img = img;
        this.array_position = array_position;
        this.mContext = mContext;
        this.predict_index_list = new ArrayList<Integer>();
        this.predict_value_forEach_index_list = new ArrayList<Float>();
    }


    // they are actually the same
    protected int getMaxInstrumentFloatArray(float[] instrumentFloatArray) {
        float max = 0;
        int index = 0;
        for (int i = 0; i < instrumentFloatArray.length; i++) {
            if (instrumentFloatArray[i] > max) {
                max = instrumentFloatArray[i];
                System.out.println("index is " + i + " max is " + max);
                index = i;
            }
        }
        System.out.println("final index is " + index);
        return index;
    }

    public ArrayList<Integer> getPredict_index_list() {
        return predict_index_list;
    }

    public void setPredict_index_list(ArrayList<Integer> predict_index_list) {
        this.predict_index_list = predict_index_list;
    }

    public ArrayList<Float> getPredict_value_forEach_index_list() {
        return predict_value_forEach_index_list;
    }

    public void setPredict_value_forEach_index_list(ArrayList<Float> predict_value_forEach_index_list) {
        this.predict_value_forEach_index_list = predict_value_forEach_index_list;
    }
}
