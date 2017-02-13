package com.exemple.cerclemoveapp;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Handler;
import android.widget.TextView;

import java.util.Vector;
import java.util.Random;
import static java.lang.Math.floor;



public class MainActivity extends AppCompatActivity {

    Cercle contina[][];             //conteneur des cercles de la grille
    Cercle cercledown;              //cercle la ou on presse down
    Cercle cercleswiped;            //cercle swiped avec cercledown
    Cercle voisins[];               //4 cercles voisins du cercledown
    Vector<Cercle> TroisMV;         //Coordonnées des 3 cercles qui match verticalement
    Vector<Cercle> QuatreMV;        //Coordonnées des 4 cercles qui match verticalement
    Vector<Cercle> CinqMV;          //Coordonnées des 5 cercles qui match verticalement
    Vector<Cercle> TroisMH;         //Coordonnées des 3 cercles qui match horizontalement
    Vector<Cercle> CinqMH;          //Coordonnées des 5 cercles qui match horizontalement
    Vector<Cercle> QuatreMH;        //Coordonnées des 4 cercles qui match horizontalement

    Canvas canvas;
    Bitmap tempBitmap;
    Bitmap bmp;
    boolean swiped;                 //si la couleur echangée ou non
    boolean match;                  //si il ya un match horizontal ou vertical
    int score = 0;                  //score accumulé
    int coups = 6;                  //coups restants
    int couleurs[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, 0xFF8B00FF, 0xFFFF7F00};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        contina = new Cercle[5][8];
        Point point;

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 5; j++) {
                point = new Point(i, j);
                if ((j == 0 && (i == 0 || i == 3 || i == 5)) || (j == 4 && (i == 0 || i == 2)))                                 //Yellow
                    contina[j][i] = new Cercle(point, Color.YELLOW);
                if ((j == 0 && (i == 1 || i == 2 || i == 4)) || (j == 1 && i == 0) || (j == 2 && i == 5) || (j == 3 && (i == 5 || i == 6)) || (j == 4 && i == 6))     //Green
                    contina[j][i] = new Cercle(point, Color.GREEN);
                if ((j == 0 && (i == 6 || i == 7)) || (j == 1 && i == 1) || (j == 3 && (i == 0)) || (j == 4 && i == 7))                           //Purple
                    contina[j][i] = new Cercle(point, 0xFF8B00FF);
                if ((j == 1 && (i == 2 || i == 6)) || (j == 2 && (i == 2 || i == 6)) || (j == 3 && (i == 1 || i == 4 || i == 7)) || (j == 4 && i == 3))            //Orange
                    contina[j][i] = new Cercle(point, 0xFFFF7F00);
                if ((j == 1 && (i == 4 || i == 5 || i == 7)) || (j == 2 && (i == 0 || i == 4)) || (j == 3 && i == 3) || (j == 4 && i == 4))                    //Blue
                    contina[j][i] = new Cercle(point, Color.BLUE);
                if ((j == 1 && i == 3) || (j == 2 && (i == 1 || i == 3 || i == 7)) || (j == 4 && (i == 1 || i == 5)) || j == 3 && i == 2)                                 //Red
                    contina[j][i] = new Cercle(point, Color.RED);

            }


        DessinerJeu();
        ImageView monimage = (ImageView) findViewById(R.id.imageView);
        monimage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                GererTouch(event);
                return true;
            }
        });


    }


    /**********
     * Gestion des touches d'ecrans
     *********/
    public void GererTouch(MotionEvent event) {

        int action = event.getActionMasked();
        int largcarre = findViewById(R.id.imageView).getWidth() / 8;
        int idX = (int) floor(event.getX(0) / largcarre);
        int idY = (int) floor(event.getY(0) / largcarre);


        switch (action) {

            case MotionEvent.ACTION_DOWN:
                GererDown(idX, idY);
                break;

            case MotionEvent.ACTION_UP:
                if (swiped) {
                    coups -= 1;
                    Log.d("Swiped:      Yes", "    coups restants: " + String.valueOf(coups));
                      if (VerifierMatchHorizontal() || VerifierMatchVertical()){
                        Log.d("Match :    ", "YES");
                        DessinerJeu();
                        ScanNewMatch();

                    } else {
                        Log.d("Match :      ", "NO");
                        AnnulerSwipe();
                    }
                 if (coups==0)
                     Message();

                } else Log.d("Swiped:      ", "NO");
                break;

            case MotionEvent.ACTION_MOVE:
                swiped = GererMove(idX, idY);
                break;
            default:
        }
    }


    public void Message(){

        AlertDialog.Builder dAlert = new AlertDialog.Builder(this);
        dAlert.setTitle("Match-3");
        dAlert.setCancelable(true);
        dAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //dismiss the dialog
                    }
                });
        dAlert.setMessage("Vous avez perdu!");
        dAlert.create().show();

    }

    /***********
     * Dessiner les cercles
     ***********/
    public void DessinerJeu() {
        Log.d("DessinerJeu", "      Oui");
        bmp = BitmapFactory.decodeResource(this.getResources(), R.drawable.grid58);
        tempBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.RGB_565);

        canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(bmp, 0, 0, null);

        float RayonCercle = tempBitmap.getHeight() / 10 - 10;   //Rayon d'un cercle
        float lgcarre = (tempBitmap.getWidth() + 7) / 8;        //+7 et +4 c pour ajuster la largeur et la longeur d'un carré
        float longcarre = (tempBitmap.getHeight() + 4) / 5;

        Paint monpaint = new Paint();

        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 5; j++) {
                monpaint.setColor(contina[j][i].getCouleur());
                canvas.drawCircle(contina[j][i].getCentre().x * lgcarre + lgcarre / 2, contina[j][i].getCentre().y * longcarre + longcarre / 2, RayonCercle, monpaint);
            }

        final ImageView monimage = (ImageView) findViewById(R.id.imageView);
        monimage.setImageBitmap(tempBitmap);

        TextView scoreaffiche = (TextView) findViewById(R.id.score);
        scoreaffiche.setText(String.valueOf(score));

        TextView coupsaffiche = (TextView) findViewById(R.id.nbcoups);
        coupsaffiche.setText(String.valueOf(coups));

        swiped = false;
        match = false;
    }

    /**********
     * Verification des couleurs dans la console Log
     *********/
    public String WhatColor(int c) {
        switch (c) {
            case 16776961:
                return "Blu";
            case 33024:
                return "Orange";
            case 7667457:
                return "Violet";
            case 16711936:
                return "Green";
            case 256:
                return "Yellow";
            case 65536:
                return "Red";
        }
        return "Ne sais pas";
    }



/*            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DessinerJeu();
                }
            }, 1000);  */


    /***********
     * Gerer l'evenment Down quand on pese avec le doigt ou le curseur sur un cercle
     *********/
    public void GererDown(int x, int y) {

        voisins = new Cercle[4];
        cercledown = new Cercle(new Point(x, y), contina[y][x].getCouleur());

        //recuperer les voisins qui sont a l'interieur de la grille seulement
        if (y != 0)
            voisins[0] = new Cercle(contina[y - 1][x].getCentre(), contina[y - 1][x].getCouleur());
        else voisins[0] = null;

        if (y != 4)
            voisins[1] = new Cercle(contina[y + 1][x].getCentre(), contina[y + 1][x].getCouleur());
        else voisins[1] = null;

        if (x != 0)
            voisins[2] = new Cercle(contina[y][x - 1].getCentre(), contina[y][x - 1].getCouleur());
        else voisins[2] = null;

        if (x != 7)
            voisins[3] = new Cercle(contina[y][x + 1].getCentre(), contina[y][x + 1].getCouleur());
        else voisins[3] = null;
    }


    /*******
     * Gerer le deplacement du doigt ou le curseur
     *******/
    public boolean GererMove(int x, int y) {


        for (int i = 0; i < 4; i++) {
            if (voisins[i] != null && voisins[i].getCentre().equals(x, y)) {                                        //si on est rendu sur un voisin non null

                cercleswiped = new Cercle(new Point(x, y), voisins[i].getCouleur());                                 //on recupere le cercle to swipe
                contina[y][x].setCouleur(cercledown.getCouleur());                                                  //on change la couleur du voisin dans le contina
                contina[cercledown.getCentre().y][cercledown.getCentre().x].setCouleur(cercleswiped.getCouleur());    //on change le couleur du cercledown
                return true;
            }
        }
        return false;
    }


    /**********
     * Verifier et remplacer tous les match horizontal
     *********/
    public boolean VerifierMatchHorizontal(){

        if (CinqMatchH() || QuatreMatchH() || TroisMatchH()){
            RemplacerMatchHorizontal();
            return true;
        }
        return false;
    }

    /**********
     * Verifier et remplacer tous les match Vertical
     *********/
    public boolean VerifierMatchVertical(){

        if (CinqMatchVertical() || QuatreMatchVertical() || TroisMatchVertical()){
            RemplacerMatchVertical();
            return true;
        }
        return false;
    }

    /**********
     * Verifier s'il ya CINQ match horizontal
     *********/
    public boolean CinqMatchH(){

        CinqMH = new Vector<Cercle>();

        for (int j=0;j<5;j++) {
            for (int i = 2; i < 6; i++) {
                if (contina[j][i].getCouleur()==contina[j][i-1].getCouleur() && contina[j][i].getCouleur()==contina[j][i+1].getCouleur()
                        && contina[j][i].getCouleur()==contina[j][i-2].getCouleur() && contina[j][i].getCouleur()==contina[j][i+2].getCouleur()){

                    for (int k=2;k>-3;k--)                                                              //On recupere les 5 cercles qui match
                        CinqMH.add(contina[j][i-k]);
                    Log.d("Match-----", "Horizontal  Cinq");
                    return true;
                }

            }
        }
        CinqMH.clear();
        return false;
    }

    /**********
     * Verifier s'il ya QUATRE match horizontal
     *********/
    public boolean QuatreMatchH(){

        QuatreMH = new Vector<Cercle>();

        for (int j=0;j<5;j++) {
            for (int i = 1; i < 6; i++) {
                if (contina[j][i].getCouleur()==contina[j][i-1].getCouleur() && contina[j][i].getCouleur()==contina[j][i+1].getCouleur()
                        && contina[j][i].getCouleur()==contina[j][i+2].getCouleur()){

                    for (int k=1;k>-3;k--)                                                              //On recupere les 4 cercles qui match
                        QuatreMH.add(contina[j][i-k]);
                    Log.d("Match-----", "Horizontal  Quatre");
                    return true;
                }

            }
        }
        QuatreMH.clear();
        return false;
    }

    /**********
     * Verifier s'il ya TROIS match horizontal
     *********/
    public boolean TroisMatchH() {

        TroisMH = new Vector<Cercle>();

        for (int j = 0; j < 5; j++)
            for (int i = 1; i < 7; i++)
                if (contina[j][i].getCouleur() == contina[j][i - 1].getCouleur() && contina[j][i].getCouleur() == contina[j][i + 1].getCouleur()) {
                    TroisMH.add(contina[j][i - 1]);
                    TroisMH.add(contina[j][i]);
                    TroisMH.add(contina[j][i + 1]);
                    Log.d("Match-----", "Horizontal");
                    return true;
                }
        TroisMH.clear();
        return false;
    }
    

    /********
     * Verifier s'il ya CINQ match vertical
     ********/
    public boolean CinqMatchVertical() {

        CinqMV = new Vector<Cercle>();
        int j=2;

        for (int i = 0; i < 8; i++)
                if (contina[j][i].getCouleur() == contina[j - 1][i].getCouleur() && contina[j][i].getCouleur() == contina[j -2][i].getCouleur()
                        &&  contina[j][i].getCouleur()==contina[j+1][i].getCouleur() &&  contina[j][i].getCouleur()==contina[j+2][i].getCouleur()) {
                    Log.d("Match-----", "Cinq Vertical");
                    for (int k=2;k>-3;k--)
                        CinqMV.add(contina[j+k][i]);
                    return true;
                }
        //a faire le cas de match horisontal et vertical en meme temps
        CinqMV.clear();
        return false;

    }

    /********
     * Verifier s'il ya QUATRE match vertical
     ********/
    public boolean QuatreMatchVertical() {

        QuatreMV = new Vector<Cercle>();

        for (int i = 0; i < 8; i++)
            for (int j = 1; j < 3; j++)
                if (contina[j][i].getCouleur() == contina[j - 1][i].getCouleur() && contina[j][i].getCouleur() == contina[j + 1][i].getCouleur()
                        &&  contina[j][i].getCouleur()==contina[j+2][i].getCouleur()) {
                    Log.d("Match-----", "Quatre Vertical");
                    for (int k=2;k>-2;k--)
                        QuatreMV.add(contina[j+k][i]);
                    return true;
                }
        //a faire le cas de match horisontal et vertical en meme temps
        QuatreMV.clear();
        return false;

    }

    /********
     * Verifier s'il ya un TROIS match vertical
     ********/
    public boolean TroisMatchVertical() {

        TroisMV = new Vector<Cercle>();

        for (int i = 0; i < 8; i++)
            for (int j = 1; j < 4; j++)
                if (contina[j][i].getCouleur() == contina[j - 1][i].getCouleur() && contina[j][i].getCouleur() == contina[j + 1][i].getCouleur()) {
                    Log.d("Match-----", "Trois Vertical");
                    TroisMV.add(contina[j + 1][i]);
                    TroisMV.add(contina[j][i]);
                    TroisMV.add(contina[j - 1][i]);
                    return true;
                }
        //a faire le cas de match horisontal et vertical en meme temps
        TroisMV.clear();
        return false;

    }


    /*************
     * Remplacer les 5 cercles qui "match"
     ***********/
    public void RemplacerMatchHorizontal() {

        int Xm;
        int Ym;
        Vector<Cercle> TempMatching = null;

            if (!CinqMH.isEmpty())
                TempMatching = CinqMH;
            if (!QuatreMH.isEmpty())
                TempMatching = QuatreMH;
            if (!TroisMH.isEmpty())
                TempMatching = TroisMH;

                for (Cercle c : TempMatching) {
                    Xm = c.getCentre().x;
                    Ym = c.getCentre().y;
                    while ((Ym - 1) >= 0) {
                        contina[Ym][Xm].setCouleur(contina[Ym - 1][Xm].getCouleur());
                        Ym--;
                    }
                    if (Ym == 0) {
                        contina[Ym][Xm].setCouleur(couleurs[new Random().nextInt(couleurs.length)]);            //Couleur aleatoire
                    }

                    Log.d("TroisMV Cinq  ", WhatColor(-1 * c.getCouleur()) + "X  " + c.getCentre().x + "   Y  " + c.getCentre().y);
                }
                if (CinqMH.size() ==5)
                score += 300;
                if (QuatreMH.size() ==4)
                    score += 200;
                if (TroisMH.size() ==3)
                    score += 100;
                Log.d("SCore  Horizontal", String.valueOf(score));


        if (CinqMH != null)
            CinqMH.clear();
        if (QuatreMH != null)
            QuatreMH.clear();
        if (TroisMH != null)
            TroisMH.clear();       
    }

    public void RemplacerMatchVertical(){

        int Xm;
        int Ym;

    if (CinqMV!=null)
        if (!CinqMV.isEmpty()) {
            for (Cercle c: CinqMV){
                Xm = c.getCentre().x;
                Ym = c.getCentre().y;
                contina[Ym][Xm].setCouleur(couleurs[new Random().nextInt(couleurs.length)]);            //Couleur aleatoire
                Log.d("Cinq Match  ", WhatColor(-1 * c.getCouleur()) + "X  " + c.getCentre().x + "   Y  " + c.getCentre().y);
            }
            score += 300;
        }

    if (QuatreMV!=null)
        if (!QuatreMV.isEmpty()) {
            for (Cercle c: QuatreMV) {
                Xm = c.getCentre().x;
                Ym = c.getCentre().y;
                if ((Ym-4)>=0)
                    contina[Ym][Xm].setCouleur(contina[Ym - 4][Xm].getCouleur());
                else
                    contina[Ym][Xm].setCouleur(couleurs[new Random().nextInt(couleurs.length)]);
                Log.d("Quatre Match  ", WhatColor(-1 * c.getCouleur()) + "X  " + c.getCentre().x + "   Y  " + c.getCentre().y);
            }
            score += 200;
        }

    if (TroisMV!=null)
        if (!TroisMV.isEmpty()){
            for (Cercle c : TroisMV) {
                Xm = c.getCentre().x;
                Ym = c.getCentre().y;
                if ((Ym-3)>=0)
                    contina[Ym][Xm].setCouleur(contina[Ym - 3][Xm].getCouleur());
                else
                    contina[Ym][Xm].setCouleur(couleurs[new Random().nextInt(couleurs.length)]);

                Log.d("Trois Match  ", WhatColor(-1 * c.getCouleur()) + "X  " + c.getCentre().x + "   Y  " + c.getCentre().y);
            }
            score += 100;
        }

        if (CinqMV != null)
            CinqMV.clear();
        if (QuatreMV != null)
            QuatreMV.clear();
        if (TroisMV != null)
            TroisMV.clear();


    }

    /*************
     * Remplacer les 3 cercles qui "match"
     ***********/
    public void RemplacerMatchTrois() {

        int Xm;
        int Ym;

        if (TroisMH != null) {
            if (!TroisMH.isEmpty()) {
//                Log.d("Remplacer Horizontal", "  TroisMH Not Empty");
                for (Cercle c : TroisMH) {
                    Xm = c.getCentre().x;
                    Ym = c.getCentre().y;
                    while ((Ym - 1) >= 0) {
                        contina[Ym][Xm].setCouleur(contina[Ym - 1][Xm].getCouleur());
                        Ym--;
                    }
                    if (Ym == 0) {
                        contina[Ym][Xm].setCouleur(couleurs[new Random().nextInt(couleurs.length)]);            //Couleur aleatoire
                    }

                    Log.d("TroisMV Cercle  ", WhatColor(-1 * c.getCouleur()) + "X  " + c.getCentre().x + "   Y  " + c.getCentre().y);
                }
                score += 100;
                Log.d("SCore  Horizontal ", String.valueOf(score));
            }
        }

        if (TroisMV != null) {

            if (!TroisMV.isEmpty()) {
//                Log.d("Remplacer Vertical", "  TroisMV Not Empty");
                for (Cercle cercle : TroisMV) {
                    Xm = cercle.getCentre().x;
                    Ym = cercle.getCentre().y;
                    while (Ym - 3 >= 0) {
                        contina[Ym][Xm].setCouleur(contina[Ym - 3][Xm].getCouleur());
                        Ym--;

                    }
                    if (Ym >= 0 && Ym <= 2) {
                        contina[Ym][Xm].setCouleur(couleurs[new Random().nextInt(couleurs.length)]);
                    }

                    Log.d("TroisMV Cercle  ", WhatColor(-1 * cercle.getCouleur()) + "X  " + cercle.getCentre().x + "   Y  " + cercle.getCentre().y);
                }
                score += 100;
                Log.d("SCore  Vertical ", String.valueOf(score));
            }
        }

        if (TroisMH != null)
            TroisMH.clear();
        if (TroisMV != null)
            TroisMV.clear();

    }

    /*************
     * Verifier s'il ya de nouveaux match
     ************/
    public void ScanNewMatch() {

        boolean matchH =false;
        boolean matchV =false;
        int nbmatch=1;

        while(matchH=VerifierMatchHorizontal()) {
            DessinerJeu();
            Log.d("********ScanNewMatch  H", String.valueOf(matchH));
        }
        while(matchV = VerifierMatchVertical()) {
            DessinerJeu();
            Log.d("********ScanNewMatch  V",String.valueOf(matchV));
        }


/*        Log.d("SCore  Vertical ", String.valueOf(score));
        while ((matchV = TroisMatchVertical()) || (matchH = TroisMatchH()) ) {
            RemplacerMatchTrois();
            DessinerJeu();
            Log.d("MatchV ",String.valueOf(matchV)+"   *********MatchH   "+String.valueOf(matchH));
            if (matchH) {
                nbmatch++;
                score += nbmatch * 100;
            }

            if (matchV) {
                nbmatch++;
                score += nbmatch*100;
            }
        }*/

    }

    /***********
     * Annuler un swipe si le doigt bouge seulement a l'interieur d'un cercle
     ***********/
    public void AnnulerSwipe() {
//        if (cercleswiped!=null) {

        contina[cercledown.getCentre().y][cercledown.getCentre().x].setCouleur(cercledown.getCouleur());
        contina[cercleswiped.getCentre().y][cercleswiped.getCentre().x].setCouleur(cercleswiped.getCouleur());
        Log.d("Annuler Swipe", "     Oui");

    }



}