/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import com.google.gson.Gson;
import controlador.master;
import generador.Generador;
import inventario.AdminInv;
import inventario.Inventario;
import inventario.modelo.IMayorista;
import inventario.modelo.Orden;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Luis David SAHER
 */
public class Minorista {
    
    
    private Generador demanda;
    private IMayorista servidor = null;
    private int dia;
    private int id =-1;
    private Orden o;
    private Gson g;
    private boolean correr;
    private master m;
    private String[] datos;
    
    public Minorista(master m){
        g = new Gson();
        o= new Orden(0,0,0);
        inicializar();
        correr=false;
        this.m = m;
        datos = new String[4] ;
    }
    
    private void inicializar(){
        demanda = new Generador();
        demanda.addOpcion(25, 0.02);
        demanda.addOpcion(26, 0.04);
        demanda.addOpcion(27, 0.03);
        demanda.addOpcion(28, 0.12);
        demanda.addOpcion(29, 0.2);
        demanda.addOpcion(30, 0.24);
        demanda.addOpcion(31, 0.15);
        demanda.addOpcion(32, 0.10);
        demanda.addOpcion(33, 0.05);
        demanda.addOpcion(34, 0.02);
    }
    
    public boolean conectar(String ip, int puerto){
        try {
            Registry registro=  LocateRegistry.getRegistry(ip,puerto);
             servidor = (IMayorista)registro.lookup("Mayorista");
             id = servidor.conectarse("S");
             dia = servidor.obtenerDia();
             System.out.println("Conectado!!");
             return true;
        } catch (RemoteException ex) {
            System.out.println(" no se puede conectar");
           // Logger.getLogger(VistaMinoristaHumano.class.getName()).log(Level.SEVERE, null, ex);
        }  catch (NotBoundException ex) {
            System.out.println(" no se puede conectar");
           //    Logger.getLogger(VistaMinoristaHumano.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return false;
    }
    
    public void correr(){
        
        Thread hilo = new Thread() {
            @Override
            public void run() {
                super.run();
                
                try {            
                    int d;
                    int orden;
                    correr=true;
                    while(correr){
                        d = servidor.obtenerDia();
                        m.mostrarDia();
                        if(d>dia){
                            dia = d;
                            
                            if(o.getDiasEspera()<=0){
                                
                                //generar demanda
                                if(servidor.numeroMinoristas()==0){
                                    orden =(int) demanda.generar();
                                }else{
                                    orden =(int) demanda.generar()/servidor.numeroMinoristas();
                                }                             
                             
                                String respuesta = servidor.hacerPedido(orden, id);
                                System.out.println(respuesta);
                                servidor.aceptarOrden(id);                                
                                try{
                                    recibir();//recibir orden pendiente
                                    m.actualizar();//actualizar recibido en la vista                                   
                                    o = g.fromJson(respuesta, Orden.class);
                                    guardar();//guadar los datos de oreden de la nueva oreden
                                    m.mostar();//mostrar datos de la nueva oreden
                                    
                                }catch(Exception ex){                                    
                                    o.setDiasEspera((o.getDiasEspera()-1));
                                    actualizar();//actualizar los datos de la oreden
                                    m.actualizar();
                                    System.out.println(ex.toString());
                                }   
                            }else{
                                o.setDiasEspera((o.getDiasEspera()-1));
                                actualizar();//actualizar los datos de la oreden
                                m.actualizar();
                            } 
                        }                
                    }
                } catch (RemoteException ex) {
                    System.out.println("Fallo la Conexion");
                    Logger.getLogger(Minorista.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        hilo.start();
    }
    
    
    
    public void iniciar() throws RemoteException{        
        int orden;
        //generar primera demanda
        if(servidor.numeroMinoristas()==0){
            orden =(int) demanda.generar();
        }else{
            orden =(int) demanda.generar()/servidor.numeroMinoristas();
        }
        
        String respuesta = servidor.hacerPedido(orden, id); //Realizar pedido y capturar respuesta del servidor
        
        try{
            o = g.fromJson(respuesta, Orden.class); // Traducir la respuesta
            servidor.aceptarOrden(id); // Aceptar el pedido;
            guardar();//guadar los datos de oreden de la nueva oreden
            
        }catch(Exception ex){
            System.out.println(ex.toString());
        }

        correr();//Empezar la simulacion automatica        
    }

    public int getDia() {
        return dia;
    }

    public void setDia(int dia) {
        this.dia = dia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Orden getO() {
        return o;
    }
    
    public void guardar(){
        datos[0]=""+o.getCantidad();
        datos[1]=""+o.getDiasEspera();
        datos[2]=""+o.getDiasEspera();
        datos[3]=""+o.isEntregado();
    }
    
    public void actualizar(){
        datos[2]=""+o.getDiasEspera();
    }
    
    public void recibir(){
        datos[3]=""+true;
    }
  
    public String[] getDatos(){  
        return datos;
    }

    public void setO(Orden o) {
        this.o = o;
    }
    
    
    
}
