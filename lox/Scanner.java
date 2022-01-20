import java.util.ArrayList;
import java.util.List;


class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        
        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            // one-char lexemes
            case '(':
                addToken(TokenType.LEFT_PAREN);
                break;
            case ')':
                addToken(TokenType.RIGHT_PAREN);
                break;
            case '{':
                addToken(TokenType.LEFT_BRACE);
                break;
            case '}':
                addToken(TokenType.RIGHT_BRACE);
                break;
            case ',':
                addToken(TokenType.COMMA);
                break;
            case '.':
                addToken(TokenType.DOT);
                break;
            case '-':
                addToken(TokenType.MINUS);
                break;
            case '+':
                addToken(TokenType.PLUS);
                break;
            case ';':
                addToken(TokenType.SEMICOLON);
                break;
            case '*':
                addToken(TokenType.STAR);
                break;

            // one/two-char lexemes
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '>':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            
            // one/many-char lexemes
            case '/':
                if (match('/')) {
                    // skip all until end of comment (newline)
                    while (peek() != '\n' && !isAtEnd()) {
                        advance();
                    }
                } else {
                    addToken(TokenType.SLASH);
                }
                break;
            
            // whitespace
            case ' ':
            case '\r':
            case '\t':
                break;  // ignore
            case '\n':
                line++; // newline
                break;
            
            // literals
            case '"':
                string();  // strings
                break;
            
            default:
                if (isDigit(c)) {
                    number();   // numbers
                
                } else {
                    // catch unexpected characters
                    Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    private void number() {
        while ( isDigit(peek()) ) {
            advance();
        }
        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();
            while (isDigit(peek()))  {
                advance();
            }
        }
        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(current + 1);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') {
                line++;
            }
            advance();
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }
        // The closing "
        advance();
        // Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, value);
    }

    // if this char could be the start of a one-or-two char lexeme, determines which is the case
    // assumes only one possible complimentary (second) character per first character
    private boolean match(char compliment) {
        if (isAtEnd()) {
            return false;
        }

        if (source.charAt(current) != compliment) {     // if the next char isn't the potential complement
            return false;
        }
        
        current++;  // don't double-count second char of lexeme/token
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }
}