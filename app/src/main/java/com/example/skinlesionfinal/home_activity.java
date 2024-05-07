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
    TextView result2;
    int imageSize = 32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gallery = findViewById(R.id.button1forhome);

        result = findViewById(R.id.result);
        result2 = findViewById(R.id.result2);
        imageView = findViewById(R.id.imageView1);


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

            String[] classes = {"akiec", "bcc", "bkl", "df", "mel", "nv", "vasc"};
            String[] descriptions = {
                    "Akiec: Actinic keratosis is a rough, scaly patch on the skin that develops from years of exposure to the sun.",
                    "Bcc: Basal cell carcinoma is a type of skin cancer that begins in the basal cells.",
                    "Bkl: Benign keratosis-like lesions are skin growths that resemble benign keratosis, which is a non-cancerous skin condition.",
                    "Df: Dermatofibroma is a common skin growth that usually forms on the lower legs of adults.",
                    "Mel: Melanoma is a serious type of skin cancer that begins in the melanocytes.",
                    "Nv: Melanocytic nevi are moles, which are common skin growths that develop when pigment cells (melanocytes) grow in clusters.",
                    "Vasc: Vascular lesions are skin conditions that are caused by abnormal blood vessels."
            };
            String predictedClassName = classes[maxIndex];
            String predictedClassDescription = descriptions[maxIndex];

// Display the result
            result.setText("Predicted Class: " + predictedClassName);
            result2.setText("Description: " + predictedClassDescription);



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
