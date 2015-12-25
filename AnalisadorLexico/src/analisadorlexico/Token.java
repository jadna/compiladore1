/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analisadorlexico;

/**
 *
 * @author doug
 */
public class Token {
    
    private String lexeme;
    private String type;
    private int line;
    private int column;
    
    public Token(String lexeme, String type, int line, int column){
        this.lexeme = lexeme;
        this.type = type;
        this.line = line;
        this.column = column;
        
    }
    
    public String getLexeme(){
        return lexeme;
    }
    
    public String getType(){
        return type;
    }
    
    public int getLinha(){
        return line;
    }
    
    public int getColumn(){
        return column;
    }
    
    
}
