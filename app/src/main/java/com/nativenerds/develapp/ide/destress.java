package com.nativenerds.develapp.ide;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.amrdeveloper.codeview.CodeView;
import com.nativenerds.develapp.MainActivity;
import com.nativenerds.develapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Screen to edit/run code, launch camera, and select language
 */
public class destress extends AppCompatActivity implements HttpRequestClient.HttpResponseListener {

    private static final String IMAGE_URI_BASE = "https://akashmeruva12.cognitiveservices.azure.com/";
    private static final String CODE_EXECUTE_URI_BASE = "https://api.jdoodle.com/v1/execute";
    private static final int CODE_SUCCESS_STATUS = 200;

    private HttpRequestClient mHttpRequestClient;
    private ArrayList<String> codeText;

    private CodeView codeView;
    private ProgressBar loadingSpinner;
    private TextView progressText;
    private Spinner languageSpinner;

    /**
     * Sets up the main screen, based on the Android lifecycle.  Initializes all fields for the class.
     *
     * @param savedInstanceState activity's previously saved state
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destress);
        Bundle extras = getIntent().getExtras();

        mHttpRequestClient = new HttpRequestClient(getString(R.string.image_processing_api_key), getString(R.string.jdoodle_client_id), getString(R.string.jdoodle_client_secret), this);
        codeText = new ArrayList<String>();

        codeView = findViewById(R.id.codeView);
        codeView.setEnableLineNumber(true);
        codeView.setEnableAutoIndentation(true);
        codeView.setLineNumberTextColor(Color.WHITE);
        codeView.setLineNumberTextSize(50f);

        String code = getIntent().getStringExtra("code");
        codeView.setText(code);

        loadingSpinner = findViewById(R.id.loading_progress_spinner);
        progressText = findViewById(R.id.progress_spinner_text);
        languageSpinner = findViewById(R.id.language_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.language_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);

        if (extras != null && extras.getString(getString(R.string.saved_image_path)) != null) {
            loadingSpinner.setVisibility(VISIBLE);
            progressText.setVisibility(VISIBLE);
            codeView.setVisibility(GONE);
            sendImage(extras.getString(getString(R.string.saved_image_path)));
        } else if (extras != null && getIntent().getSerializableExtra(getString(R.string.code_lines_tag)) != null) {
            loadingSpinner.setVisibility(GONE);
            progressText.setVisibility(GONE);
            codeView.setVisibility(VISIBLE);
            codeText = (ArrayList<String>) getIntent().getSerializableExtra(getString(R.string.code_lines_tag));
            newCodeReceived();
        } else {
            loadingSpinner.setVisibility(GONE);
            progressText.setVisibility(GONE);
            String[] defaultCode = {"public class MyClass {",
                    "",
                    "    public static void main(String args[]) {",
                    "        int x = 40;",
                    "        int y = 35;",
                    "        int z = 3;",
                    "",
                    "        System.out.println(\"Sum of (x+y)/z = \" + (x + y)/z);",
                    "    }",
                    "}"};
            codeText = new ArrayList(Arrays.asList(defaultCode));
            newCodeReceived();
        }


    }

    /**
     * Overrides back button functionality such that user can't accidentally break navigation
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
        this.finish();
    }


    // HttpRequestClient callbacks //

    /**
     * Callback from HttpRequestClient, with the response from processing the image of code
     *
     * @param response json object response from Azure's Computer Vision API
     */
    public void onImageProcessingResponse(JSONObject response) {


        try {
            if (!response.getString("status").equals("Succeeded")) {
                fireNoResultToast();
                return;
            }

            JSONArray results = response.getJSONObject("recognitionResult").getJSONArray("lines");
            codeText.clear();

            if (results == null) {
                fireNoResultToast();
                return;
            }

            for (int i = 0; i < results.length(); i++) {
                JSONObject tempResult = results.getJSONObject(i);
                codeText.add(tempResult.getString("text"));
            }

            newCodeReceived();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback from HttpRequestClient, with response from running the code
     *
     * @param response string of response from Jdoodle's API
     * @param statusCode response code that Jdoodle sent, 200 == success
     */
    public void onCodeRunResponse(String response, int statusCode) {
        if (statusCode == CODE_SUCCESS_STATUS) {
            Intent intent = new Intent(this, CodeOutput.class);
            intent.putExtra("code", codeView.getText().toString());
            intent.putExtra(getString(R.string.code_lines_tag), codeText);
            intent.putExtra(getString(R.string.code_output_tag), response);
            startActivity(intent);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingSpinner.setVisibility(GONE);
                    progressText.setVisibility(GONE);
                    languageSpinner.setClickable(true);
                    Toast.makeText(destress.this, "Failed to run code! Check language setting.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void onClickCamera(View v) {
        Intent intent = new Intent(this, Pictureactivity.class);
        startActivity(intent);
    }

    /**
     * Updates the code view.  To be called whenever new lines of code are received or modified
     */
    private void newCodeReceived() {
        final String lines = codeView.getText().toString();
        Log.e("error", lines);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingSpinner.setVisibility(GONE);
                progressText.setVisibility(GONE);
                codeView.setVisibility(VISIBLE);
                codeView.setText(lines);
            }
        });
    }

    /**
     * Sends the image at the provided path to the Azure Computer Vision API to be processed for text/code
     *
     * @param imageFilePath path to image
     */
    private void sendImage(String imageFilePath) {
        Map<String, Object> params = new HashMap<>();
        File imgFile = new File(imageFilePath);

        params.put("mode", "Handwritten");
        String url = HttpRequestClient.getUrl(IMAGE_URI_BASE, params);

        try {
            InputStream imgStream = new FileInputStream(imgFile);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int bytesRead;
            byte[] bytes = new byte[1024];
            while ((bytesRead = imgStream.read(bytes)) > 0) {
                byteArrayOutputStream.write(bytes, 0, bytesRead);
            }

            byte[] data = byteArrayOutputStream.toByteArray();
            params.put("data", data);

            mHttpRequestClient.postImage(url, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the code in main activity with Jdoodle's API.
     *
     * @param v view of button selected
     */
    public void onClickPlayCode(View v) {
        loadingSpinner.setVisibility(VISIBLE);
        progressText.setVisibility(VISIBLE);
        languageSpinner.setClickable(false);
        Log.e("error", codeView.getText().toString());
        mHttpRequestClient.postCode(CODE_EXECUTE_URI_BASE, codeView.getText().toString(), languageSpinner.getSelectedItem().toString());
    }


    private void fireNoResultToast() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingSpinner.setVisibility(GONE);
                progressText.setVisibility(GONE);
                Toast.makeText(destress.this, "Failed to get text from image!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
