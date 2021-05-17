package mikenakis.testana.kit.textTree;

import java.util.Iterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Generates trees consisting of lines of text.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TextTree
{
	private static final String[][] PREFIXES = { { " ├─ ", " │  " }, { " └─ ", "    " } };

	private final Consumer<String> emitter;
	private final StringBuilder parentPrefix = new StringBuilder();

	public TextTree( String header, Consumer<String> emitter )
	{
		this.emitter = emitter;
		emitter.accept( header );
	}

	public <T> void print( Iterable<T> items, Function<T,String> stringizer )
	{
		print( items, stringizer, null );
	}

	public <T> void print( Iterable<T> items, Function<T,String> stringizer, BiConsumer<TextTree,T> breeder )
	{
		for( Iterator<? extends T> iterator = items.iterator(); iterator.hasNext(); )
		{
			T item = iterator.next();
			boolean hasNext = iterator.hasNext();
			String header = stringizer.apply( item );
			print( item, hasNext, header, breeder );
		}
	}

	<T> void print( T item, boolean hasNext, String header, BiConsumer<TextTree,T> breeder )
	{
		String[] prefixes = PREFIXES[hasNext ? 0 : 1];
		emitter.accept( parentPrefix + prefixes[0] + header );
		if( breeder != null )
		{
			int length = parentPrefix.length();
			parentPrefix.append( prefixes[1] );
			breeder.accept( this, item );
			parentPrefix.replace( length, parentPrefix.length(), "" );
		}
	}
}
