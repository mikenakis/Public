package mikenakis.kit.io.stream.text;

public interface TextStreamWriter
{
	void write( String text );

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	void writeLine( String text );

	interface Defaults extends TextStreamWriter
	{
		@Override default void writeLine( String text )
		{
			write( text );
			write( "\n" );
		}
	}
}
