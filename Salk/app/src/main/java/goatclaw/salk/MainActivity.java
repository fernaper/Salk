package goatclaw.salk;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //atributos estáticos para la configuración de cada usuario
    public static String language;
    public static int level = 0; //nivel del usuario: 0-fácil, 1-medio, 2-difícil
    public static GoogleSignInAccount account;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_main);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("165092933075-l627e9d3elufvocrrd84v559dv1ctmlr.apps.googleusercontent.com")
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

        //Mando el user a la api de barral
        ConnectAPI.sendUserName(account.getGivenName().toLowerCase(), language, this);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(ConnectAPI.respuesta != null && ConnectAPI.respuesta.get("warning") == ""){
            SettingsActivity.setLanguage(Locale.getDefault().getDisplayLanguage());
            SettingsActivity.setUsername(account.getDisplayName());
        } else {
            logOut();
            Toast toast1 = Toast.makeText(this, "Ha habido un problema al iniciar sesión con la aplicación", Toast.LENGTH_LONG);
            toast1.setGravity(Gravity.CENTER, 0, 0);
            toast1.show();
        }

        View headerView = navigationView.getHeaderView(0);
        TextView name = (TextView) headerView.findViewById(R.id.tvName);
        TextView email = (TextView) headerView.findViewById(R.id.tvEmail);
        CircleImageView ph = (CircleImageView) headerView.findViewById(R.id.circle_image);

        name.setText(account.getDisplayName());
        email.setText(account.getEmail());

        Glide.with(this).load(account.getPhotoUrl().toString())
                .override(180,180)
                .into(ph);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //TODO: obtener datos de los usuarios con una llamada a la API

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
    protected void onStart() {
        super.onStart();
    }
}
