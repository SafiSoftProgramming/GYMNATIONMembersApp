package safisoft.gymnationmembers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


@RequiresApi(api = Build.VERSION_CODES.S)
public class SplashActivity extends AppCompatActivity {
    private static final int REQUEST= 112;

    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
           // Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //  android.Manifest.permission.CHANGE_WIFI_STATE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);// hide notification bar

        if (Build.VERSION.SDK_INT >= 23) {

            if (!hasPermissions(this, PERMISSIONS[0]) &&
                    !hasPermissions(this, PERMISSIONS[1]) &&
                    !hasPermissions(this, PERMISSIONS[2])) {

                final AppPermissionsDialog appPermissionsDialog = new AppPermissionsDialog(SplashActivity.this);
                appPermissionsDialog.show();
                appPermissionsDialog.setCanceledOnTouchOutside(false);
                appPermissionsDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                appPermissionsDialog.btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, REQUEST);
                        appPermissionsDialog.dismiss();
                    }
                });
            }
            else {
                callActivity();
            }

        }
        else {
            callActivity();
        }

        SetAlarmClass setAlarmClass = new SetAlarmClass();
        setAlarmClass.setalarm(getApplicationContext());

    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {


                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {

                    callActivity();
                } else {
                    Toast.makeText(this, "Pleas accept all user PERMISSIONS", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    public void callActivity() {
        new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) { }
            public void onFinish() {
                Intent intent = new Intent(SplashActivity.this, AdsPostActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();
    }











}
