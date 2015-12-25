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
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author doug
 */
public class AnalisadorLexico {

    
        private BufferedReader input;
        private BufferedWriter output;
        private int symbol = 0; 
        private String token = "";
        
        ArrayList<String> linhas;
        ArrayList<Token> tks;
        ArrayList<Token> errs;
        
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
        token = "";
        
        tks = new ArrayList<>();
        errs = new ArrayList<>();
        linhas = CarregaCodigo(input);
        
    }
        
        
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        
        AnalisadorLexico AnLex = new AnalisadorLexico();
        AnLex.Automato();
        
    }
    
    public void Automato() throws IOException{
        int i = 0; //linha
        int j = 0; //coluna
        String line;
         while (i<linhas.size()) {
            line = linhas.get(i);
            
//            if(symbol != -1) // o fim do arquivo pode ser alcançado no loop anterior. O valor inicial de symbol é zero
//                symbol = input.read();
            
//            pos [1]++;
            
//            if(symbol != '\n' && symbol != -1)
//                token = token + (char)symbol;
            while(j<line.length()){
                
//                if( !Character.isWhitespace( line.charAt(j) ) )
//                    token = token + line.charAt(j);
                
                if(line.charAt(j) == '"'){
                    j = CadeiaConst(line, i, j);

                } 


                else if(line.charAt(j) == '\''){
                    j = CaractereConst(line, i, j);

                }   


                else if(Character.isLetter(line.charAt(j))){
                    //Id();

                }

                else if(line.charAt(j) == '/'){ //falta implementar escolha de qual automato par / (pode ser comentário ou operador)
                    if(j+1<line.length()){//testar se a barra n é o ultimo caractere da linha
                        if(line.charAt(j+1) == '/')
                            break;
                        else if(line.charAt(j+1) == '*'){
                            int [] pos;
                            pos = ComentarioBloco(i, j);
                            i = pos[0];
                            j = pos[1];
                            
                            if(i<linhas.size())
                                line = linhas.get(i);//atualizar a linha atual
                            else{
                                System.out.println("deu merda nos comments");
                                break;   
                            }
                            
                            
                        }
                    }
                    else;//TIRAAAAAR
                        //Operadores

                }
                j++;
            }//chave laço dos caracteres    
            i++;
            j = 0;
        } //chave laço das linhas
        
        Print(tks);
        System.out.println("");
        Print(errs);
        input.close();
        
    }
    
    public void Id() throws IOException{
        
        symbol = input.read();
        while ( (Character.isLetterOrDigit(symbol)) && (symbol <= 126 && symbol >= 32) || symbol=='_' ){
            token = token + (char)symbol;
        }
        
        
    }
    
    public int CadeiaConst(String line, int i, int j) throws IOException{
         
        String lexeme = "\"";
        j++; 
        
        while(j<line.length()){
             
             //if((symbol == '"') || (Character.isLetterOrDigit(symbol) && (symbol <= 126 && symbol >= 32))){
             if(line.charAt(j) <= 126 && line.charAt(j) >= 32){ //intervalo que inclue letras, digitos e os simbolos validos
                 lexeme = lexeme + line.charAt(j);
                 if(line.charAt(j) == '"'){
                   tks.add(new Token(lexeme, "cadeia_constante", i+1, j+1));
                   return j;
                 }
             } 
             
             else /*if( (line.charAt(j) > 126) || (line.charAt(j) < 32) )*/{
                while(j<line.length() && line.charAt(j)!='"'){
                    lexeme = lexeme + line.charAt(j);
                    j++;
                }
                 
                errs.add(new Token(lexeme, "cadeia_mal_formada", i+1, j+1)); 
                return j;
             }
             j++;
         }
            //se sair do while é pq deu erro
            errs.add(new Token(lexeme, "cadeia_mal_formada", i+1, j+1)); 
            
         
        return j;
    }
    
    public int CaractereConst(String line, int i, int j) throws IOException{
        // (symbol >= 48 && symbol <= 57) || (symbol >= 65 && symbol <= 90) || (symbol >= 97 && symbol <= 122)
        String lexeme = "\'";
        j++;
        
        if ( (Character.isLetterOrDigit(line.charAt(j))) && (line.charAt(j) <= 126 && line.charAt(j) >= 32) ) {
            lexeme = lexeme + line.charAt(j);

            j++;
            if(j<line.length() && line.charAt(j) == '\''){
                lexeme = lexeme + line.charAt(j);
                tks.add(new Token(lexeme, "caracter_constante", i+1, j+1));
                return j;

            }

        } 

        while(j<line.length() && line.charAt(j) != '\''){
            lexeme = lexeme + line.charAt(j);
            j++;
        }
        
        errs.add(new Token(lexeme, "caractere_mal_formado", i+1, j+1));
        
        return j;
        
                    
    } 
    
    public int[] ComentarioBloco(int i, int j) throws IOException{
        
            String temp;
            int pos [] = {i, j};
            System.out.println("J "+j);
            while (i<linhas.size()){
                System.out.println("linha");
                temp = linhas.get(i);
                while(j<temp.length()){
                    if( (temp.charAt(j) == '*') && (j+1<temp.length()) && (temp.charAt(j+1) == '/') ){
                        pos[0] = i;
                        pos[1] = j+1; //precisa incrementar J para que retorne na posição da barra que fecha comentario
                        return pos;
                    }
                    j++;
                }
                i++;
                j = 0;
            }
            
            System.out.println("I1: "+i+" J1: "+j);
        pos[0] = i;
        pos[1] = j;
        return pos;
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
    
    
    public ArrayList<String> CarregaCodigo(BufferedReader in) throws IOException{
        ArrayList<String> line = new ArrayList<>();
        String str = "";
        
        while((str = in.readLine())!=null)
            line.add(str);
        
        
        return line;
    }
    
    public void Print(ArrayList<Token> list){
        int aux = 0;
        Token tk;
        
        while(aux<list.size()){
            
            tk = list.get(aux);
            System.out.println(tk.getLexeme() + " " + tk.getType() + " " + tk.getLinha());
            aux++;
        }
        
        
    }
    
}
