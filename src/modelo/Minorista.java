/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import com.google.gson.Gson;
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
    
    private Inventario inv;
    private AdminInv admin;
    private Generador demanda;
    private IMayorista servidor = null;
    private int dia;
    private int id =-1;
    
    public Minorista(){
        inicializar();
    }
    
    private void inicializar(){
        inv = new Inventario();
        admin = new AdminInv(inv, 5, 50, 52, 1, 2, 7);
        admin.cambiarPolitica(10, 25);
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
             id = servidor.conectarse("h");
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
    
    public void iniciar() throws RemoteException{
        
        
        int orden =(int) demanda.generar();
        String respuesta = servidor.hacerPedido(orden, id);
        Gson g = new Gson();
        Orden o = g.fromJson(respuesta, Orden.class);
        System.out.println(respuesta);
        int espera = o.getDiasEspera();
        servidor.aceptarOrden(id);
        while(true){
            try{
                int aux = servidor.obtenerDia();
                if(dia+5<aux){
                    
                        dia=servidor.obtenerDia();
                        respuesta = servidor.hacerPedido(orden, id);
                        System.out.println(respuesta);
                        //o = g.fromJson(respuesta, Orden.class);
                        //espera = o.getDiasEspera();
                        servidor.aceptarOrden(id);
                    
                    
                }else{
                    //System.out.println(aux + "Esperando...");
                }
            }catch( RemoteException ex){
                System.out.println("Fallo en la conexion!!!");
            }
            
        }
    }
    
    
}
