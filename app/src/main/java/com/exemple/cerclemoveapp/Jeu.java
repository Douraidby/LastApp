package com.exemple.cerclemoveapp;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Vector;
import java.util.Random;

/**
 * Created by doura on 2/13/2017.
 */

public class Jeu {

    Activity activite;
    Grille grillejeu;
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
    int level;                      //niveau du jeu
    int couleurs[] = {Color.GREEN, Color.RED, Color.YELLOW, Color.BLUE, 0xFF8B00FF, 0xFFFF7F00};




    //Constructeur du jeu
    public Jeu(Activity act, int niveau){

        level = niveau;
        this.activite = act;
        grillejeu = new Grille(level);
        contina = grillejeu.getLagrille();
        DessinerJeu();
    }


    /***********
     * Retourne les dimensions de la grille selon le niveau
     *********/
    public int[] DimensionsGrille(){

        int[] choix = new int[2];

        if (level ==1) {
            choix[0] = 5;
            choix[1] = 8;  }

        if (level ==2) {
            choix[0] = 6;
            choix[1] = 8;  }

        if (level ==3) {
            choix[0] = 7;
            choix[1] = 7;  }

        if (level ==4) {
            choix[0] = 8;
            choix[1] = 7;  }

        return choix;
    }


    /***********
     * Dessiner la grille du jeu
     *********/
    public void DessinerJeu() {

        Log.d("DessinerJeu", "      Oui");
        int[] c;
        c = DimensionsGrille();

        int d=0;

        if (level ==1)
            d = R.drawable.grid58;
        if (level==2)
            d = R.drawable.grid68;
        if (level ==3)
            d = R.drawable.grid77;
        if (level==4)
            d = R.drawable.grid87;


        bmp = BitmapFactory.decodeResource(this.activite.getResources(), d);
        tempBitmap = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.RGB_565);

        canvas = new Canvas(tempBitmap);
        canvas.drawBitmap(bmp, 0, 0, null);

        float RayonCercle = tempBitmap.getHeight() / (2*c[0]) - 10;   //Rayon d'un cercle
        float lgcarre = (tempBitmap.getWidth() + 7) / c[1];        //+7 et +4 c pour ajuster la largeur et la longeur d'un carré
        float longcarre = (tempBitmap.getHeight() + 4) / c[0];

        Paint monpaint = new Paint();

        for (int i = 0; i < c[1]; i++)
            for (int j = 0; j < c[0]; j++) {
                monpaint.setColor(contina[j][i].getCouleur());
                canvas.drawCircle(contina[j][i].getCentre().x * lgcarre + lgcarre / 2, contina[j][i].getCentre().y * longcarre + longcarre / 2, RayonCercle, monpaint);
            }

        final ImageView monimage = (ImageView) this.activite.findViewById(R.id.imageView);
        monimage.setImageBitmap(tempBitmap);

        TextView scoreaffiche = (TextView) this.activite.findViewById(R.id.score);
        scoreaffiche.setText(String.valueOf(score));

        TextView coupsaffiche = (TextView) this.activite.findViewById(R.id.nbcoups);
        coupsaffiche.setText(String.valueOf(coups));

        swiped = false;
        match = false;
    }

    /***********
     * Gerer l'evenment Down quand on pese avec le doigt et recuperer les 4 voisins
     *********/
    public void GererDown(int x, int y) {

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        voisins = new Cercle[4];
        cercledown = new Cercle(new Point(x, y), contina[y][x].getCouleur());
        Log.d("GererDown  X: ",String.valueOf(cercledown.getCentre().x)+"   Y: "+String.valueOf(cercledown.getCentre().y)+"  Couleur: "+String.valueOf(WhatColor(-1*cercledown.getCouleur())));
        //recuperer les voisins qui sont a l'interieur de la grille seulement
        if (y != 0)
            voisins[0] = new Cercle(contina[y - 1][x].getCentre(), contina[y - 1][x].getCouleur());         //voisin en haut
        else voisins[0] = null;

        if (y != c[0]-1)
            voisins[1] = new Cercle(contina[y + 1][x].getCentre(), contina[y + 1][x].getCouleur());         //voisin en bas
        else voisins[1] = null;

        if (x != 0)
            voisins[2] = new Cercle(contina[y][x - 1].getCentre(), contina[y][x - 1].getCouleur());         //voisin a gauche
        else voisins[2] = null;

        if (x != c[1]-1)
            voisins[3] = new Cercle(contina[y][x + 1].getCentre(), contina[y][x + 1].getCouleur());         //voisin a droite
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

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        CinqMH = new Vector<Cercle>();

        for (int j=0;j<c[0];j++) {
            for (int i = 2; i < c[1]-2; i++) {
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

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        QuatreMH = new Vector<Cercle>();

        for (int j=0;j<c[0];j++) {
            for (int i = 1; i < c[1]-2; i++) {
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

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        TroisMH = new Vector<Cercle>();

        for (int j = 0; j < c[0]; j++)
            for (int i = 1; i < c[1]-1; i++)
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

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        CinqMV = new Vector<Cercle>();

        for (int i = 0; i < c[1]; i++)
            for (int j=2;j<c[0]-2;j++)
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

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        QuatreMV = new Vector<Cercle>();

        for (int i = 0; i < c[1]; i++)
            for (int j = 1; j < c[0]-2; j++)
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

        int[] c = DimensionsGrille();               //Dimensions m=c[0] et k=c[1]
        TroisMV = new Vector<Cercle>();

        for (int i = 0; i < c[1]; i++)
            for (int j = 1; j < c[0]-1; j++)
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
/*    public void RemplacerMatchTrois() {

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

    }*/

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


    public void Message(){

        AlertDialog.Builder dAlert = new AlertDialog.Builder(this.activite);
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

}
