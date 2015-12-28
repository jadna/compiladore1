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

/**
 *
 * @author doug
 */
public class AnalisadorLexico {

    
        private final BufferedReader input;
        private final BufferedWriter output;
        
        ArrayList<String> linhas;
        ArrayList<Token> tks;
        ArrayList<Token> errs;
        
        ArrayList<String> operadores;
        ArrayList<String> delimitadores;
        ArrayList<String> PalavrasReservadas;
        
        //private final int Op [] = {'!', '&', '*', '+', '-', '.', '/', '<', '=', '>', '|'};
        //private final int Dl [] = {';', ',', '(', ')', '{', '}', '[', ']'};
        
    
    public AnalisadorLexico() throws FileNotFoundException, IOException{
        input = new BufferedReader(new FileReader("input.txt"));
        output = new BufferedWriter(new FileWriter("output.txt"));
        
        tks = new ArrayList<>();
        errs = new ArrayList<>();
        linhas = CarregaCodigo(input);
        
        operadores = new ArrayList<>();
        delimitadores = new ArrayList<>();
        PalavrasReservadas = new ArrayList<>();
        
        FillArrays();
        
    }
    
    public void FillArrays(){
        operadores.add("!");
        operadores.add("&");
        operadores.add("*");
        operadores.add("+");
        operadores.add("++");
        operadores.add("-");
        operadores.add("--");
        operadores.add(".");
        operadores.add("/");
        operadores.add("<");
        operadores.add("<=");
        operadores.add("=");
        operadores.add("==");
        operadores.add(">");
        operadores.add(">=");
        operadores.add("|");
        
        /**********************************/
        
        delimitadores.add(";");
        delimitadores.add(",");
        delimitadores.add("(");
        delimitadores.add(")");
        delimitadores.add("{");
        delimitadores.add("}");
        delimitadores.add("[");
        delimitadores.add("]");
        
        /*********************************/
        
        PalavrasReservadas.add("bool");
        PalavrasReservadas.add("char");
        PalavrasReservadas.add("class");
        PalavrasReservadas.add("const");
        PalavrasReservadas.add("else");
        PalavrasReservadas.add("false");
        PalavrasReservadas.add("float");
        PalavrasReservadas.add("if");
        PalavrasReservadas.add("int");
        PalavrasReservadas.add("main");
        PalavrasReservadas.add("new");
        PalavrasReservadas.add("read");
        PalavrasReservadas.add("return");
        PalavrasReservadas.add("string");
        PalavrasReservadas.add("true");
        PalavrasReservadas.add("void");
        PalavrasReservadas.add("while");
        PalavrasReservadas.add("write");
        
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
            
            while(j<line.length()){
                                
                if(line.charAt(j) == '"'){
                    j = CadeiaConst(line, i, j);
                    
                } 


                else if(line.charAt(j) == '\''){
                    j = CaractereConst(line, i, j);

                }   


                else if( (Character.isLetter(line.charAt(j))) && (line.charAt(j) <= 126 && line.charAt(j) >= 32) ){
                    j = Id(line, i, j);

                }
                
                
                else if( delimitadores.contains(""+line.charAt(j)) ){
                    Delimitador(line.charAt(j), i, j);
                }
                
                
                else if(line.charAt(j) == '/'){
                    if( j+1<line.length() && ((line.charAt(j+1)=='/') || (line.charAt(j+1)=='*')) ){//testa se a barra n é o ultimo caractere da linha
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
                                break;   
                            }
                            
                            
                        }
                    }
                    else
                        j = Operador(line, i, j);

                }
                
                
                else if(line.charAt(j)=='-'){
                    int size = tks.size();
                    if(j+1<line.length() && line.charAt(j+1)=='-')
                        j = Operador(line, i, j);
                    else if(size>0){
                        String temp = tks.get(size-1).getLexeme();
                        if ( ( delimitadores.contains(temp) || (operadores.contains(temp) && !temp.equals("++") && !temp.equals("--") ) ) ){
                            boolean found = false;
                            int i2 = i;
                            int j2 = j+1;
                            String line_temp = line;//somente para inicializar. Como i2 = i no inicio, é garantida a entrada no while
                            while(i2<linhas.size() && !found){
                                line_temp = linhas.get(i2);
                                while(j2<line_temp.length() ){
                                    if(!Character.isWhitespace(line_temp.charAt(j2))){
                                        found = true;
                                        break;
                                    }
                                    j2++;    
                                }
                                if(!found){
                                    i2++;
                                    j2 = 0;
                                }
                            }
                        
                            if(found){
                                if(Character.isDigit(line_temp.charAt(j2))){
                                    i = i2;
                                    line = line_temp;
                                    j = Numero(line_temp, i2, j2, true);
                                    
                                }
                                else{
                                    j = Operador(line, i, j);
                                }
                            }
                            else{
                                j = Operador(line, i, j);
                            }
                        
                        }
                        
                        else{
                            j = Operador(line, i, j);
                        }
                    }
                    
                    else{
                        j = Operador(line, i, j);
                    }
                }
                
                
                else if(operadores.contains(""+line.charAt(j))){
                    j = Operador(line, i, j);
                }
                
                else if(Character.isDigit(line.charAt(j))){
                    j = Numero(line, i, j, false);
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
    
    public int Id(String line, int i, int j) throws IOException{
        
        String lexeme = ""+line.charAt(j);
        boolean accept = true;
        j++;
        
        /*((j+1<line.length()) && ( (line.charAt(j)=='!' && line.charAt(j+1)=='=') || (line.charAt(j)=='&' && line.charAt(j+1)=='&') 
        || (line.charAt(j)=='|' && line.charAt(j+1)=='|') )  )*/
        
        while( j<line.length() && !( Character.isWhitespace(line.charAt(j)) || delimitadores.contains(""+line.charAt(j)) || 
                operadores.contains(""+line.charAt(j)) )     ){
            
            lexeme = lexeme + line.charAt(j);
            if( !( ( (Character.isLetterOrDigit(line.charAt(j))) && (line.charAt(j) <= 126 && line.charAt(j) >= 32) ) || line.charAt(j)=='_' ) )
                accept = false;
            j++;
        }
        
            
            
        if(accept){
            if(PalavrasReservadas.contains(lexeme))
                tks.add(new Token(lexeme, "palavra_reservada", i+1, j+1));
            else
                tks.add(new Token(lexeme, "id", i+1, j+1));
        }
        else
            errs.add(new Token(lexeme, "id_mal_formado", i+1, j+1));
        
        j--;
        return j;
    }
    
    public int CadeiaConst(String line, int i, int j) throws IOException{
         
        String lexeme = "\"";
        j++; 
        
        while(j<line.length() && line.charAt(j) <= 126 && line.charAt(j) >= 32){ //intervalo que inclue letras, digitos e os simbolos validos
             
            lexeme = lexeme + line.charAt(j);
            if(line.charAt(j) == '"'){
              tks.add(new Token(lexeme, "cadeia_constante", i+1, j+1));
              return j;
            }
            j++;
         }
                
            while(j<line.length() ){
                    lexeme = lexeme + line.charAt(j);
                    if(line.charAt(j)=='"'){
                        break;
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

        while(j<line.length()){
            lexeme = lexeme + line.charAt(j);
            if(line.charAt(j) == '\'')
                break;
            j++;
        }
        
        errs.add(new Token(lexeme, "caractere_mal_formado", i+1, j+1));
        
        return j;
        
                    
    } 
    
    public int[] ComentarioBloco(int i, int j) throws IOException{
        
            String temp;
            int pos [] = {i, j};
            while (i<linhas.size()){
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
        
        temp = linhas.get(pos[0]).substring(pos[1]);
        errs.add(new Token(temp, "comentário_mal_formado", pos[0]+1, pos[1]+1));
        
        pos[0] = i;
        pos[1] = j;
        return pos;
    }
    
    public void Delimitador(char dl, int i, int j){
        tks.add(new Token(""+dl, "delimitador", i+1, j+1));
    }
    
    public int Operador(String line, int i, int j){
        String lexeme = ""+line.charAt(j);
        if((line.charAt(j)=='!') || (line.charAt(j)=='|') || (line.charAt(j)=='&')){
            if(j+1<line.length()) {
                if ( ((line.charAt(j)=='!') && (line.charAt(j+1)=='=')) || ((line.charAt(j)=='|') && (line.charAt(j+1)=='|')) 
                        || ((line.charAt(j)=='&') && (line.charAt(j+1)=='&')))
                    tks.add(new Token(lexeme+line.charAt(j+1), "operador", i+1, j+1));
                else
                    errs.add(new Token(lexeme+line.charAt(j+1), "operador_mal_formado", i+1, j+1));
                j++;
            }
            else
                errs.add(new Token(lexeme, "operador_mal_formado", i+1, j+1));
                
        } 
        
        else if(j+1<line.length() && operadores.contains(lexeme+line.charAt(j+1))){
            tks.add(new Token(lexeme+line.charAt(j+1), "operador", i+1, j+1));
            j++;
        }
        
        else
            tks.add(new Token(lexeme, "operador", i+1, j+1));
        
        return j;
    }
    
    public int Numero(String line, int i, int j, boolean negativo){ 
    //variavel booleana necessária para adicionar o - 
    //(menos) ao lexema, ja que pode haver varios espaços entre este e o digito.
    //Para não ter que varrer todos os whitespaces ate o digito novamente, o que ja foi    
    //feito no if de -(menos) no método Automato
        
        String lexeme;
        if(negativo){
            lexeme = "-"+line.charAt(j);
        } 
        else
            lexeme = ""+line.charAt(j);
        boolean accept = true;
        int ponto = 0;
        j++;
        while(j<line.length() && !( Character.isWhitespace(line.charAt(j)) || delimitadores.contains(""+line.charAt(j)) || 
                (operadores.contains(""+line.charAt(j))  && line.charAt(j)!='.') )){
            if(!Character.isDigit(line.charAt(j)) && line.charAt(j)!='.'){//varrer ate limitador ***********************************
                accept = false;
                ponto = 2; //caso seja numero mal formado, n pode mais aceitar ponto
            }
            else if(line.charAt(j)=='.'){
                if(j+1<line.length() && Character.isDigit(line.charAt(j+1))){
                    ponto++;
                    if(ponto>1){
                        break;
                    }
                }
                else{
                    break;
                }
                
                
            }
            
            
            lexeme = lexeme + line.charAt(j);
            j++;
        }
        
        if(accept)
            tks.add(new Token(lexeme, "numero", i+1, j+1));
        else
            errs.add(new Token(lexeme, "numero_mal_formado", i+1, j+1));
        
        j--;
        return j;
    }
            
    public ArrayList<String> CarregaCodigo(BufferedReader in) throws IOException{
        ArrayList<String> line = new ArrayList<>();
        String str;
        
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