/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import utilities.CONSTANTES;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 *
 * @author Valeria Barzola
 */
public class GloboVerde extends Globo {
    private final ImageView globoverde;
    
    /**
    * Constructor del globo Verde que setea la imagen definida para este globo y añade las letras creadas
    */
    public GloboVerde(){
        letralabel= new Label();
        Image img = new Image(getClass().getResourceAsStream(
                        CONSTANTES.RUTA_IMGS+"Globo_Verde.png"),
                                150,
                                250,
                                true,
                                true);
        String s = Globo.generarLetra();
        letras.add(s);
        añadirLetras(letras);
        globoverde = new ImageView(img);        
        this.getChildren().addAll(globoverde,letralabel);
    }

    
   
    
}
