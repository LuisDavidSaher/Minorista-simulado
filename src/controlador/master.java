/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Minorista;
import visa.JFrameConectar;
import visa.JFramePrincipal;

/**
 *
 * @author Luis David SAHER
 */
public class master {
    
    private Minorista min;
    private JFramePrincipal ventana;   
    
    
    public master(){
        ventana = new JFramePrincipal();
    }
    
    public void controlarMinorista(Minorista min){
        this.min=min;
    }
    
    public boolean conectar(String ip, int puerto){
        if(min.conectar(ip, puerto)){
            ventana.setVisible(true);
            ventana.mostrarId(min.getId());
            ventana.mostrarDia(min.getDia());
            try {
                min.iniciar();
            } catch (RemoteException ex) {
                Logger.getLogger(master.class.getName()).log(Level.SEVERE, null, ex);
            }            
            ventana.mostrar(min.getDatos());
            return true;
        }
        return false;
    }
    public void mostrarDia(){
        ventana.mostrarDia(min.getDia());
    }
    public void actualizar(){
        ventana.actualizar(min.getDatos());
        ventana.mostrarDia(min.getDia());
    } 
    public void mostar(){
        ventana.mostrar(min.getDatos());
    }  
    
}
