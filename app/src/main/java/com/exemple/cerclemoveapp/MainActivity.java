package com.exemple.cerclemoveapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;




public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button B_Demarrer = (Button) findViewById(R.id.btn_demarrer);
        B_Demarrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNiveau();
            }
        });

        Button B_Regles = (Button)findViewById(R.id.btn_regles);
        B_Regles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegles();
            }
        });

        Button B_Quitter = (Button)findViewById(R.id.btn_quitter);
        B_Quitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExitPopUp();
            }
        });

    }

    //Aller a l'activité des niveaux
    public void goToNiveau(){
        Intent intent = new Intent(this,Niveaux.class);
        startActivity(intent);
    }

    //Aller a l'activité des regles
    public  void goToRegles(){
        Intent intent = new Intent(this,Regles.class);
        startActivity(intent);
    }

    //menu popup pour quitter ou non le jeu
    public  void ExitPopUp(){
        AlertDialog.Builder alertpopup = new AlertDialog.Builder(this);
        alertpopup.setMessage("Êtes vous sûr de vouloir quitter ?");
        alertpopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                QuitterJeu();
            }
        });
        alertpopup.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertpopup.create();
        alertpopup.show();


    }

    //Quitter le jeu et fermer tout les activités
    public void QuitterJeu(){
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    // Quitter le jeu quand on presse le bouton Backpressed
    @Override
    public void onBackPressed(){

        AlertDialog.Builder alertpopup = new AlertDialog.Builder(this);
        alertpopup.setMessage("Êtes vous sûr de vouloir quitter ?");
        alertpopup.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.super.finish();
            }
        });
        alertpopup.setNegativeButton("Non", null);
        alertpopup.create();
        alertpopup.show();

    }
}












