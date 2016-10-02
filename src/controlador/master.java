/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import inventario.modelo.Mayorista;
import modelo.Minorista;
import visa.JFrameConectar;

/**
 *
 * @author Luis David SAHER
 */
public class master {   
    public static void main(String args[]){
        Minorista m = new Minorista();
        JFrameConectar conectar;
        conectar = new JFrameConectar(m);
        conectar.setVisible(true);
    }
}
