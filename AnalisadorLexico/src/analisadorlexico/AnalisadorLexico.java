/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorlexico;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author doug
 */
public class AnalisadorLexico {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
        BufferedReader input = new BufferedReader(new FileReader("input.txt")); 
        int symbol = 0; 
        boolean eof = false;
        int err_count = 0;
        int pos [] = {1, 0};
        String token = "";
        boolean accept = true;
        
        
        while (!eof) {
            token = "";
            accept = true;
            
            symbol = input.read();
            pos [1]++;
            
            if(symbol != '\n' && symbol != -1)
            token = token + (char)symbol;

            switch (symbol){

                case '"':
                    while((symbol = input.read()) != '"' && symbol != '\n' && symbol != -1){
                        token = token + (char) symbol;
                        if(symbol > 126 || symbol < 32) {
                            System.out.println("Erro: Cadeia mal formada (Símbolo Ínvalido)");
                            accept = false;
                            err_count ++;
                        }
                    }
                    
                     if(symbol=='"'){
                        token = token + (char) symbol;
                        if(accept == true)
                        System.out.println(token);
                    } else { //if(symbol == -1 || symbol == '\n')
                        System.out.println("Erro: Cadeia constante não terminada");
                        err_count ++;
                    } 
                        
                    break;
                
                case -1:
                    System.out.println("Numero de erros: "+err_count);
                    eof = true;
                break;

                

            }
               
                    
                    
                    
                
                
                
           
        
        } 
        
        input.close();

   
        
        
    }
    
}
