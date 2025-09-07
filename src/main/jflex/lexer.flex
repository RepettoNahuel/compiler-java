package lyc.compiler;

import java_cup.runtime.Symbol;                       // Necesario para devolver tokens compatibles con CUP
import lyc.compiler.ParserSym;                        // Nombres de los símbolos/tokens definidos en parser.cup (como ParserSym.IF, ParserSym.PLUS, etc.)
import lyc.compiler.model.*;                      
import static lyc.compiler.constants.Constants.*;     // Importa directamente las constantes definidas globalmente (como STRING_MAX_LENGTH, etc.)

import lyc.compiler.files.SymbolTableGenerator;       // Permite insertar constantes y variables en la tabla de símbolos

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}

%{
  private Symbol symbol(int type) {                   //Devuelve un objetos de tipo Symbol (lo que CUP espera) sin valor asociado
    return new Symbol(type, yyline, yycolumn);
  }

  private Symbol symbol(int type, Object value) {     //Devuelve un objetos de tipo Symbol (lo que CUP espera) con valor asociado, por ejemplo, el texto de un identificador o el valor de una constante.
    return new Symbol(type, yyline, yycolumn, value);
  }

  private int stringNbr = 0;                          //Contador para identificar los StringConstant generados automáticamente
%}

//Sección de definiciones de expresiones regulares

/* Espacios, tabulaciones, EOLs */
LineTerminator  = \r|\n|\r\n
InputCharacter  = [^\r\n]
Identation      = [ \t\f]
WhiteSpace = {LineTerminator} | {Identation}

/* Operadores aritméticos */
Plus  = "+"
Sub   = "-"
Mult  = "*"
Div   = "/"

/* Operador de asignación */
Assig = ":="

/* Operadores relacionales */
Equal           = "=="
NotEqual        = "!="
LessEqual       = "<="
GreaterEqual    = ">="
Less            = "<"
Greater         = ">"

/* Operadores lógicos */
And = "and"
Or  = "or"
Not = "not"

/* Delimitadores */
OpenBracket         = "("
CloseBracket        = ")"
OpenCurlyBracket    = "{"
CloseCurlyBracket   = "}"
OpenSquareBracket   = "["
CloseSquareBracket  = "]"
Comma               = ","
Colon               = ":"

/* Palabras clave */
While       = "while"
If          = "if"
Else        = "else"
Init        = "init"
IntType     = "Int"
FloatType   = "Float"
StringType  = "String"
Read        = "read"
Write       = "write"

/* Componentes básicos */
Letter = [a-zA-Z]
Digit  = [0-9]

/* Funciones especiales del lenguaje */
EqualExpressions    = "equalExpressions"
TriangleAreaMaximum = "triangleAreaMaximum"

/* Identificadores */
Identifier = {Letter}({Letter}|{Digit})*

/* Constantes */
IntegerConstant = 0 | [1-9]{Digit}*
FloatConstant   = {Digit}+\.{Digit}* | \.{Digit}+
StringConstant = \"(.*)\"

/* Comentarios */
Comment = "#+"([^#]|#+[^#+])*"+#"

%%

//Cuerpo del analizador léxico (acciones léxicas)  -->  {ExpresionRegular}   {Acción}  (Lo que el lexer (archivo .flex) debe hacer cuando reconoce un patrón (ER). Generalmente, devuelve un token al parser)
/* keywords */

<YYINITIAL> {  
  /* Operadores aritméticos */
  {Plus}                        { return symbol(ParserSym.PLUS); }                  // "+"
  {Sub}                         { return symbol(ParserSym.SUB); }                   // "-"
  {Mult}                        { return symbol(ParserSym.MULT); }                  // "*"
  {Div}                         { return symbol(ParserSym.DIV); }                   // "/"
  
  /* Operador de asignación */
  {Assig}                       { return symbol(ParserSym.ASSIG); }                // ":="

  /* Operadores relacionales */
  {Equal}                       { return symbol(ParserSym.EQUAL); }                 // "=="
  {NotEqual}                    { return symbol(ParserSym.NOT_EQUAL); }             // "!="
  {LessEqual}                   { return symbol(ParserSym.LESS_EQUAL); }            // "<="
  {GreaterEqual}                { return symbol(ParserSym.GREATER_EQUAL); }         // ">="
  {Less}                        { return symbol(ParserSym.LESS); }                  // "<"
  {Greater}                     { return symbol(ParserSym.GREATER); }               // ">"

  /* Operadores lógicos */
  {And}                         { return symbol(ParserSym.AND); }                   // "and"
  {Or}                          { return symbol(ParserSym.OR); }                    // "or"
  {Not}                         { return symbol(ParserSym.NOT); }                   // "not"
  
  /* Delimitadores */
  {OpenBracket}                 { return symbol(ParserSym.OPEN_BRACKET); }          // "("
  {CloseBracket}                { return symbol(ParserSym.CLOSE_BRACKET); }         // ")"
  {OpenCurlyBracket}            { return symbol(ParserSym.OPEN_CURLY_BRACKET); }    // "{"
  {CloseCurlyBracket}           { return symbol(ParserSym.CLOSE_CURLY_BRACKET); }   // "}"
  {OpenSquareBracket}           { return symbol(ParserSym.OPEN_SQUARE_BRACKET); }   // "["
  {CloseSquareBracket}          { return symbol(ParserSym.CLOSE_SQUARE_BRACKET); }  // "]"
  {Comma}                       { return symbol(ParserSym.COMMA); }                 // ","
  {Colon}                       { return symbol(ParserSym.COLON); }                 // ":"

  /* Palabras clave */
  {While}                       { return symbol(ParserSym.WHILE); }                 // "while"
  {If}                          { return symbol(ParserSym.IF); }                    // "if"
  {Else}                        { return symbol(ParserSym.ELSE); }                  // "else"
  {Init}                        { return symbol(ParserSym.INIT); }                  // "init"
  {IntType}                     { return symbol(ParserSym.TYPE_INT); }              // "Int"
  {FloatType}                   { return symbol(ParserSym.TYPE_FLOAT); }            // "Float"
  {StringType}                  { return symbol(ParserSym.TYPE_STRING); }           // "String"
  {Read}                        { return symbol(ParserSym.READ); }                  // "read"
  {Write}                       { return symbol(ParserSym.WRITE); }                 // "write"

  /* Funciones especiales del lenguaje */
  {EqualExpressions}            { return symbol(ParserSym.EQUAL_EXPRESSIONS); }     // "equalExpressions"
  {TriangleAreaMaximum}         { return symbol(ParserSym.TRIANGLE_AREA_MAXIMUM); } // "triangleAreaMaximum"

  /* Identificadores */
  {Identifier}                  { return symbol(ParserSym.IDENTIFIER, yytext()); }  // Devuelve el texto completo del identificador, ejemplo: "variable1"
    
  /* Constantes */  
  {IntegerConstant}             {
                                  String value = yytext();
                                  try {
                                      short val = Short.parseShort(value);          // Verifica que esté en el rango de 16 bits
                                  } catch (NumberFormatException e) {
                                      throw new InvalidIntegerException("Constante entera inválida o fuera de rango: " + value);
                                  }

                                  SymbolTableGenerator.insertNonStringConstant("_" + value, value, "CTE_INTEGER");
                                  return symbol(ParserSym.INTEGER_CONSTANT, value);
                                }

  {FloatConstant}               {
                                  String value = yytext();
                                  try {
                                      float f = Float.parseFloat(value);    // Verifica que esté en el rango de 32 bits
                                      if (Float.isNaN(f) || Float.isInfinite(f)) {
                                        throw new NumberFormatException("Constante float inválida o fuera de rango: " + value);
                                      }
                                  } catch (NumberFormatException e) {
                                      throw new InvalidFloatException("Constante float inválida o fuera de rango: " + value);
                                  }

                                  SymbolTableGenerator.insertNonStringConstant("_" + value, value, "CTE_FLOAT");
                                  return symbol(ParserSym.FLOAT_CONSTANT, value);
                                }

  {StringConstant}              {
                                  String contenido = yytext();
                                  int longitud = contenido.length() - 2; // excluye las comillas

                                  if (longitud > STRING_MAX_LENGTH) {
                                      throw new InvalidLengthException("String demasiado largo: " + contenido);
                                  }

                                  String generatedName = "_stringConstant" + stringNbr++;
                                  SymbolTableGenerator.insertStringConstant(generatedName, contenido, "CTE_STRING", longitud);
                                  return symbol(ParserSym.STRING_CONSTANT, generatedName);
                               }

  /* whitespace */
  {WhiteSpace}                 { /* ignore */ }

  /* Comentarios */
  {Comment}                    { /* ignore */ }
}

/* error fallback */
[^]                            { throw new UnknownCharacterException(yytext()); }
