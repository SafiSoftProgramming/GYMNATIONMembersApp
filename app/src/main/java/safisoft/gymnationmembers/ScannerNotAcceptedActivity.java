package safisoft.gymnationmembers;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScannerNotAcceptedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_not_accepted);

        ImageButton btn_try_agin_login = findViewById(R.id.btn_try_agin_login);

        btn_try_agin_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(ScannerNotAcceptedActivity.this,GymsChooseActivity.class);
                startActivity(intent);
                finish();

            }
        });


    }




    /**
     * Back button listener.
     * Will close the application if the back button pressed twice.
     */
    @Override
    public void onBackPressed()
    {

        Intent intent = new Intent(ScannerNotAcceptedActivity.this, AdsPostActivity.class);
        startActivity(intent);
        finish();

    }


}
