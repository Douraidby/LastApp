package com.exemple.cerclemoveapp;

import android.graphics.Point;


/**
 * Created by doura on 2/5/2017.
 */

public class Cercle {

    private Point centre;
    private int couleur;

    //Constructeur du cercle
    public Cercle(Point p, int c){

        centre = new Point(p);
        couleur = c;
    }

    //recuperer le centre
    public Point getCentre() {
        return centre;
    }

    //recuperer la couleur
    public int getCouleur() {
        return couleur;
    }

    //appliquer une couleur
    public void setCouleur(int couleur) {
        this.couleur = couleur;
    }

    //assigner un centre
    public void setCentre(Point centre) {
        this.centre = centre;
    }
    //assigner les coordonnées du centre
    public void setXY(int x,int y)
    {
        this.centre.set(x,y);

    }
    //switcher les couleurs
    public int switchcouleur(int newcolor)
    {
        int oldcolor = couleur;
        this.couleur = newcolor;
        return oldcolor;

    }
}

