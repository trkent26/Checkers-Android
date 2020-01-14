package edu.uga.cs.checkers;

import android.widget.ImageView;

/**
 * CHECKERS
 *
 * MADE BY:
 * THOMAS KENT
 * HARRISON WEESE
 */

public class Checker {

    private int xCord;
    private int yCord;
    private String color;
    private ImageView imageView;

    public Checker(ImageView imageView, int x, int y, String color){
        this.imageView = imageView;
        this.yCord = y;
        this.xCord = x;
        this.color = color;
    }

    public Checker(){
        this.imageView = null;
        this.yCord = 0;
        this.xCord = 0;
        this.color = null;
    }


    public ImageView getImageView(){
        return imageView;
    }

    public String getColor(){
        return color;
    }

    public int getX(){
        return xCord;
    }

    public int getY(){
        return yCord;
    }
}
