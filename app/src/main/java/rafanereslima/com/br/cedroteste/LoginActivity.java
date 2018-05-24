package rafanereslima.com.br.cedroteste;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.PermissionRequest;
import android.widget.Button;
import android.widget.EditText;
import android.app.KeyguardManager;
import android.hardware.fingerprint.FingerprintManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.single.PermissionListener;

public class LoginActivity extends AppCompatActivity {

    EditText logemail;
    EditText logpassword;

    String verifymail;
    String verifypassword;

    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private TextView textFinger;

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);

        final OkHttpClient client = new OkHttpClient();

        textFinger = findViewById(R.id.textfinger);
        logemail = findViewById(R.id.logemail);
        logpassword = findViewById(R.id.logpassword);

        final Button buttonLogin = findViewById(R.id.signbtn);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                verifymail = logemail.getText().toString();
                verifypassword = logpassword.getText().toString();

                final MediaType MEDIA_TYPE = MediaType.parse("application/json");
                JSONObject postdata = new JSONObject();
                try {
                    postdata.put("email", verifymail);
                    postdata.put("password", verifypassword);
                } catch(JSONException e){
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

                final Request request = new Request.Builder()
                        .url("https://dev.people.com.ai/mobile/api/v2/login")
                        .post(body)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "")
                        .addHeader("cache-control", "no-cache")
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        String mMessage = e.getMessage().toString();
                        Log.w("failure Response", mMessage);
                        //Toast.makeText(getBaseContext(), "Falha no login", Toast.LENGTH_SHORT).show();
                        //call.cancel();
                    }

                    @Override
                    public void onResponse(Call call, Response response)
                            throws IOException {

                        String mMessage = response.body().string();
                        if (response.isSuccessful()){
                            try {
                                JSONObject json = new JSONObject(mMessage);
                                final String serverResponse = json.getString("token");
                                System.out.println("Resposta do server '"+serverResponse + "' !!!");

                                Intent intent = new Intent();
                                intent.setClass(LoginActivity.this, HomeAuth.class);

                                startActivity(intent);

                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), ("Falha no login, tente novamente."), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        final ImageButton buttonFinger = findViewById(R.id.fingerbtn);
        buttonFinger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Dexter.withActivity(LoginActivity.this).withPermission(Manifest.permission.USE_FINGERPRINT).withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setStatus("Aguardando autenticar...");
                        auth();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        setStatus("Permissão não concedida");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(com.karumi.dexter.listener.PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

            }
        });

    }

    private void auth(){
        if(fingerprintManager.isHardwareDetected()){
            if(fingerprintManager.hasEnrolledFingerprints()){
                fingerprintManager.authenticate(null, null, 0, new FingerprintManager.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        setStatus(errString.toString());
                    }

                    @Override
                    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                        super.onAuthenticationHelp(helpCode, helpString);
                        setStatus(helpString.toString());
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        setStatus("Autenticado com sucesso!");

                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, HomeAuth.class);

                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        setStatus("Não reconhecido");
                    }
                }, null);
            }
            else{
                setStatus("Impressão digital não salva");
            }
        }
        else{
            setStatus("Impressão digital não reconhecida");
        }
    }

    private void setStatus(final String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textFinger.setText(message);
            }
        });
    }
}