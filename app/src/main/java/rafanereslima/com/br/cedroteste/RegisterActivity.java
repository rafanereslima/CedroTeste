package rafanereslima.com.br.cedroteste;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {

    EditText regemail;
    EditText regname;
    EditText regpassword;
    String verifypassword;
    String verifyname;
    String verifyemail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_view);

        regname = findViewById(R.id.regname);
        regemail = findViewById(R.id.regemail);
        regpassword = findViewById(R.id.regpassword);

        final OkHttpClient client = new OkHttpClient();

        final Button button = findViewById(R.id.registersbtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                verifyname = regname.getText().toString();
                verifyemail = regemail.getText().toString();
                verifypassword = regpassword.getText().toString();

                int size=verifypassword.length();

                if(size==0) {
                    Toast.makeText(getBaseContext(), "Digite a senha!", Toast.LENGTH_SHORT).show();
                }
                Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");

                Matcher matcher = pattern.matcher(verifypassword);

                if (!matcher.matches() && size>=8) {
                    System.out.println("string '"+verifypassword + "' contains special character");
                    Toast.makeText(getBaseContext(), "Contem caracter especial", Toast.LENGTH_SHORT).show();

                    //ENVIO DO REGISTRO LOGIN

                    final MediaType MEDIA_TYPE = MediaType.parse("application/json");
                    JSONObject postdata = new JSONObject();
                    try {
                        postdata.put("email", verifyemail);
                        postdata.put("name", verifyname);
                        postdata.put("password", verifypassword);
                    } catch(JSONException e){
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    RequestBody body = RequestBody.create(MEDIA_TYPE, postdata.toString());

                    final Request request = new Request.Builder()
                            .url("https://dev.people.com.ai/mobile/api/v2/register")
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
                            //call.cancel();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            String mMessage = response.body().string();
                            if (response.isSuccessful()){
                                try {
                                    JSONObject json = new JSONObject(mMessage);
                                    final String serverResponse = json.getString("Your Index");

                                } catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        }
                    });



                    //System.out.println("Resposta do Server '"+serverResponse + "' !!!");


                    Toast.makeText(getBaseContext(), "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();

                } else {
                    System.out.println("string '"+verifypassword + "' não contem caracter especial");
                    Toast.makeText(getBaseContext(), "Digite caratecter especial e pelo menos 8 digitos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}