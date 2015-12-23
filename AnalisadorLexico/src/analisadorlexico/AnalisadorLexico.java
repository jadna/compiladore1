/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorlexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

/**
 *
 * @author doug
 */
public class AnalisadorLexico {

    
        private BufferedReader input;
        private BufferedWriter output;
        private int symbol = 0; 
        private boolean eof = false;
        private int err_count = 0;
        private int pos [];
        private String token = "";
        private boolean accept = true;
        private final int Op [] = {33, 38, 42, 43, 45, 46, 47, 60, 61, 62, 124};
        private final int Dl [] = {';', ',', '(', ')', '{', '}', '[', ']'};
        private final String Reservadas [] = {"bool", "char", "class", "const", "else", 
                                              "false", "float", "if", "int", "main", 
                                              "new", "read", "return", "string", 
                                              "true", "void", "while", "write" };
    
    public AnalisadorLexico() throws FileNotFoundException, IOException{
        input = new BufferedReader(new FileReader("input.txt"));
        output = new BufferedWriter(new FileWriter("output.txt"));;
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
            
            if(symbol != -1) // o fim do arquivo pode ser alcançado no loop anterior. O valor inicial de symbol é zero
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
                    //Id();
                    System.out.println("entrou");

                }


                else if(symbol == -1){
                    System.out.println("Numero de erros: "+err_count);
                    eof = true;

                }


                else if(symbol == '/'){ //falta implementar escolha de qual automato par / (pode ser comentário ou operador)
                    Comentario();

                }

                
        
        } 
        
        input.close();
        
    }
    
    public void Id() throws IOException{
        
        symbol = input.read();
        while ( (Character.isLetterOrDigit(symbol)) && (symbol <= 126 && symbol >= 32) || symbol=='_' ){
            token = token + (char)symbol;
        }
        
        
    }
    
    public void CadeiaConst() throws IOException{
        
         while(true){
             symbol = input.read();
             //System.out.println((char)symbol);
             
             //if((symbol == '"') || (Character.isLetterOrDigit(symbol) && (symbol <= 126 && symbol >= 32))){
             if(symbol <= 126 && symbol >= 32){ //intervalo que inclue letras, digitos e os simbolos validos
                 token = token + (char) symbol;
                 
                 if(symbol == '"'){
                   System.out.println(token);
                   break;
                 }
             } 
             
             else if (symbol == '\n' || symbol == -1){ // esse if antes do de simbolo invalido por cusada da intersecção -1 < 32
                 
                System.out.println("Erro: Cadeia constante terminada incorretamente");
                err_count ++;
                break;
                 
             }
             
             else if( (symbol > 126) || (symbol < 32) ){
                 
                System.out.println("Erro: Cadeia constante mal formada (Símbolo Ínvalido)");
                err_count ++;
                accept = false;
                
                 
             }
             
         }
         
        
    }
    
    public void CaractereConst() throws IOException{
        symbol = input.read(); // (symbol >= 48 && symbol <= 57) || (symbol >= 65 && symbol <= 90) || (symbol >= 97 && symbol <= 122)
        if ( (Character.isLetterOrDigit(symbol)) && (symbol <= 126 && symbol >= 32) ) {
            token = token + (char) symbol;

            symbol = input.read();

            if(symbol == '\''){
                token = token + (char)symbol;
                System.out.println(token);

            }
            else{
                //System.out.println("Erro: Caractere constante mal formado");

                while((symbol = input.read()) != '\n' && symbol != '\'');

                if(symbol == '\'')
                    System.out.println("Erro: Caractere constante mal formado (tamanho invalido)");
                else
                    System.out.println("Erro: Caractere constante mal formado (unclosed)");


                err_count ++;
            }

        } else if (symbol == -1 || symbol == '\n'){
            System.out.println("Erro: Caractere constante terminado incorretamente");
            err_count ++;
        } else if(symbol == '\'') {
            System.out.println("Erro: Caractere constate vazio");
            err_count ++;

        } else {
            System.out.println("Erro: Caractere constate mal formada (Símbolo Ínvalido)");
            while((symbol = input.read()) != '\n' && symbol != '\'')//e se vier -1?
                System.out.println(symbol);;
            err_count ++;
        }
                    
    } 
    
    public void Comentario() throws IOException{
        symbol = input.read();
                      
        if(symbol == '/'){
            String temp = input.readLine();
            if(temp == null){
                symbol = -1;
            }
            else
                System.out.println("//"+temp);
        }
        else if(symbol == '*'){
            boolean endCom = false;
            symbol = input.read();
            int aux;
            System.out.print("/*");
            while (!endCom && symbol != -1){
                aux = symbol;
                symbol = input.read();
                if( (aux == '*') && (symbol == '/') ){
                    endCom = true;
                }
                System.out.print((char)symbol);
            }
            System.out.println("");
        }
    }  
    
    public boolean ArrayContains(int valor, int [] array) {
        for (int i = 0; i < array.length; i++) {
            if (valor == array[i]) {
                return true;
            }
        }
        return false;
    }
    
    public int FindEndCC() throws IOException{ //find the end of a constant string
        
        
        boolean end = false;
        int trash = 0;
        
        while(!end){
            input.mark(100);
            symbol = input.read();
            
            

            if(ArrayContains(symbol, Op)){
                int aux = input.read();
                if( ( (symbol == '!') && (aux != '=') ) || ( (symbol=='&') && (aux != '&') ) || 
                        (symbol=='|') && (aux != '|') ){
                    
                    trash++;
                    
                }
                else 
                    end = true;
            } 
            else if ( ((ArrayContains(symbol, Dl)) || symbol=='\n' || symbol == -1 || Character.isWhitespace(symbol)) ){
                end = true;

            }
            else
                trash++;
        }
       
        input.reset();
 
        
        return trash;
    }
    
}
