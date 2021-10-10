package mikenakis.testana.kit.structured.json;

import mikenakis.kit.Kit;

import java.io.BufferedReader;
import java.io.Closeable;
import java.util.Arrays;

/**
 * JSON Parser.
 *
 * @author michael.gr
 */
public class JsonParser implements Closeable
{
	public enum TokenType
	{
		ObjectBegin, /* { */
		ObjectEnd,   /* } */
		ArrayBegin,  /* [ */
		ArrayEnd,    /* ] */
		Comma,       /* , */
		Colon,       /* : */
		Number,
		String,
		Boolean,
		Null,
		Identifier
	}

	private final BufferedReader reader;
	private String currentLine = "";
	private int currentOffset;
	private boolean finished;
	private boolean closed;
	private final boolean unquotedIdentifiers;
	private TokenType currentTokenType;
	private String currentTokenContent;
	private int depth;

	public JsonParser( BufferedReader reader, boolean unquotedIdentifiers )
	{
		this.reader = reader;
		this.unquotedIdentifiers = unquotedIdentifiers;
		loadNextLine();
		nextToken();
	}

	public int depth()
	{
		return depth;
	}

	@Override public void close()
	{
		assert !closed;
		closed = true;
	}

	public boolean isObjectBegin()
	{
		assert !closed;
		return currentTokenType == TokenType.ObjectBegin;
	}

	public boolean isObjectEnd()
	{
		assert !closed;
		return currentTokenType == TokenType.ObjectEnd;
	}

	public boolean isArrayBegin()
	{
		assert !closed;
		return currentTokenType == TokenType.ArrayBegin;
	}

	public boolean isArrayEnd()
	{
		assert !closed;
		return currentTokenType == TokenType.ArrayEnd;
	}

	public boolean isNull()
	{
		assert !closed;
		return currentTokenType == TokenType.Null;
	}

	public boolean isBoolean()
	{
		assert !closed;
		return currentTokenType == TokenType.Boolean;
	}

	public boolean isNumber()
	{
		assert !closed;
		return currentTokenType == TokenType.Number;
	}

	public boolean isString()
	{
		assert !closed;
		return currentTokenType == TokenType.String;
	}

	public boolean isIdentifier()
	{
		assert !closed;
		return currentTokenType == TokenType.String || (unquotedIdentifiers && currentTokenType == TokenType.Identifier);
	}

	public boolean isColon()
	{
		assert !closed;
		return currentTokenType == TokenType.Colon;
	}

	public boolean isComma()
	{
		assert !closed;
		return currentTokenType == TokenType.Comma;
	}

	public String skip( TokenType tokenType )
	{
		assert !closed;
		assert currentTokenType != null;
		assert currentTokenType == tokenType;
		String result = currentTokenContent;
		switch( currentTokenType )
		{
			case ObjectBegin:
			case ArrayBegin:
				depth++;
				break;
			case ObjectEnd:
			case ArrayEnd:
				depth--;
				break;
			default:
				break;
		}
		nextToken();
		return result;
	}

	public boolean isFinished()
	{
		assert !closed;
		return currentTokenType == null;
	}

	private enum CharacterClass
	{
		Whitespace,
		Identifier,
		Number,
		ObjectBegin,
		ObjectEnd,
		ArrayBegin,
		ArrayEnd,
		Comma,
		Colon,
		QuotedContent,
		CStyleComment,
		YamlStyleComment,
		Unexpected
	}

	private static final CharacterClass[] characterClassTable = buildCharacterClassTable();

	private static CharacterClass[] buildCharacterClassTable()
	{
		CharacterClass[] characterClasses = new CharacterClass[127];
		Arrays.fill( characterClasses, CharacterClass.Unexpected );
		characterClasses[' '] = CharacterClass.Whitespace;
		characterClasses['\t'] = CharacterClass.Whitespace;
		characterClasses['\r'] = CharacterClass.Whitespace;
		characterClasses['\n'] = CharacterClass.Whitespace;
		for( char c = 'a';  c <= 'z';  c++ )
			characterClasses[c] = CharacterClass.Identifier;
		for( char c = 'A';  c <= 'Z';  c++ )
			characterClasses[c] = CharacterClass.Identifier;
		characterClasses['$'] = CharacterClass.Identifier;
		characterClasses['_'] = CharacterClass.Identifier;
		characterClasses['.'] = CharacterClass.Number;
		characterClasses['+'] = CharacterClass.Number;
		characterClasses['-'] = CharacterClass.Number;
		for( char c = '0'; c <= '9'; c++ )
			characterClasses[c] = CharacterClass.Number;
		characterClasses['{'] = CharacterClass.ObjectBegin;
		characterClasses['}'] = CharacterClass.ObjectEnd;
		characterClasses['['] = CharacterClass.ArrayBegin;
		characterClasses[']'] = CharacterClass.ArrayEnd;
		characterClasses[','] = CharacterClass.Comma;
		characterClasses[':'] = CharacterClass.Colon;
		characterClasses['"'] = CharacterClass.QuotedContent;
		characterClasses['\''] = CharacterClass.QuotedContent;
		characterClasses['/'] = CharacterClass.CStyleComment;
		characterClasses['#'] = CharacterClass.YamlStyleComment;
		return characterClasses;
	}

	private void nextToken()
	{
		for( ; ; )
		{
			if( finished )
			{
				currentTokenType = null;
				currentTokenContent = null;
				return;
			}
			char c = readChar();
			CharacterClass characterClass = c >= characterClassTable.length? CharacterClass.Unexpected : characterClassTable[c];
			switch( characterClass )
			{
				case Whitespace:
					break;
				case Identifier:
				{
					var builder = new StringBuilder();
					builder.append( c );
					for( ; ; )
					{
						c = peekChar();
						//noinspection CharacterComparison
						if( (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '$' || c == '_' )
						{
							builder.append( c );
							skipChar();
							continue;
						}
						break;
					}
					currentTokenContent = builder.toString();
					switch( currentTokenContent )
					{
						case "true":
						case "false":
							currentTokenType = TokenType.Boolean;
							break;
						case "null":
							currentTokenType = TokenType.Null;
							break;
						default:
							currentTokenType = TokenType.Identifier;
							break;
					}
					return;
				}
				case Number:
				{
					var builder = new StringBuilder();
					builder.append( c );
					for( ; ; )
					{
						c = peekChar();
						//noinspection CharacterComparison
						if( (c >= '0' && c <= '9') || c == '+' || c == '-' || c == '.' || c == 'e' || c == 'E' )
						{
							builder.append( c );
							skipChar();
							continue;
						}
						break;
					}
					currentTokenContent = builder.toString();
					currentTokenType = TokenType.Number;
					return;
				}
				case ObjectBegin:
					currentTokenContent = "{";
					currentTokenType = TokenType.ObjectBegin;
					return;
				case ObjectEnd:
					currentTokenContent = "}";
					currentTokenType = TokenType.ObjectEnd;
					return;
				case ArrayBegin:
					currentTokenContent = "[";
					currentTokenType = TokenType.ArrayBegin;
					return;
				case ArrayEnd:
					currentTokenContent = "]";
					currentTokenType = TokenType.ArrayEnd;
					return;
				case Comma:
					currentTokenContent = ",";
					currentTokenType = TokenType.Comma;
					return;
				case Colon:
					currentTokenContent = ":";
					currentTokenType = TokenType.Colon;
					return;
				case QuotedContent:
					currentTokenContent = parseQuotedContent( c );
					currentTokenType = TokenType.String;
					return;
				case CStyleComment:
					parseCStyleComment();
					break;
				case YamlStyleComment:
					parseYamlStyleComment();
					continue;
				case Unexpected:
					throw new JsonParsingException( "unexpected character: \"" + c + "\"" );
			}
		}
	}

	private String parseQuotedContent( char quoteCharacter )
	{
		var builder = new StringBuilder();
		for( ; ; )
		{
			char c = readChar();
			if( c == quoteCharacter )
				break;
			if( c == '\\' )
			{
				c = readChar();
				switch( c )
				{
					case 13:
					case 10:
						continue;
					case 'b':
						c = 8;
						break;
					case 't':
						c = 9;
						break;
					case 'f':
						c = 12;
						break;
					case 'n':
						c = 10;
						break;
					case 'r':
						c = 13;
						break;
					case '\\':
					case '\'':
					case '\"':
						break;
					case 'x':
						c = (char)(readNibble() << 4 | readNibble());
						break;
					case 'u':
						c = (char)(readNibble() << 12 | readNibble() << 8 | readNibble() << 4 | readNibble());
						break;
					default:
						throw new JsonParsingException( "invalid escape: \"\\" + c + "\"" );
				}
			}
			builder.append( c );
		}
		return builder.toString();
	}

	@SuppressWarnings( "CharacterComparison" ) private int readNibble()
	{
		char c = readChar();
		if( c >= '0' && c <= '9' )
			return (char)(c - '0');
		if( c >= 'a' && c <= 'f' )
			return (char)(c - 'a' + 10);
		if( c >= 'A' && c <= 'F' )
			return (char)(c - 'A' + 10);
		throw new JsonParsingException( "unexpected character: \"" + c + "\"" );
	}

	private void parseCStyleComment()
	{
		char c = readChar();
		if( c == '/' )
		{
			for( ; ; )
			{
				c = readChar();
				if( c == '\n' )
					break;
			}
			return;
		}
		if( c == '*' )
		{
			for( boolean haveStar = false; ; )
			{
				c = readChar();
				if( c == '*' )
				{
					haveStar = true;
					continue;
				}
				if( haveStar )
				{
					if( c == '/' )
						break;
					haveStar = false;
				}
			}
			return;
		}
		throw new JsonParsingException( "unexpected token: \"/" + c + "\"" );
	}

	private void parseYamlStyleComment()
	{
		for( ; ; )
		{
			char c = readChar();
			if( c == '\n' )
				break;
		}
	}

	@Override public String toString()
	{
		return finished ? "Finished" : "depth:" + depth + " " + currentTokenType + " \"" + currentTokenContent + "\" Current char: '" + peekChar() + "'";
	}

	private char peekChar()
	{
		assert !finished;
		if( currentOffset == currentLine.length() )
			return '\n';
		return currentLine.charAt( currentOffset );
	}

	private void skipChar()
	{
		assert !finished;
		currentOffset++;
		if( currentOffset > currentLine.length() ) //allow currentOffset == currentLine.length() in order for 'peek' to return '\n' at end-of-line
			loadNextLine();
	}

	private void loadNextLine()
	{
		currentOffset = 0;
		currentLine = Kit.unchecked( () -> reader.readLine() );
		if( currentLine == null )
		{
			finished = true;
			currentLine = "";
		}
	}

	private char readChar()
	{
		assert !finished;
		char result = peekChar();
		skipChar();
		return result;
	}
}
