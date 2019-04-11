/*package goatclaw.salk;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TranslateActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
    }
}*/


package goatclaw.salk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class TranslateActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_translate);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx =  getApplicationContext();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        final EditText etInput = (EditText) findViewById(R.id.etTranslate);
        Button btnOK = (Button) findViewById(R.id.btnOK);

        final TableLayout tlTable = (TableLayout) findViewById(R.id.tableLayout);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String palabra = etInput.getText().toString().split(" ")[0];
                palabra += " ";
                Log.i("HEY", palabra);
                for (int i = 0; i < palabra.length(); i++){
                    TableRow tr = new TableRow(ctx);
                    tr.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.FILL_PARENT));

                    for (int j = 0; j <  palabra.length(); j++) {

                        Log.i("HEY", palabra.substring(0, 1));
                        ImageView view = new ImageView(ctx);
                        int id = getResources().getIdentifier("goatclaw.salk:drawable/" + palabra.substring(0, 1), null, null);
                        view.setImageResource(id);
                        palabra = palabra.substring(1);
                        tr.addView(view);
                    }
                    tlTable.addView(tr);

                }
                /*
                for (int i = 0; i < 5; i++) {

                    TableRow tr = new TableRow(ctx);
                    tr.setLayoutParams(new TableRow.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));

                    for (int j = 0; j < 5; j++) {

                        ImageView view = new ImageView(ctx);
                        int id = getResources().getIdentifier("goatclaw.salk:drawable/" + "a", null, null);
                        view.setImageResource(id);
                        tr.addView(view);
                    }
                }*/
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

            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);


        } else if (id == R.id.nav_levels) {

        } else if (id == R.id.nav_translate) {


        } else if (id == R.id.nav_share){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

