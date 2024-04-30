package com.example.skinlesionfinal;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.skinlesionfinal.ml.Skin3LesionModel;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class home_activity extends AppCompatActivity {

    Button camera, gallery;
    ImageView imageView;
    TextView result;
    int imageSize = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        camera = findViewById(R.id.button2forhome);
        gallery = findViewById(R.id.button1forhome);

        result = findViewById(R.id.result);
        imageView = findViewById(R.id.imageView1);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 3);
                } else {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                }
            }
        });

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });
    }

    public void classifyImage(Bitmap image) {
        try {
            Skin3LesionModel model = Skin3LesionModel.newInstance(getApplicationContext());

            // Preprocess the image
            ByteBuffer byteBuffer = preprocessImage(image);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 32, 32, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.
            Skin3LesionModel.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Releases model resources if no longer used.
            model.close();

            // Find the index of the maximum value in the output vector
            float[] outputArray = outputFeature0.getFloatArray();
            int maxIndex = 0;
            float maxValue = 0;
            for (int i = 0; i < outputArray.length; i++) {
                if (outputArray[i] > maxValue) {
                    maxValue = outputArray[i];
                    maxIndex = i;
                }
            }

            // Decode the predicted class
            String[] classes = {"akiec", "bcc", "bkl", "df", "mel", "nv", "vasc"};
            String predictedClassName = classes[maxIndex];

            // Display the result
            result.setText("Predicted Class: " + predictedClassName);

        } catch (IOException e) {
            // TODO Handle the exception
            e.printStackTrace();
        }
    }

    private ByteBuffer preprocessImage(Bitmap image) {
        // Define the target image size
        int targetSize = 32;

        // Resize the image to the target size
        Bitmap resizedImage = Bitmap.createScaledBitmap(image, targetSize, targetSize, true);

        // Normalize pixel values and convert them to uint8
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(targetSize * targetSize * 3 * 4); // 4 bytes per float
        byteBuffer.order(ByteOrder.nativeOrder());
        for (int i = 0; i < targetSize; i++) {
            for (int j = 0; j < targetSize; j++) {
                int pixelValue = resizedImage.getPixel(j, i);
                byteBuffer.putFloat((float) ((pixelValue >> 16) & 0xFF) / 255.0f);  // R
                byteBuffer.putFloat((float) ((pixelValue >> 8) & 0xFF) / 255.0f);   // G
                byteBuffer.putFloat((float) (pixelValue & 0xFF) / 255.0f);          // B
            }
        }
        byteBuffer.rewind();  // Rewind the buffer to the beginning

        return byteBuffer;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            // Remove this line: image = ThumbnailUtils.extractThumbnail(image, imageSize, imageSize);
            imageView.setImageBitmap(image);
            classifyImage(image);
        }
        else {
            Uri dat = data.getData();
            Bitmap image = null;
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);
            classifyImage(image);
        }
    }
}