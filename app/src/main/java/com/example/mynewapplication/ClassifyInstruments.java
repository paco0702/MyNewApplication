package com.example.mynewapplication;

import android.content.Context;
import android.graphics.Bitmap;

import com.example.mynewapplication.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ClassifyInstruments extends Classify {
    private String[] instruments_categories = {"acordian",
            "alphorn",
            "banjo",
            "bongo drum",
            "casaba",
            "castanets",
            "clarinet",
            "flute",
            "guitar",
            "piano",
            "recorder"};

    public ClassifyInstruments(Bitmap[] img, int array_position, Context mContext) {
        super(img, array_position, mContext);
        super.predict_index_list = new ArrayList<Integer>();
        super.predict_value_forEach_index_list = new ArrayList<Float>();
    }

    // after uploaded the picture, allowed to classify
    public void doClassifyForInstrumentPicture() {
        System.out.println("total number Of picture " + this.array_position);
        int numberOfTotalPics = this.array_position + 1;
        try {
            Model model = Model.newInstance(this.mContext);

            for (int i = 0; i < numberOfTotalPics; i++) {
                System.out.println("The " + (i + 1) + " picture");
                this.img[i] = Bitmap.createScaledBitmap(img[i], 224, 224, true);

                // Creates inputs for reference.
                TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
                TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                tensorImage.load(this.img[i]);

                ByteBuffer byteBuffer = tensorImage.getBuffer();

                inputFeature0.loadBuffer(byteBuffer);

                // Runs model inference and gets result.
                com.example.mynewapplication.ml.Model.Outputs outputs = model.process(inputFeature0);
                TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                /*
                System.out.println("around number" + i + " " + outputFeature0.getFloatArray()[0]
                        + "\n" + outputFeature0.getFloatArray()[1]
                        + "\n" + outputFeature0.getFloatArray()[2]
                        + "\n" + outputFeature0.getFloatArray()[3]
                        + "\n" + outputFeature0.getFloatArray()[4]
                        + "\n" + outputFeature0.getFloatArray()[5]
                        + "\n" + outputFeature0.getFloatArray()[6]
                        + "\n" + outputFeature0.getFloatArray()[7]
                        + "\n" + outputFeature0.getFloatArray()[8]
                        + "\n" + outputFeature0.getFloatArray()[9]
                        + "\n" + outputFeature0.getFloatArray()[10]

                );

                System.out.println("getFloatArray flat size " + outputFeagetMaxInstrumentFloatArrayture0.getFlatSize());
*/
                int largest_output_index = getMaxInstrumentFloatArray(outputFeature0.getFloatArray());

                //String output_result = instruments_categories[largest_output_index];
                this.predict_index_list.add(largest_output_index);
                this.predict_value_forEach_index_list.add(outputFeature0.getFloatArray()[largest_output_index]);
/*
                System.out.println("The " + (i + 1) + " picture predicted index is "+ largest_output_index);

*/
                // Releases model resources if no longer used.
            }
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }

    public String getCategoriesText(int index){
        return instruments_categories[index];
    }
}
