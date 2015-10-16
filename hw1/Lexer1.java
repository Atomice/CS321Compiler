//----------------------------------------------------------------------
// A starter version of miniJava lexer (manual version). (For CS321 HW1)
//----------------------------------------------------------------------
//
// Include your name here.
// JASON HANNAN
//
import java.io.*;

public class Lexer1 {
  private static FileReader input = null;
  private static int nextC = -1;   // buffer for holding next char	
  private static int line = 1;     // currect line position
  private static int column = 0;   // currect column position
  
  // Internal token code
  //
  enum TokenCode {
    // Tokens with multiple lexemes
    ID, INTLIT, DBLLIT, STRLIT,

    // Keywords
    //   "class", "extends", "static", "public", "main", "void", "boolean", 
    //   "int", "double", "String", "true", "false", "new", "this", "if", 
    //   "else", "while", "return", "System", "out", "println"
    CLASS, EXTENDS, STATIC, PUBLIC, MAIN, VOID, BOOLEAN, INT, DOUBLE, STRING, 
    TRUE, FALSE, NEW, THIS, IF, ELSE, WHILE, RETURN, SYSTEM, OUT, PRINTLN,

    // Operators and delimiters
    //   +, -, *, /, &&, ||, !, ==, !=, <, <=, >, >=, =, 
    //   ;, ,, ., (, ), [, ], {, }
    ADD, SUB, MUL, DIV, AND, OR, NOT, EQ, NE, LT, LE, GT, GE,  ASSGN,
    SEMI, COMMA, DOT, LPAREN, RPAREN, LBRAC, RBRAC, LCURLY, RCURLY;
  }

  // Token representation
  //
  static class Token {
    TokenCode code;
    String lexeme;
    int line;	   	// line # of token's first char
    int column;    	// column # of token's first char
    
    public Token(TokenCode code, String lexeme, int line, int column) {
      this.code=code; this.lexeme=lexeme;
      this.line=line; this.column=column; 
    }

    public String toString() {
      return String.format("(%d,%2d) %-10s %s", line, column, code, 
			   (code==TokenCode.STRLIT)? "\""+lexeme+"\"" : lexeme);
    }
  }

  static void init(FileReader in) throws Exception { 
    input = in; 
    nextC = input.read();
  }

  //--------------------------------------------------------------------
  // Do not modify the code listed above. Add your code below. 
  //
  // LexError Formatter
  //
 
  static class LexError extends Exception {
    public LexError(int line, int column, String msg) {
      super("at (" + line + "," + column + ") " + msg);
    }
  }
  // Return next char
  //
  // - need to track both line and column numbers
  // 
  private static int nextChar() throws Exception {
    // ... add code ...
    try{
      nextC = input.read();
      if ( nextC == -1 ){
        return 1;
      } 
      ++column;
      if( Integer.toString(nextC) == "\n" || Integer.toString(nextC) == "\r" ){
        column = 0;
        ++line;
      }
      return 1;
    }
    catch(Exception e){
      
      return 0;
    }
  }

  // Return next token (the main lexer routine)
  //
  // - need to capture the line and column numbers of the first char 
  //   of each token
  //
  static Token nextToken() throws Exception {
    // ... add code ...
   
    //flag variable to catch return from nextChar()
    int flag;
    //Token object
    Token token = null;
    //Token string buffer
    String buffer = "";
    
    //Build Token Section
    try{
      //If EOF was read
      if( nextC == -1 ){
        return null;
      }
      //Non-Token Skipping
      while( nextC == (int)' ' || nextC == (int)'\n' || nextC == (int)'\r' ){
        flag = nextChar();
      }

      //Build String
      //Identify Issues or Token Types
      token = new Token(null, null, line, column);
      //Digits First
      if( isDigit(nextC) ){
        //Special formed digits: HEX, OCT
        if( nextC == 0 ){
          buffer.append((char)nextC);
          flag = nextChar();
          //INTLIT special case of "0"
          if ( nextC == -1 || nextC == (int)" " ){
             buffer.append((char)nextC);
             token.lexeme = new String(buffer);
             token.code = INTLIT;
             return token;
          }
          //HEX
          if( nextC == (int)"x" ){
            buffer.append((char)nextC);
            flag = nextChar();
            if( ! isHex(nextC) && isLetter(nextC) ){
              throw new LexError(line, column, "Malformed HEX");
            }
          }
        }
      }
      
    }
    catch(Exception e){
      return null;
    }
  }




// Utility routines
  //
  private static boolean isLetter(int c) {
    return (('A' <= c) && (c <= 'Z')
            || ('a' <= c) && (c <= 'z'));
  }

  private static boolean isDigit(int c) {
    return ('0' <= c) && (c <= '9');
  }

  private static boolean isOctal(int c) {
    return ('0' <= c) && (c <= '7');
  }

  private static boolean isHex(int c) {
    return ('0' <= c) && (c <= '9')
            || ('A' <= c) && (c <= 'F')
            || ('a' <= c) && (c <= 'f');
  }


}














