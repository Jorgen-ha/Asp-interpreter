// © 2021 Dag Langmyhr, Institutt for informatikk, Universitetet i Oslo

package no.uio.ifi.asp.scanner;

import java.io.*;
import java.util.*;

import no.uio.ifi.asp.main.*;

import static no.uio.ifi.asp.scanner.TokenKind.*;

public class Scanner {
    private LineNumberReader sourceFile = null;
    private ArrayList<Token> curLineTokens = new ArrayList<>();
    private Stack<Integer> indents = new Stack<>();


    public Scanner(String fileName) {
	String curFileName = fileName;
	indents.push(0);

	try {
	    sourceFile = new LineNumberReader(
			    new InputStreamReader(
				new FileInputStream(curFileName),
				"UTF-8"));
	} catch (IOException e) {
	    scannerError("Cannot read " + curFileName + "!");
	}
    }


    private void scannerError(String message) {
	String m = "Asp scanner error";
	if (curLineNum() > 0)
	    m += " on line " + curLineNum();
	m += ": " + message;

	Main.error(m);
    }


    public Token curToken() {
	while (curLineTokens.isEmpty()) {
	    readNextLine();
	}
	return curLineTokens.get(0);
    }


    public void readNextToken() {
	if (! curLineTokens.isEmpty())
	    curLineTokens.remove(0);
    }



    private void readNextLine() {
	curLineTokens.clear();

	// Read the next line:
	String line = null;
	try {
	    line = sourceFile.readLine();
	    if (line == null) {
		sourceFile.close();
		sourceFile = null;
	    } else {
		Main.log.noteSourceLine(curLineNum(), line);
		}
	} catch (IOException e) {
		sourceFile = null;
		scannerError("Unspecified I/O error!");
	}

	//-- Must be changed in part 1:			- CHANGED

	if(line == null){
        while(indents.peek() > 0){
            indents.pop();
            curLineTokens.add(new Token(dedentToken, curLineNum()));
        }
		curLineTokens.add(new Token(eofToken, curLineNum()));		//If line = null we've found the end of the file
	}else if(!isLineEmpty(line)){									//Checks if the line should be ignored or not
		line = expandLeadingTabs(line);								//Change TABS to right amount of blanks
		int indent = findIndent(line);								//From here we can calculate indents compared to last line
		if(indent > indents.peek()){
			indents.push(indent);
			curLineTokens.add(new Token(indentToken, curLineNum()));
		}
		while(indent < indents.peek()){
			indents.pop();
			curLineTokens.add(new Token(dedentToken, curLineNum()));
		}
		if(indent != indents.peek()){
			scannerError("Indentation error!");
		}												// See algorithm 3.9, p.39

			//FIND THE DIFFERENT TOKENS - FROM HERE
        String word = new String();
        String symbol = new String();
        int i = 0;
        while (i < line.length()){
            char charInWord = line.charAt(i);
            if (isSpace(charInWord)){						//Checks if the current character is a space
                i++;

            }else if(charInWord == '#'){					//Checks if the current character is a '#', indicating this is a comment from here
				i = line.length();

			}else if (isLetterAZ(charInWord)){				//Checks if the current character is a letter
				int n = i;
				boolean added = false;
				while(n < line.length() && (isLetterAZ(line.charAt(n)) || isDigit(line.charAt(n)))){	//Finds the end of the word
                    word = word + line.charAt(n);
                    n++;
                }
                for (TokenKind tk: EnumSet.range(andToken,yieldToken)) {				//Checks if the word is a token between and-yield in enum class
            	    if (word.equals(tk.image)) {
                		curLineTokens.add(new Token(tk ,curLineNum()));
                        word = "";
                        i = n;
						added = true;
						break;												//Breaks the for loop and continues on the main loop when/if we find the token
                    }
            	}
				if(!added){
					Token tk = new Token(nameToken, curLineNum());					   //If no token is added thus far, we have a nameToken
					tk.name = word;
					curLineTokens.add(tk);
					word="";
					i = n;															   //Update the main loops counter
				}

    			} else if (isDigit(charInWord)){					//Checks if the current character is a digit
				int n = i;
				String number = "";
				while (n < line.length() && (isDigit(line.charAt(n)) || line.charAt(n) == '.')){		//Checks if the number has multiple digits
					number = number + line.charAt(n);
					if(number.startsWith("0")){								//If the number starts with 0, this should be read alone
						n++;
						break;
					}
					n++;
				}
				if (number.contains(".")){									//If the number contains a '.' we know it's a float - adds a floatToken
					Token tk = new Token(floatToken, curLineNum());
					tk.floatLit = Double.parseDouble(number);
					curLineTokens.add(tk);
					number = "";
				}else{														//If not, we add a integerToken
					Token tk = new Token(integerToken, curLineNum());
					if(number.equals("0")){									//Checks if the number is 0, indicating it was a starting 0
						tk.integerLit = 0;
						curLineTokens.add(tk);
						number = "";
					}else{													//All other numbers are added using parseint on the number string
						tk.integerLit = Integer.parseInt(number);
						curLineTokens.add(tk);
						number = "";
					}
				}
                i = n;														//Update the main loop counter

			} else if(isBeginString(charInWord)){								//Checks if the current character is the beginning of a string ("/')
				int n = i+1;
				String tmp = new String();
				while(n < line.length() && line.charAt(n) != charInWord){		//Finds the entire string, from first "/' to last
					tmp = tmp + line.charAt(n);
					n++;
				}
				if(n == line.length()){											//If no terminating "/' is found, we have an error
					scannerError("String literal not terminated!");
				}else{															//If it's terminated, we add the string as a stringToken
					Token tk = new Token(stringToken, curLineNum());
					tk.stringLit = tmp;
					curLineTokens.add(tk);
					i = n+1;													//Update the main loop counter - +1 to continue scanning after the terminating "/'
				}

			} else if (isSpecialSymbol(charInWord)){							//Checks if the current character is a special symbol (!=/<>)
				if (i < line.length() - 1 && checkDoubleSym(line.charAt(i+1))){		//Checks if the next character is to be understood as a token with the first
					word = word + charInWord + line.charAt(i+1);
					for (TokenKind tk: EnumSet.range(astToken, semicolonToken)) {
						if (word.equals(tk.image)) {
							curLineTokens.add(new Token(tk ,curLineNum()));
							word = "";
							break;												//Breaks the for-loop when the right token is found
						}
					}
					i = i + 2;
				} else {														//If not, adds the character as one single symbol token
					symbol = symbol + charInWord;
					for (TokenKind tk: EnumSet.range(astToken, semicolonToken)) {
						if (symbol.equals(tk.image)) {
							curLineTokens.add(new Token(tk, curLineNum()));
							symbol = "";
							break;							  					//Breaks the for-loop when the right token is found
						}
					}
					i++;
				}

			} else {															//If none of the above, we know we have a symbol
                symbol = symbol + charInWord;
                for (TokenKind tk: EnumSet.range(astToken, semicolonToken)) {
                    if (symbol.equals(tk.image)) {
                        curLineTokens.add(new Token(tk, curLineNum()));
                        symbol = "";
                    }
                }
                i++;															//Update the main loop counter
			}
        }
			//TO HERE

		// Terminate line:
		curLineTokens.add(new Token(newLineToken,curLineNum()));
	}

	for (Token t: curLineTokens)
		Main.log.noteToken(t);
	}

	public int curLineNum() {
		return sourceFile!=null ? sourceFile.getLineNumber() : 0;
	}

	private int findIndent(String s) {
		int indent = 0;
		while (indent<s.length() && s.charAt(indent)==' ') indent++;
		return indent;
    }

    private String expandLeadingTabs(String s) {						//Function to expand leading tabs to right amount of spaces
		//-- Must be changed in part 1:		- 
		int c = 0;
		int blankCounter = 0;
		while (c < s.length()) {
			if (s.charAt(c) != '\t' && s.charAt(c) != ' ') {
				break;												 	// Ends loop if a non-blank character is found
			} else if (s.charAt(c) == '\t') {
				blankCounter = blankCounter + (4 - (blankCounter % 4)); // Adds the appropriate number of blanks equal
																		// to a tab
				c++;
			} else if (s.charAt(c) == ' ') {
				blankCounter++; 										// Adds a blank to the counter
				c++;
			}
		}
		char[] sWoBlanks = new char[s.length() - c]; 					// Makes a char array for the sentence without beginning spaces
		char[] blanks = new char[blankCounter];							//Makes a char array for all the blanks at beginning of the string

		for (int n = 0; n < s.length() - c; n++) {
			sWoBlanks[n] = s.charAt(c + n);
		} 																// fills sWoBlanks with all the letters - not including the beginning spaces

		for (int n = 0; n < blankCounter; n++) {
			blanks[n] = ' ';
		} 																// fills blanks with all the spaces - tabs changed to right number of spaces

		String noBlanks = new String(sWoBlanks);						//Make a new string, containing no spaces - last part of the string
		String newS = new String(blanks);								//Make a second new string, containing all the spaces - first part of the string
		s = newS.concat(noBlanks);										//Change the input string to reflect the change in spaces
		return s;
    }


    private boolean isLetterAZ(char c) {
	return ('A'<=c && c<='Z') || ('a'<=c && c<='z') || (c=='_');
    }


    private boolean isDigit(char c) {
	return '0'<=c && c<='9';
    }


    public boolean isCompOpr() {
		TokenKind k = curToken().kind;
		List<TokenKind> compOprTokens = Arrays.asList(lessToken, greaterToken,
		doubleEqualToken, greaterEqualToken, lessEqualToken , notEqualToken);

		for (TokenKind compOprToken : compOprTokens){
			if (compOprToken == k ) return true;
		}
		return false;
    }


    public boolean isFactorPrefix() {
		TokenKind k = curToken().kind;
		if(k == plusToken || k == minusToken) return true;
		return false;
    }


    public boolean isFactorOpr() {
		TokenKind k = curToken().kind;
		List<TokenKind> factorOprTokens = Arrays.asList(astToken,
					slashToken, percentToken, doubleSlashToken);
		for(TokenKind factOprTok : factorOprTokens){
			if(k == factOprTok) return true;
		}
		return false;
    }


    public boolean isTermOpr() {
        TokenKind k = curToken().kind;
    	if (k == plusToken || k == minusToken){
            return true;
        }
    	return false;
    }


    public boolean anyEqualToken() {
	for (Token t: curLineTokens) {
	    if (t.kind == equalToken) return true;
	    if (t.kind == semicolonToken) return false;
	}
	return false;
    }




	//METHODS MADE BY US - NOT IN THE PRE CODE
	private boolean isLineEmpty(String line) {
		int blankcounter = 0;
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == '\t' || line.charAt(i) == ' ' || line.charAt(i) == '\n') {
				blankcounter++;
			} else if (line.charAt(i) == '#') {
				blankcounter = 0;
				for (int n = 0; n < i; n++) {
					if (line.charAt(n) == '\t' || line.charAt(n) == ' ' || line.charAt(n) == '\n') {
						blankcounter++;
					}
				}
				if (blankcounter == i) {
					return true;
				}
			}
		}
		if (blankcounter == line.length()) {
			return true;
		}
		return false;
	}

    private boolean checkDoubleSym(char c){
         if(c == '='){
             return true;
         }else if(c == '/'){
             return true;
         }else{
             return false;
         }
    }

	private boolean isBeginString(char c){
		if(c == ('\'')){
			return true;
		}else if(c == '"'){
			return true;
		}
		return false;
	}

	private boolean isSpecialSymbol(char c){
		 if (c == '=' || c == '/' || c == '>' || c == '<' || c == '!'){
			 return true;
		 }else{
			 return false;
		 }
	}

	private boolean isSpace(char c){
		if(c == ' '){
			return true;
		}else if(c == '\t'){
			return true;
		}else{
			return false;
		}
	}
}
