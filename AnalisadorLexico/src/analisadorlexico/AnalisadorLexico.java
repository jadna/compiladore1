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

    
        private BufferedReader input; 
        private int symbol = 0; 
        private boolean eof = false;
        private int err_count = 0;
        private int pos [];
        private String token = "";
        private boolean accept = true;
    
    public AnalisadorLexico() throws FileNotFoundException{
        input = new BufferedReader(new FileReader("input.txt"));
        symbol = 0; 
        eof = false;
        err_count = 0;
        pos = new int[2];
        pos[0] = 1;
        pos[1] = 0;
        token = "";
        accept = true;
        
    }
        
        
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
        AnalisadorLexico AnLex = new AnalisadorLexico();
        AnLex.Automato();
        
    }
    
    public void Automato() throws IOException{
         while (!eof) {
            token = "";
            accept = true;
            
            symbol = input.read();
            pos [1]++;
            
            if(symbol != '\n' && symbol != -1)
            token = token + (char)symbol;
            
                
                    if(symbol == '"'){
                        CadeiaConst();
                     
                    } 
                    
                    
                    else if(symbol == '\''){
                        CaractereConst();
                    
                    }
                    
                    else if(Character.isLetter(symbol)){
                        
                    }
                
                
                    else if(symbol == -1){
                    System.out.println("Numero de erros: "+err_count);
                    eof = true;
                    }

                
        
        } 
        
        input.close();
        
    }
    
    
    public void CadeiaConst() throws IOException{
        
         while((symbol = input.read()) != '"' && symbol != '\n' && symbol != -1){
                        token = token + (char) symbol;
                        if(symbol > 126 || symbol < 32) {
                            System.out.println("Erro: Cadeia constante mal formada (Símbolo Ínvalido)");
                            accept = false;
                            err_count ++;
                        }
                    }
                    
                     if(symbol=='"'){
                        token = token + (char) symbol;
                    } else { //if(symbol == -1 || symbol == '\n')
                        System.out.println("Erro: Cadeia constante terminada incorretamente");
                        err_count ++;
                        accept = false;
                    } 
                     
                     if(accept == true)
                        System.out.println(token);
        
    }
    
    public void CaractereConst() throws IOException{
        symbol = input.read(); // (symbol >= 48 && symbol <= 57) || (symbol >= 65 && symbol <= 90) || (symbol >= 97 && symbol <= 122)
                    if ((Character.isLetter(symbol))||(Character.isDigit(symbol))) {
                        token = token + (char) symbol;
                    } else if (symbol == -1 || symbol == '\n'){
                        System.out.println("Erro: Caractere constante terminado incorretamente");
                        err_count ++;
                        accept = false;
                    } else {
                        System.out.println("Erro: Caractere constate mal formada (Símbolo Ínvalido)");
                        err_count ++;
                        accept = false;
                    }
                    
                    symbol = input.read();
                    
                    if(symbol == '\'')
                        token = token + (char)symbol;
                    else{
                        System.out.println("Erro: Caractere constante terminado incorretamente");
                        err_count ++;
                        accept = false;
                    }
                    
                    if(accept == true)
                        System.out.println(token);
    }
    
}
