/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Vistas;

import Modelo.Dificultad;
import Modelo.Globo;
import Modelo.GloboAmarillo;
import Modelo.GloboMalo;
import Modelo.GloboRojo;
import Modelo.GloboVerde;
import Modelo.Juego;
import Modelo.MoverGlobo;
import controlador.Controlador;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import utilities.CONSTANTES;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import utilities.Utilities;

/**
 * Clase VistaJuego: ventana de la primera fase del juego
 * @author Valeria Barzola
 */


public class VistaJuego1 {
    Juego juego;
    Pane root;
    Pane Gpane;
    Pane onRoot;
    Pane pMessage;
    Label tiempo;
    int tiempoJuego;
    Label Globos;
    int numeroGlobos;
    private Random random=new Random();
    ImageView i;
    public  ArrayList <Globo> globoslista;
    
    public HashMap<String,Integer> letrasObtenidas;
    public Thread mov;
    public VistaJuego1(Dificultad f){
        juego= new Juego();
        letrasObtenidas= juego.getPlayer_l();
        globoslista = new ArrayList<>();    
        tiempoJuego=60;
        root = CrearElementos();
        iniciarJuego(f);
        Thread cuentareg= new Thread(new HiloTiempo());
        cuentareg.start();        
        
    }
    /**
     * Crea el contenido de la venta, será el contenedor raíz de la ventana
     * @return un Pane con el contendio de la ventana
     */
    
    public Pane CrearElementos(){
        onRoot= new Pane();
        Gpane= new Pane();
        pMessage = new Pane();
        
        Font theFont = Font.font("Aharoni", FontWeight.BOLD, 20 );
        Pane root = new Pane();
        ImageView imv=null;
        switch (Controlador.getSkin()){
            case 1:
                imv = new ImageView(new Image(CONSTANTES.RUTA_IMGS+"BG_02.png"));
                break;
            case 2:
                imv = new ImageView(new Image(CONSTANTES.RUTA_IMGS+"BG_04.png"));
        }        
        
        Image im_tiemp = new Image(getClass().getResourceAsStream(
                        CONSTANTES.RUTA_IMGS+"TextBox.png"),
                                100,
                                200,
                                true,
                                true); 
        ImageView imv_tiemp= new ImageView(im_tiemp);
        
        
        Label l2= new Label("Tiempo transcurrido: ");
        l2.setFont(theFont);
        StackPane  t = new StackPane();       
        tiempo = new Label(String.valueOf(tiempoJuego));
        tiempo.setFont(theFont);
        tiempo.setLayoutX(20);
        tiempo.setLayoutY(20);;
        t.getChildren().addAll(imv_tiemp,tiempo);
        HBox htiempo= new HBox();
        htiempo.setSpacing(20);
        htiempo.getChildren().addAll(l2,t);
        htiempo.setLayoutX(40);
        htiempo.setLayoutY(40);
        htiempo.setMaxWidth(400);
        
        Label lg = new Label("Numero de \n Globos:");
        
        lg.setFont(theFont);
        Image img = new Image(getClass().getResourceAsStream(
                        CONSTANTES.RUTA_IMGS+"Globo_Blanco.png"),
                                100,
                                200,
                                true,
                                true); 
        ImageView im_marcador= new ImageView(img);
        Pane p = new Pane();        
        Globos = new Label(String.valueOf(numeroGlobos)); 
        Globos.setFont(theFont);
        Globos.setLayoutX(20);
        Globos.setLayoutY(20);
        p.getChildren().addAll(im_marcador,Globos);
        HBox marcador= new HBox();
        marcador.setSpacing(20);
        marcador.getChildren().addAll(lg,p);
        marcador.setLayoutX(540);
        marcador.setLayoutY(40);
        
        Button b = new Button();
        b.setLayoutY(100);
        root.getChildren().addAll(new Button(),b,imv,htiempo,Gpane,onRoot,marcador,pMessage);
        root.setOnKeyPressed((e)->{
            String letra = e.getText();
            
            //System.out.println(e.getCode());
            Iterator <Globo> iterator = globoslista.iterator();
            while(iterator.hasNext()){
                Globo g = iterator.next();
                if(g.getLetras().contains(letra) && g.onScreen){
                    //System.out.println(g.getClass().getName());
                    Platform.runLater(()->{
                                onRoot.getChildren().clear();
                            });
                    
                        JuegoGlobos(g,letra);
                        if (letrasObtenidas.keySet().contains(letra.toLowerCase())) {
                                    int cont= letrasObtenidas.get(letra.toLowerCase())+1;
                                    letrasObtenidas.put(letra.toLowerCase(), cont);
                                    if (g instanceof GloboMalo) {
                                        letrasObtenidas.put(letra.toLowerCase(),0);
                                    }
                        }else{
                                    letrasObtenidas.put(letra.toLowerCase(), 1);
                                    if (g instanceof GloboMalo) {
                                        letrasObtenidas.put(letra.toLowerCase(),0);
                                    }
                        }                                                                              
                }
                
            }
            
            
        });
        return root;
    }
    /**
     * Método que realiza la funcion principal del juego, verifica si la letra ingresada coincide con la letra del globo.
     * @param gb El globo sobre el cual se dará la accion de eliminar la letra
     * @param s La letra ingresada por el usuario en el teclado
     */
    
     public void JuegoGlobos(Globo gb, String s){
        Media music = new Media(new File(CONSTANTES.RUTA_SOUNDS+"explosion.mp3").toURI().toString());
        Iterator it = gb.getLetras().iterator();
                while(it.hasNext()){
                    if (it.next().equals(s)) {
                        it.remove();
                        gb.añadirLetras(gb.getLetras(),22,20);
                    }
                }
                if (gb.getLetras().isEmpty()) {
                    Gpane.getChildren().remove(gb);
                    gb.onScreen=false;                   
                    if(Controlador.isSondEsp()){

                        MediaPlayer mp = new MediaPlayer(music);
                        mp.play();
                    }
                    numeroGlobos += 1;
                    Globos.setText(String.valueOf(numeroGlobos));            
                }
                if (gb instanceof GloboMalo) {
                            
                            Globos.setText(String.valueOf(numeroGlobos)); 
                            Label whoops = new Label("Woops, globo malo");
                            whoops.setFont(CONSTANTES.FUENTE);
                            whoops.setTextFill(Color.RED);
                            whoops.setLayoutX(200);
                            whoops.setLayoutY(70);
                            Thread tMessage = new Thread(()->{
                                Platform.runLater(()->{
                                pMessage.getChildren().add(whoops);
                                });
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(VistaJuego1.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                Platform.runLater(()->{
                                    pMessage.getChildren().remove(whoops);
                                });
                            });
                            tMessage.start();
                            
                }
     }
     
     /**
      * Metodo que desactiva la accion OnKeyPressed sobre el globo y presenta los resultados de la primera etapa
      */
    
    public void FinalizarJuego(){
        EliminarLetrasAusentes();
        System.out.println(letrasObtenidas);
        DeshabilitarGlobos();      
        Platform.runLater(()->{
            onRoot.getChildren().clear();
            Pane Over = new Pane();
            ImageView ini = new ImageView(new Image(CONSTANTES.RUTA_IMGS+"BG_IJ02.png"));
            Label ltitulo = new Label("TIME'S OVER");
            ltitulo.setTextFill(Color.WHITE);
            ltitulo.setFont(CONSTANTES.FUENTE);            
            Pane Seguir = Utilities.boton("Seguir");
            Seguir.setLayoutX(500);
            Seguir.setLayoutY(290); 
            Seguir.setOnMouseClicked((e)->{
                VistaJuego2 vj2= new VistaJuego2(juego);
                Utilities.transition(root,vj2.getRoot());
            });
            ltitulo.setLayoutX(150);
            ltitulo.setLayoutY(30);
            ScrollPane scrollp= PaneLetrasObtenidas();
            
            Over.setLayoutX(100);
            Over.setLayoutY(-500);
            
            HBox h= new HBox();
            h.setLayoutX(100);
            h.setLayoutY(90);
            Label l1,l2;
            l1= new Label("Letras");
            l2= new Label ("Stock"); 
            l1.setTextFill(Color.WHITE);
            l2.setTextFill(Color.WHITE);
            
            try {
                Font f = Font.loadFont(new FileInputStream(new File("src/myFonts/Top_p2.ttf")), 24);
                l1.setFont(f);
                l2.setFont(f);
            } catch (FileNotFoundException ex) {
                System.out.println(ex.getMessage());
            }
            h.getChildren().addAll(l1,l2);
            h.setSpacing(60);
            
            Over.getChildren().addAll(ini,ltitulo,h,scrollp,Seguir);
            
            
            Utilities.bajarCartel(onRoot, Over, 200);
            System.out.println(letrasObtenidas);
            
        });  
    }
    
    /**
     * Metodo que crea el scrollPane con las letras obtenidas durante la partida
     * @return Scrollpane con la letra y el sotck de cada una
     */
    
    public ScrollPane PaneLetrasObtenidas(){  
        Font theFont = Font.font("Helvetica", FontWeight.BOLD,24);
        ScrollPane scrollp = new ScrollPane();
        VBox playerLetters= new VBox();
        for(String s: letrasObtenidas.keySet()){
            HBox box= new HBox();
            Label letter= new Label(s);
            Label num= new Label(Integer.toString(letrasObtenidas.get(s))); 
            letter.setFont(theFont);
            num.setFont(theFont);
            box.getChildren().addAll(letter,num);
            playerLetters.getChildren().add(box);          
            box.setSpacing(100);
        }   
        scrollp.setContent(playerLetters);     
        scrollp.setPrefSize(300,180);
        scrollp.setLayoutX(100);
        scrollp.setFitToWidth(true);
        playerLetters.setSpacing(30);
        scrollp.setLayoutY(180); 
        scrollp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollp.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        playerLetters.setPadding(new Insets(20));              
        return scrollp;
  
    }
    
    /**
     * Metodo que retorna el root de la VentanaJuego1
     * @return Pane que cumple la función de ser el root
     */
    
    public Pane getRoot(){
        return root;
    }
    
    /**
     * Método de genera una posición random en el eje x
     * @return random double 
     */
    private double generarPosicionX(){
        double x = random.nextDouble()*(CONSTANTES.WIDTH-150);
        return x;
    }
    
    
    /**
     * Metodo que crea un objeto GloboRojo, lo situa en la ventana y genera el movimiento del mismo
     */
    public void CrearGloboRojo(int tiempo){        
        GloboRojo globor = new GloboRojo();
        double posicionx = generarPosicionX();
        globor.fijarPosicion(posicionx);
        globoslista.add(globor);
        Gpane.getChildren().addAll(globor);        
        MoverGlobo mv = new MoverGlobo(globor,tiempo);
        mov= new Thread(mv);
        mov.start();    
    };
    
    /**
     * Metodo que crea un objeto GloboVerde, lo situa en la ventana y genera el movimiento del mismo
     */
    public void CrearGloboVerde(int tiempo){        
        GloboVerde globov = new GloboVerde();
        globoslista.add(globov);
        double posicionx = generarPosicionX();
        globov.fijarPosicion(posicionx);
           
        Gpane.getChildren().addAll(globov);
        MoverGlobo mv = new MoverGlobo(globov,tiempo);
        mov= new Thread(mv);
        mov.start();          
    };
    
    
    /**
     * Metodo que crea un objeto GloboAmarillo, lo situa en la ventana y genera el movimiento del mismo
     */
    public void CrearGloboAmarillo(int tiempo){        
        GloboAmarillo globoa = new GloboAmarillo();
        globoslista.add(globoa);
        double posicionx = generarPosicionX();
        globoa.fijarPosicion(posicionx);
        Gpane.getChildren().addAll(globoa);
        MoverGlobo mv = new MoverGlobo(globoa,tiempo);
        mov= new Thread(mv);
        mov.start();   
    }
    
    
    
    /**
     * Metodo que crea un objeto GloboMalo, lo situa en la ventana y genera el movimiento del mismo
     */
    public void CrearGloboMalo(int tiempo){
        GloboMalo globom = new GloboMalo();
        globoslista.add(globom); 
        double posicionx = generarPosicionX();
        globom.fijarPosicion(posicionx);
        Gpane.getChildren().addAll(globom);
        MoverGlobo mv = new MoverGlobo(globom,tiempo);
        mov= new Thread(mv);
        mov.start();
    }
    
    private class HiloCrearGlobosMalos implements Runnable{
        @Override
        public void run() {
            while(tiempoJuego!=0){
                Platform.runLater(()->{                                         
                    CrearGloboMalo(7);
                    });
                    try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }                                
            }
        }        
    }
    
    /**
     * Hilo que crea varios globos al mismo tiempo según las especificaiciones del nivel FACIL
     */
         
    private class HiloCrearGlobosFacil implements Runnable{        
        @Override
        public void run() {
            while(tiempoJuego!=0){
                Platform.runLater(()->{                     
                    CrearGloboVerde(10);
                    CrearGloboAmarillo(10);
                    });
                    try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }                                
            }
        }        
    }   
    /**
     * Hilo que crea varios globos al mismo tiempo según las especificaiciones del nivel MEDIO
     */         
    private class HiloCrearGlobosMedio implements Runnable{

        @Override
        public void run() {
            while(tiempoJuego!=0){
                Platform.runLater(()->{                                         
                    CrearGloboRojo(9);
                    CrearGloboVerde(9);
                    CrearGloboAmarillo(9);
                    });
                    try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }                                
            }
        }        
    }    
    /**
     * Hilo que crea varios globos al mismo tiempo según las especificaiciones del nivel DIFICIL
     */         
    private class HiloCrearGlobosDificil implements Runnable{
        @Override
        public void run() {
            while(tiempoJuego!=0){
                Platform.runLater(()->{                     
                    CrearGloboVerde(7);
                    CrearGloboAmarillo(7);
                    CrearGloboRojo(7);                    
                    });
                    try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    System.out.println(ex.getMessage());
                }                               
            }
        }        
    }
    
    /**
     * Hilo que crea cuenta el tiempo de juego y lo muestra por la ventana
     */
         
    private class HiloTiempo implements Runnable{

        @Override
        public void run() {
            
            
            while(tiempoJuego!=0){
                tiempoJuego-=1;
                Platform.runLater(()->{                
                tiempo.setText(String.valueOf(tiempoJuego));                                        
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    
                }
                
            }
            FinalizarJuego();
        }
        
    }
    
 
    /**
     * Inicia el juego según la dificultad pasada como parámetro
     * @param d Dificultad del nivel 
     */
    public void iniciarJuego(Dificultad d){
        switch(d){
            case FACIL:                 
                Thread thfacil = new Thread(new HiloCrearGlobosFacil());                                
                thfacil.start();                                                           
                break;
            case MEDIO:
                Thread thmedio = new Thread(new HiloCrearGlobosMedio());                                
                thmedio.start();                 
                break;                
            case DIFICIL:
                Thread thdif = new Thread(new HiloCrearGlobosDificil()); 
                Thread gm = new Thread(new HiloCrearGlobosMalos() );
                gm.start();
                thdif.start();                  
                break;
        }                                           
    }
    
    public void EliminarLetrasAusentes(){        
        Iterator it = letrasObtenidas.keySet().iterator();
        while(it.hasNext()){
            if (letrasObtenidas.get(it.next())==0) {
                it.remove();
            }
            
        }
    }
    
    public void DeshabilitarGlobos(){
        Iterator <Globo> iterator = globoslista.iterator();
            while(iterator.hasNext()){
                Globo g = iterator.next();
                g.onScreen=false;
                
            }
    }              
    
    
}

