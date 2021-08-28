package mikenakis.kit.spooling2.s0.format.json.enspooling;

import mikenakis.kit.Kit;
import mikenakis.kit.io.stream.text.TextStreamWriter;
import mikenakis.kit.lifetime.Closeable;
import mikenakis.kit.lifetime.guard.LifeGuard;

/**
 * JSON Emitter.
 *
 * @author Michael Belivanakis (michael.gr)
 */
class JsonEmitter implements Closeable.Defaults
{
	private final LifeGuard lifeGuard = LifeGuard.create( this );
	private final TextStreamWriter textStreamWriter;
	private final boolean unquotedIdentifiers;
	private final boolean prettyPrint;
	private int depth;
	private boolean started;

	JsonEmitter( TextStreamWriter textStreamWriter, boolean prettyPrint, boolean unquotedIdentifiers )
	{
		this.textStreamWriter = textStreamWriter;
		this.prettyPrint = prettyPrint;
		this.unquotedIdentifiers = unquotedIdentifiers;
	}

	public int depth()
	{
		return depth;
	}

	private void emitIndentation()
	{
		if( prettyPrint )
		{
			if( !started )
				started = true;
			else
				write( "\n" );
			for( int i = 0; i < depth; i++ )
				write( "\t" );
		}
	}

	@Override public boolean lifeStateAssertion( boolean value )
	{
		return lifeGuard.lifeStateAssertion( value );
	}

	@Override public void close()
	{
		assert isAliveAssertion();
		if( started )
			write( "\n" );
		lifeGuard.close();
	}

	private void write( String content )
	{
		Kit.unchecked( () -> textStreamWriter.write( content ) );
	}

	public void emitObjectBegin()
	{
		assert isAliveAssertion();
		emitIndentation();
		write( "{" );
		depth++;
	}

	public void emitObjectEnd()
	{
		assert isAliveAssertion();
		depth--;
		emitIndentation();
		write( "}" );
	}

	public void emitArrayBegin()
	{
		assert isAliveAssertion();
		emitIndentation();
		write( "[" );
		depth++;
	}

	public void emitArrayEnd()
	{
		assert isAliveAssertion();
		depth--;
		emitIndentation();
		write( "]" );
	}

	public void emitNull( boolean arrayIndentationMode )
	{
		assert isAliveAssertion();
		if( arrayIndentationMode )
			emitIndentation();
		write( "null" );
	}

	public void emitString( String content, boolean arrayIndentationMode )
	{
		assert isAliveAssertion();
		if( arrayIndentationMode )
			emitIndentation();
		var builder = new StringBuilder();
		builder.append( "\"" );
		for( char c : content.toCharArray() )
		{
			switch( c )
			{
				case 8:
					builder.append( "\\b" );
					break;
				case 9:
					builder.append( "\\t" );
					break;
				case 12:
					builder.append( "\\f" );
					break;
				case 13:
					builder.append( "\\r" );
					break;
				case 10:
					builder.append( "\\n" );
					break;
				case '\\':
					builder.append( "\\\\" );
					break;
				case '\"':
					builder.append( "\\\"" );
					break;
				default:
					if( c < 32 )
					{
						builder.append( "\\x" );
						builder.append( digitFromNibble( c >> 4 ) );
						builder.append( digitFromNibble( c & 0x0f ) );
					}
					else
						builder.append( c );
			}
		}
		builder.append( "\"" );
		write( builder.toString() );
	}

	private static char digitFromNibble( int nibble )
	{
		assert nibble >= 0 && nibble < 16;
		if( nibble >= 10 )
			return (char)('a' + nibble - 10);
		return (char)('0' + nibble);
	}

	public void emitLiteral( String content, boolean arrayIndentationMode )
	{
		assert isAliveAssertion();
		if( arrayIndentationMode )
			emitIndentation();
		write( content );
	}

	public void emitIdentifier( String content )
	{
		assert isAliveAssertion();
		assert !unquotedIdentifiers || isValidIdentifier( content );
		emitIndentation();
		if( !unquotedIdentifiers )
			write( "\"" );
		write( content );
		if( !unquotedIdentifiers )
			write( "\"" );
	}

	public void emitColon()
	{
		assert isAliveAssertion();
		write( ":" );
		if( prettyPrint )
			write( " " );
	}

	public void emitComma()
	{
		assert isAliveAssertion();
		write( "," );
	}

	private static boolean isValidIdentifier( String content )
	{
		int n = content.length();
		if( n < 1 )
			return false;
		if( !isValidIdentifierFirst( content.charAt( 0 ) ) )
			return false;
		for( int i = 1;  i < n;  i++ )
			if( !isValidIdentifierNext( content.charAt( i ) ) )
				return false;
		return true;
	}

	@SuppressWarnings( "CharacterComparison" ) private static boolean isValidIdentifierFirst( char c )
	{
		return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
	}

	@SuppressWarnings( "CharacterComparison" ) private static boolean isValidIdentifierNext( char c )
	{
		return isValidIdentifierFirst(c ) || (c >= '0' && c <= '9');
	}

	@Override public String toString()
	{
		return "depth:" + depth;
	}
}
