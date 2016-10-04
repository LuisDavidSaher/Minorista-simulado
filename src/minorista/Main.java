/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package minorista;

import controlador.master;
import modelo.Minorista;
import visa.JFrameConectar;

/**
 *
 * @author Luis David SAHER
 */
public class Main {
    
    public static void main(String args[]){
        master m = new master();
        Minorista min = new Minorista(m);
        m.controlarMinorista(min);
        new JFrameConectar(m).setVisible(true);
        
    }
}
