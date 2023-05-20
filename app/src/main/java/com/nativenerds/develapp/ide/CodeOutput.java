package com.nativenerds.develapp.ide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.amrdeveloper.codeview.CodeView;
import com.nativenerds.develapp.MainActivity;
import com.nativenerds.develapp.R;

import java.util.ArrayList;


/**
 * Screen for displaying code and it's output after being run
 */
public class CodeOutput extends AppCompatActivity {

    private ArrayList<String> codeText;
    private String codeOutput;

    private TextView outputTextView;
    private CodeView codeView;

    /**
     * Sets up the output screen, initializes fields for the activity
     *
     * @param savedInstanceState activity's previously saved state
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_output);

        codeText = (ArrayList<String>)getIntent().getSerializableExtra(getString(R.string.code_lines_tag));
        codeOutput = (String)getIntent().getExtras().getString(getString(R.string.code_output_tag));

        outputTextView = findViewById(R.id.output_text);
        codeView = findViewById(R.id.codeView1);
        String text = getIntent().getStringExtra("code");
        codeView.setText(text);
        setupOutputText();
    }

    /**
     * Overrides hardware back button to call software back button
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this , MainActivity.class);
        startActivity(intent);
    }

    /**
     * Returns to the main activity with the code as a parameter
     *
     * @param v button that was selected
     */
    public void onClickBack(View v) {
        Intent intent = new Intent(this, destress.class);
        intent.putExtra(getString(R.string.code_lines_tag), codeText);
        startActivity(intent);
    }


    private void setupOutputText() {
        outputTextView.setText(codeOutput);
    }

    /**
     * Translates the code text list to a string format
     *
     * @return string of code
     */

}
