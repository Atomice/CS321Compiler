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
  private static int column = 1;   // currect column position
  
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
/*  static public void LexError(int line, int column, String msg, String buffer){
    System.err.println("Lexer$Lexer1:at (" + line + "," + column + "). " + msg + ": " + buffer );
    return;
  } */

  static class LexError extends Exception {
    public LexError(int line, int column, String msg, String bf) {
      super("at (" + line + "," + column + "). " + msg + ": " + bf);
    }
    public LexError(int line, int column, String msg) {
      super("at (" + line + "," + column + "). " + msg);
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
      if( nextC == (int)'\n' || nextC == (int)'\r' ){
        column = 0;
        ++line;
      }
      return 1;
    }
    catch(Exception e){
      System.out.println("badread");
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
    StringBuilder buffer = new StringBuilder();
    //END OF FILE
      if( nextC == -1 ){
        return null;
      }
    
    //Build Token Section
      //Non-Token Skipping
      while( nextC == (int)' ' || nextC == (int)'\n' || nextC == (int)'\r' ){
        try{
          flag = nextChar();
        }
        catch(Exception e){
          System.out.println("Bad Read");
          return null;
        }
      }
      token = new Token(null, null, line, column);
      if( isIllegal(nextC) ){
        buffer.append((char)nextC);
        throw new LexError(token.line, token.column, "Illegal character", buffer.toString());
      }
      //Build String
      //Identify Issues or Token Types
      //Digits First
      if( isDigit(nextC) ){
        //Special formed digits: HEX, OCT
        if( nextC == '0' ){
          buffer.append((char)nextC);
          flag = nextChar();
          //INTLIT special case of "0"
          if ( nextC == -1 || ( !isOctal(nextC) && nextC != 'x' && nextC != 'X' && isDigit(nextC))){
             buffer.append((char)nextC);
             throw new LexError(token.line, token.column, "Ill-formed Octal", buffer.toString());
          }
          //DBLLIT with 0 leading
          if( nextC == '.' ){
            buffer.append((char)nextC);
            flag = nextChar();
            while( isDigit(nextC) ){
              buffer.append((char)nextC);
              flag = nextChar();
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.DBLLIT;
            return token;
          }
          //HEX
          if( nextC == 'x' || nextC == 'X' ){
            buffer.append((char)nextC);
            flag = nextChar();
            if( !isHex(nextC) && isLetter(nextC) ){
              buffer.append((char)nextC);
              throw new LexError(token.line, token.column, "Ill-formed Hex", buffer.toString());
            }
            while( isHex(nextC) ){
              buffer.append((char)nextC);
              flag = nextChar();
            }
            token.code = TokenCode.INTLIT;
            token.lexeme = buffer.toString();
            try{
              int z = Integer.parseInt(Integer.toHexString(Integer.decode(buffer.toString())), 16);
            }
            catch(Exception e){
              throw new LexError(token.line, token.column, "Invalid hex literal", buffer.toString());
            }
            return token;
          }
          //OCT
          if( isOctal(nextC) ){
            buffer.append((char)nextC);
            flag = nextChar();
            while( isOctal(nextC) ){
              buffer.append((char)nextC);
              flag = nextChar();
            }
            token.code = TokenCode.INTLIT;
            token.lexeme = buffer.toString();
            try{
              int z = Integer.parseInt(token.lexeme, 8);
            }
            catch(Exception e){
              throw new LexError(token.line, token.column, "Invalid octal literal", buffer.toString());
            }
            return token;
          }
          //Already found all invalid varients. Rest is INTLIT
          token.code = TokenCode.INTLIT;
          token.lexeme = buffer.toString();
          return token;
        }
        //Regular INTLITS
        else{
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          while( isDigit(nextC) ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
          }
          if( nextC == (int)'.' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            while( isDigit(nextC) ){
              buffer.append((char)nextC);
              try{
                flag = nextChar();
              }
              catch(Exception e){
                System.out.println("Bad Read");
                return null;
              }
            }
            token.code = TokenCode.DBLLIT;
            token.lexeme = buffer.toString();
            return token;
          }
          token.code = TokenCode.INTLIT;
          token.lexeme = buffer.toString();
          try{
            int z = Integer.parseInt(token.lexeme, 10);
          }
          catch(Exception e){
            throw new LexError(token.line, token.column, "Invalid decimal literal", buffer.toString());
          }
          return token;
        }
      }
      //DONE WITH DIGITS
      //NEXT STRLIT
      if( nextC == (int)'"'){
        flag = nextChar();
        while(true){
          if( nextC == (int)'"'){
            token.lexeme = buffer.toString();
            flag = nextChar();
            token.code = TokenCode.STRLIT;
            return token;
          }
          if( nextC == (int)'\n' || nextC == (int)'\r' || nextC == -1 ){
            if( nextC == -1 ){
              StringBuilder nbf = new StringBuilder();
              nbf.append("\"");
              nbf.append(buffer.toString());
              throw new LexError(token.line, token.column, "Ill-formed or unclosed string", nbf.toString());
            }
            buffer.append((char)nextC);
            StringBuilder nbf = new StringBuilder();
            nbf.append("\"");
            nbf.append(buffer.toString());
            throw new LexError(token.line, token.column, "Ill-formed or unclosed string", nbf.toString());
          }
          buffer.append((char)nextC);
          flag = nextChar();
        }
      }
      //DONE WITH STRLIT
      //NEXT, KEYWORDS AND IDENTIFIERS
      if( isLetter(nextC) ){
        buffer.append((char)nextC);
        try{
          flag = nextChar();
        }
        catch(Exception e){
          System.out.println("Bad Read");
          return null;
        }
        while( isLetter(nextC) || isDigit(nextC) ){
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
        }
        token.lexeme = buffer.toString();
        switch(token.lexeme){
          case "class":
            token.code = TokenCode.CLASS;
            return token;
          case "extends":
            token.code = TokenCode.EXTENDS;
            return token;
          case "static":
            token.code = TokenCode.STATIC;
            return token;
          case "public":
            token.code = TokenCode.PUBLIC;
            return token;
          case "main":
            token.code = TokenCode.MAIN;
            return token;
          case "void":
            token.code = TokenCode.VOID;
            return token;
          case "boolean":
            token.code = TokenCode.BOOLEAN;
            return token;
          case "int":
            token.code = TokenCode.INT;
            return token;
          case "double":
            token.code = TokenCode.DOUBLE;
            return token;
          case "String":
            token.code = TokenCode.STRING;
            return token;
          case "true":
            token.code = TokenCode.TRUE;
            return token;
          case "false":
            token.code = TokenCode.FALSE;
            return token;
          case "new":
            token.code = TokenCode.NEW;
            return token;
          case "this":
            token.code = TokenCode.THIS;
            return token;
          case "if":
            token.code = TokenCode.IF;
            return token;
          case "else":
            token.code = TokenCode.ELSE;
            return token;
          case "while":
            token.code = TokenCode.WHILE;
            return token;
          case "return":
            token.code = TokenCode.RETURN;
            return token;
          case "System":
            token.code = TokenCode.SYSTEM;
            return token;
          case "out":
            token.code = TokenCode.OUT;
            return token;
          case "println":
            token.code = TokenCode.PRINTLN;
            return token;
          default:
            token.code = TokenCode.ID;
            return token;
        }
      }
      //KEYWORDS and ID's DONE
      //DELIMITERS, OPERATORS, and COMMENTS
      switch(nextC){
        //DOUBLE READ SECTION EXCEPT FOR COMMENTS
        //AND
        case (int)'&':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          if( nextC == (int)'&' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.AND;
            return token;
          }
          throw new LexError(token.line, token.column, "Non-Token", buffer.toString());
        //OR
        case (int)'|':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          if( nextC == (int)'|' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.OR;
            return token;
          }
          throw new LexError(token.line, token.column, "Non-Token", buffer.toString());
        // == and = 
        case (int)'=':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          if( nextC == (int)'=' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.EQ;
            return token;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.ASSGN;
          return token;
        //! and !!
        case (int)'!':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          if( nextC == (int)'=' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.NE;
            return token;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.NOT;
          return token;
        //< and <=
        case (int)'<':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          if( nextC == (int)'=' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.LE;
            return token;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.LT;
          return token;
        // > and >=
        case (int)'>':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          if( nextC == (int)'=' ){
            buffer.append((char)nextC);
            try{
              flag = nextChar();
            }
            catch(Exception e){
              System.out.println("Bad Read");
              return null;
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.GE;
            return token;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.GT;
          return token;
        //SINGLE READ SECTION
        //ADD
        case (int)'+':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.ADD;
          return token;
        //SUB
        case (int)'-':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.SUB;
          return token;
        //MUL
        case (int)'*':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.MUL;
          return token;
        //;
        case (int)';':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.SEMI;
          return token;
        //,
        case (int)',':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.COMMA;
          return token;
        //.
        case (int)'.':
          buffer.append((char)nextC);
          flag = nextChar();
          if( isDigit(nextC) ){
            buffer.append((char)nextC);
            flag = nextChar();
            while( isDigit(nextC) ){
              buffer.append((char)nextC);
              flag = nextChar();
            }
            token.lexeme = buffer.toString();
            token.code = TokenCode.DBLLIT;
            return token;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.DOT;
          return token;
        //(
        case (int)'(':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.LPAREN;
          return token;
        //)
        case (int)')':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.RPAREN;
          return token;
        //[
        case (int)'[':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.LBRAC;
          return token;
        //]
        case (int)']':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.RBRAC;
          return token;
        //{
        case (int)'{':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.LCURLY;
          return token;
        //}
        case (int)'}':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          token.lexeme = buffer.toString();
          token.code = TokenCode.RCURLY;
          return token;
        //DIV, SINGLE AND MULTI-COMMENT
        case (int)'/':
          buffer.append((char)nextC);
          try{
            flag = nextChar();
          }
          catch(Exception e){
            System.out.println("Bad Read");
            return null;
          }
          //SINGLE LINE COMMENTS
          if( nextC == (int)'/' ){
            flag = nextChar();
            while( nextC != (int)'\n' && nextC != (int)'\r' && nextC != -1 ){
              try{
                flag = nextChar();
              }
              catch(Exception e){
                System.out.println("Bad Read");
                return null;
              }
            }
            buffer = new StringBuilder();
            return nextToken();
          }
          //MULTI-LINE COMMENTS
          if( nextC == '*' ){
            flag = nextChar();
            //Read until EOF which causes a error
            while( nextC != -1 ){
              if( nextC == '*' ){
                flag = nextChar();
                if( nextC == (int)'/' ){
                  flag = nextChar();
                  return nextToken();
                }
                continue;
              }
              flag = nextChar();
            }
            //Error in multi-line comments
            throw new LexError(token.line, token.column, "Unclosed block comments");
          }
          //Single / operator
          token.lexeme = buffer.toString();
          token.code = TokenCode.DIV;
          return token;
        default:
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
  
  private static boolean isIllegal(int c){
    return ('#' == c);
  }

}














