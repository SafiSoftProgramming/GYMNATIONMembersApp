package safisoft.gymnationmembers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class BeforLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_befor_login);
        ImageButton btn_continue = findViewById(R.id.btn_continue);




        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BeforLoginActivity.this, safisoft.gymnationmembers.GymsChooseActivity.class);
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

        Intent intent = new Intent(BeforLoginActivity.this, AdsPostActivity.class);
        startActivity(intent);
        finish();

    }



}
