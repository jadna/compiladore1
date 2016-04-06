/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorlexico;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * UEFS 
 * EXA869 - MI - Processadores de Linguagem de Programação
 * Problema 01 - Analisador Léxico 
 * @author Douglas Almeida Carneiro - 10211167
 * @author Jadna Almeida da Cruz - 091111780
 * @version 1.0
 */
public class AnalisadorLexico {

        //Buffers para leitura e escrita dos arquivos
        private final BufferedReader input;
        private final BufferedWriter output;
        private final File file_out;
        private final File file_in;
        
        //o código lido do arquivo é quebrado em strings e salvas em um ArryList
        private ArrayList<String> linhas;
        private ArrayList<Token> tks; //lista de tokens corretos
        private ArrayList<Token> errs; //lista de tokens errados
        
        private ArrayList<String> operadores;
        private ArrayList<String> delimitadores;
        private ArrayList<String> PalavrasReservadas;
        
        
    
    public AnalisadorLexico() throws FileNotFoundException, IOException{
        file_in = new File ("../"+File.separator+"AnalisadorLexico"+File.separator+"input.txt");
        file_out = new File ("../"+File.separator+"AnalisadorLexico"+File.separator+"output.txt");
        
        if(!file_in.exists())
            file_in.createNewFile();
        
        if(!file_out.exists())
            file_out.createNewFile();
        
        input = new BufferedReader(new FileReader(file_in));
        output = new BufferedWriter(new FileWriter(file_out));
        
        tks = new ArrayList<>();
        errs = new ArrayList<>();
        linhas = CarregaCodigo(input);
        
        operadores = new ArrayList<>();
        delimitadores = new ArrayList<>();
        PalavrasReservadas = new ArrayList<>();
        
        FillArrays();
        
    }
    
    /* Preenche os Arrays de operadores, delimitadores e palavras reservadas*/
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
    
    /*Esse método funciona como o estado inicial do Automato. Dependendo do simbolo, ele
    chama o automato correspondente ao qual o simbolo lido se encaixa. Há dois laços: o maior
    para varrer cada linha do codigo e o menor para processar cada caractere da linha*/
    public void Automato() throws IOException{
        int i = 0; //linha atual
        int j = 0; //coluna atual (caractere atual na linha)
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
                
                /*A barra ser comentário ou operador, por isso é necessário uma checagem (look ahead) antes
                de chamar o automato correto. Se após a primeira barra vier outra basta da um break no
                laço menor e ignorar todo o restante da linha. No caso de o proximo simbolo ser '*' as 
                linhas são processadas até ser encontrado o par (* /). Se nenhum dos casos anteriores forem atendidos,
                o automato de operadores é chamado.
                 */
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
                
                /*para tratar o menos várias situações tem que ser avaliadas. A mais fácil é se colado a ele
                vier outro - (menos), pois trata-se de um operador de decremento. Caso contrário, é testado se ja foi processado
                algum lexema antes. Se sim e este for um delimitador ou um operador binário, então finalmente é testado se o caractere
                seguinte é um digito. Essa situação define um número negtivo, portanto o automato de numeros é chamado. Nas outras 
                ocasiões, é chamado o automato de operador*/
                else if(line.charAt(j)=='-'){
                    if(j+1<line.length() && line.charAt(j+1)=='-')
                        j = Operador(line, i, j);
                    else{
                        int size = tks.size();
                        boolean found = false;
                        int i2 = i;
                        int j2 = j+1;
                        String line_temp = line;//somente para inicializar. Como i2 = i no inicio, é garantida a entrada no while
                        while(i2<linhas.size() && !found){//achar o proximo caractere!=whitespace
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
                                if(size > 0){
                                    String temp = tks.get(size-1).getLexeme();
                                    if( delimitadores.contains(temp) || (operadores.contains(temp) && !temp.equals("++") && !temp.equals("--") ) ){
                                        i = i2;
                                        line = line_temp;
                                        j = Numero(line_temp, i2, j2, true);
                                    }
                                    else {
                                        j = Operador(line, i, j);
                                    }
                                }
                                else {
                                    j = Numero(line_temp, i2, j2, true);
                                }
                            }
                            else {
                                j = Operador(line, i, j);
                            }
                        }
                        else{
                            j = Operador(line, i, j);
                        }
                    }
                    
                }
                
                //Operador tem que ser testado após o if do - (menos) e da /
                else if(operadores.contains(""+line.charAt(j))){
                    j = Operador(line, i, j);
                }
                
                else if(Character.isDigit(line.charAt(j))){
                    j = Numero(line, i, j, false);
                }
                
                else if(!Character.isWhitespace(line.charAt(j))){
                    j = ExpressaoInvalida(line, i, j);
                }
                
                j++;
            }//chave laço dos caracteres    
            i++;
            j = 0;
        } //chave laço das linhas
        
         //Imprime a saída no arquivo
        Print(output, tks);//Primeiro os tokens certos
        output.newLine();
        Print(output, errs);
        
        input.close();
        output.close();
        
    }
    /*Informações gerais comuns a todos os automatos abaixo: Cada simbolo lido vai sendo adicionado
    na variavel lexeme, que no final será adicionada ao Array que lhe cabe (tks ou errs). J sempre
    é retornado no valor do ultimo simbolo lido. Nos casos dos automatos que processem mais de uma linha
    (Numero e ComentarioBloco) as linhas também são atualizadas, sendo que no primeiro esta é atualizada antes
    de ser passada como parametro e não no decorrer da execução do método. */
    
    /*Automato de Identificador. Consome todos os caracteres (válidos ou não) ate que o seu separador seja encontrado
    ou acabe a linha. No caso de um símbolo inválido um boolean serve para indicar o erro*/
    public int Id(String line, int i, int j) throws IOException{
        
        String lexeme = ""+line.charAt(j);
        boolean accept = true;
        j++;
        
        while( j<line.length() && !( Character.isWhitespace(line.charAt(j)) || delimitadores.contains(""+line.charAt(j)) || 
                operadores.contains(""+line.charAt(j)) || line.charAt(j) == '"' || line.charAt(j) == '\'')     ){
            
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
    
    /*Automato de cadeia costante: Consome os caracteres até encontrar o " que fecha a cadeia ou achar um
    simbolo invalido. Se o processamento passar do primeiro while significa que algum erro ocorreu e outro
    while faz o papel de consumir os caracteres necessários para que o proximo token seja processado */
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
    
    
    /*Automato de Caractere Constante: É analisado os 3 caracteres necessários para formar o token. Em caso de erro,
    o While consome o resto da linha ou até achar o ' que feche a cadeia*/
    public int CaractereConst(String line, int i, int j) throws IOException{
        // (symbol >= 48 && symbol <= 57) || (symbol >= 65 && symbol <= 90) || (symbol >= 97 && symbol <= 122)
        String lexeme = "\'";
        j++;
        
        if ( j<line.length() && (Character.isLetterOrDigit(line.charAt(j))) && (line.charAt(j) <= 126 && line.charAt(j) >= 32) ) {
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
    
    /*Automato Comentário em Bloco: O método varre quantas linhas forem necessárias ate achar o * / que feche
    o comentario em bloco. Em caso de erro, a primeira linha do comentario em bloco é impressa*/
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
        //Se chegar até aqui é porque não fechou o comentário e pos ainda ta com os indices do início do bloco (/*)
        temp = linhas.get(pos[0]).substring(pos[1]);
        errs.add(new Token(temp, "comentário_mal_formado", pos[0]+1, pos[1]+1));
        
        pos[0] = i;
        pos[1] = j;
        return pos;
    }
    
    /*Automato de Delimitador: Somente salva o delimitador na lista*/
    public void Delimitador(char dl, int i, int j){
        tks.add(new Token(""+dl, "delimitador", i+1, j+1));
    }
    
    /*Automato de Operador: Se o simbolo for !, & ou | o automato consome sempre dois simbolos, mesmo no caso de erro.
    No caso desses tres, é checado se ha mais um simbolo para ser consumido (podem estar no fim da linha ou EOF). 
    Os outros operadores são tratados pelo método contains do ArrayLits*/
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
    
    /*Automato de Numero: São consumidos todos os caracteres, digitos ou não. Uma variavel boolean é usada para
    acusar o erro quando for salva na lista. No caso do ponto, um int é usado como contador de quantas vezes o mesmo
    foi lido. Antes de anexar o ponto ao lexema, é testado se o proximo simbolo é digito.*/
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
                (operadores.contains(""+line.charAt(j))  && line.charAt(j)!='.') || line.charAt(j) == '"' || line.charAt(j) == '\'' )){
            if(!Character.isDigit(line.charAt(j)) && line.charAt(j)!='.'){//varrer ate limitador ***********************************
                accept = false;
                ponto = 2; //caso seja numero mal formado, não pode mais aceitar ponto
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
    
    /*Trata tokens que começaom com símbolos inválidos (ñtest) ou qualquer outro caractere que não possa iniciar qualquer tipo
    de token ($teste)*/
    public int ExpressaoInvalida(String line, int i, int j){
        String lexeme = "" + line.charAt(j);
        
        j++;
        
        while( j<line.length() && !( Character.isWhitespace(line.charAt(j)) || delimitadores.contains(""+line.charAt(j)) || 
                operadores.contains(""+line.charAt(j)) )     ){
            lexeme = lexeme + line.charAt(j);
            j++;
        }
        
        errs.add(new Token(lexeme, "expressão_inválida", i+1, j+1));
        j--;
        return j;
    }
    
    /*Cada linha do código é salva como uma string no ArrayList*/
    public ArrayList<String> CarregaCodigo(BufferedReader in) throws IOException{
        ArrayList<String> line = new ArrayList<>();
        String str;
        
        while((str = in.readLine())!=null)
            line.add(str);
        
        return line;
    }
    
    /*Salva os Arrays tks e errs no arquivo*/
    public void Print(BufferedWriter out, ArrayList<Token> list) throws IOException{
        int aux = 0;
        Token tk;
        
        while(aux<list.size()){
            
            tk = list.get(aux);
            out.write(tk.getLexeme() + " " + tk.getType() + " " + tk.getLinha());
            out.newLine();
            //System.out.println(tk.getLexeme() + " " + tk.getType() + " " + tk.getLinha());
            aux++;
        }
        
        
    }
    
}