package mikenakis.bytecode.test.model;

//Originally from https://stackoverflow.com/a/26227947/773113
@SuppressWarnings( "unused" )
public class Class9WithCode
{
	@SuppressWarnings( "unused" ) public static double eval( String str )
	{
		Class9WithCode parser = new Class9WithCode( str );
		return parser.parse();
	}

	private final String str;
	private int pos = -1;
	private int ch;

	public Class9WithCode( String str )
	{
		this.str = str;
	}

	private void nextChar()
	{
		ch = (++pos < str.length()) ? str.charAt( pos ) : -1;
	}

	private boolean eat( int charToEat )
	{
		//noinspection WhileLoopSpinsOnField
		while( ch == ' ' )
			nextChar();
		if( ch == charToEat )
		{
			nextChar();
			return true;
		}
		return false;
	}

	public double parse()
	{
		nextChar();
		double x = parseExpression();
		if( pos < str.length() )
			throw new RuntimeException( "Unexpected: " + (char)ch );
		return x;
	}

	// Grammar:
	// expression = term | expression `+` term | expression `-` term
	// term = factor | term `*` factor | term `/` factor
	// factor = `+` factor | `-` factor | `(` expression `)`
	//        | number | functionName factor | factor `^` factor

	private double parseExpression()
	{
		double x = parseTerm();
		for( ; ; )
		{
			if( eat( '+' ) )
				x += parseTerm(); // addition
			else if( eat( '-' ) )
				x -= parseTerm(); // subtraction
			else
				return x;
		}
	}

	private double parseTerm()
	{
		double x = parseFactor();
		for( ; ; )
		{
			if( eat( '*' ) )
				x *= parseFactor(); // multiplication
			else if( eat( '/' ) )
				x /= parseFactor(); // division
			else
				return x;
		}
	}

	private double parseFactor()
	{
		if( eat( '+' ) )
			return parseFactor(); // unary plus
		if( eat( '-' ) )
			return -parseFactor(); // unary minus

		double x;
		int startPos = pos;
		if( eat( '(' ) )
		{
			// parentheses
			x = parseExpression();
			eat( ')' );
		}
		else if( (ch >= '0' && ch <= '9') || ch == '.' )
		{
			// numbers
			while( (ch >= '0' && ch <= '9') || ch == '.' )
				nextChar();
			x = Double.parseDouble( str.substring( startPos, pos ) );
		}
		else if( ch >= 'a' && ch <= 'z' )
		{
			// functions
			while( ch >= 'a' && ch <= 'z' )
				nextChar();
			String func = str.substring( startPos, pos );
			x = parseFactor();
			x = switch( func )
				{
					case "sqrt" -> Math.sqrt( x );
					case "sin" -> Math.sin( x );
					case "cos" -> Math.cos( x );
					case "tan" -> Math.tan( x );
					case "rad" -> Math.toRadians( x );
					case "deg" -> Math.toDegrees( x );
					default -> throw new RuntimeException( "Unknown function: " + func );
				};
		}
		else
		{
			throw new RuntimeException( "Unexpected: " + (char)ch );
		}

		if( eat( '^' ) )
			x = Math.pow( x, parseFactor() ); // exponentiation

		return x;
	}
}
