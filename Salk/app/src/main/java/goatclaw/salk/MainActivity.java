package goatclaw.salk;

import android.content.Intent;
import android.graphics.Color;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //atributos estáticos para la configuración de cada usuario
    public static String language;
    public static int level = 0; //nivel del usuario: 0-fácil, 1-medio, 2-difícil
    public static GoogleSignInAccount account;
    private final String idToken = "165092933075-l627e9d3elufvocrrd84v559dv1ctmlr.apps.googleusercontent.com";
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(idToken)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //Cogemos la info de google
        account = getIntent().getParcelableExtra(LoginActivity.GOOGLE_ACCOUNT);

        String lang = Locale.getDefault().getLanguage();

        switch(lang){
            case "es":
                language = "spanish";
                break;
            case "en":
                language = "english";
                break;
            case "fr":
                language = "french";
                break;
            case "de":
                language = "german";
                break;
            default:
                language = "spanish";
        }

        //Mando el user a la api de , si hay algun problema hago logout
        //sendUserName(account.getGivenName().toLowerCase(), language);

        //getLevel(account.getGivenName().toLowerCase());

        //Seteo los campos de la interfaz con los datos del usuario de google
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.tvName);
        TextView email = (TextView) headerView.findViewById(R.id.tvEmail);
        CircleImageView ph = (CircleImageView) headerView.findViewById(R.id.circle_image);

        /* Solo para Enroque
        SettingsActivity.setUsername("Enroque");
        SettingsActivity.setEmail("prueba@enroque.io");
        SettingsActivity.setLanguage(language);
        SettingsActivity.setUserImage("goatclaw.salk:drawable/cabra");
        int id = getResources().getIdentifier(SettingsActivity.getUserImage(), null, null);
        ph.setImageResource(id);*/

        SettingsActivity.setUsername(account.getDisplayName());
        SettingsActivity.setEmail(account.getEmail());
        SettingsActivity.setLanguage(language);
        SettingsActivity.setUserImage(account.getPhotoUrl().toString());

        name.setText(SettingsActivity.getUsername());
        email.setText(SettingsActivity.getLanguage());

        Glide.with(this).load(SettingsActivity.getUserImage())
                .override(180,180)
                .into(ph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Mostramos el cuadrante del usuario
        PieChartView pieChartView = (PieChartView) findViewById(R.id.chart);
        List<SliceValue> pieData = new ArrayList<>();
        pieData.add(new SliceValue(40, Color.BLUE).setLabel("Facil"));
        pieData.add(new SliceValue(40, Color.GRAY).setLabel("Medio"));
        pieData.add(new SliceValue(20, Color.MAGENTA).setLabel("Dificil"));
        PieChartData pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true);
        pieChartData.setHasCenterCircle(true).setCenterText1("Resumen del usuario").setCenterText1FontSize(15)
                    .setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);


        Button btnScan = (Button) findViewById(R.id.btnContinue);
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent scanActivity = new Intent(getApplicationContext(), Camera2Activity.class);
                startActivity(scanActivity);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void sendUserName(final String username, final String language){
        RequestQueue queueDatabase = Volley.newRequestQueue(this);

        if (language != null && !language.equals("") && username != null && !username.equals("")) {
            StringRequest myReq = new StringRequest(Request.Method.POST, "http://92.176.178.247:5754/create_user", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        HashMap<String, String> respuesta = mapper.readValue(response, new TypeReference<Map<String, String>>(){});
                        if(respuesta.get("warning") == "") {
                            SettingsActivity.setLanguage(Locale.getDefault().getDisplayLanguage());
                            SettingsActivity.setUsername(account.getDisplayName());
                        }
                        else
                            errorLogin();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("PETITION_DB",  response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("PETITION_DB",  error.toString());
                    errorLogin();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user", username);
                    params.put("language", language);
                    return params;
                }
            };
            queueDatabase.add(myReq);
        }
    }

    private void getLevel(final String username){
        RequestQueue queueDatabase = Volley.newRequestQueue(this);

        if (language != null && !language.equals("") && username != null && !username.equals("")) {
            StringRequest myReq = new StringRequest(Request.Method.POST, "http://92.176.178.247:5754/get_user", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        HashMap<String, String> respuesta = mapper.readValue(response, new TypeReference<Map<String, String>>(){});
                        if(respuesta.get("warning") == "") {
                            SettingsActivity.setLanguage(respuesta.get("language"));
                            level = Integer.parseInt(respuesta.get("difficulty"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Log.i("PETITION_DB",  response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("PETITION_DB",  error.toString());
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("user", username);
                    return params;
                }
            };
            queueDatabase.add(myReq);
        }
    }

    private void errorLogin(){
        logOut();
        Toast toast1 = Toast.makeText(this, "Ha habido un problema al iniciar sesión con la aplicación", Toast.LENGTH_LONG);
        toast1.setGravity(Gravity.CENTER, 0, 0);
        toast1.show();
    }

    private void logOut(){
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //On Succesfull signout we navigate the user back to LoginActivity
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_levels) {
            Log.i("Settings", SettingsActivity.getLanguage() + " " + SettingsActivity.getLevel());

        } else if (id == R.id.nav_translate) {

            Intent transalateIntent = new Intent(getApplicationContext(), TranslateActivity.class);
            startActivity(transalateIntent);

        } else if (id == R.id.nav_sign_out) {
            logOut();

        } else if (id == R.id.nav_share){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
