package mikenakis.testana.kit.textTree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class Branch implements AutoCloseable
{
	private static final class Group<T>
	{
		private final Branch branch;
		private final Collection<T> items;
		private final Function<T,String> stringizer;
		private final BiConsumer<TextTree,T> breeder;

		Group( Branch branch, Collection<T> items, Function<T,String> stringizer, BiConsumer<TextTree,T> breeder )
		{
			this.branch = branch;
			this.items = items;
			this.stringizer = stringizer;
			this.breeder = breeder;
		}

		void print( boolean parentHasNext )
		{
			for( Iterator<? extends T> iterator = items.iterator(); iterator.hasNext(); )
			{
				T item = iterator.next();
				boolean hasNext = parentHasNext || iterator.hasNext();
				String header = stringizer.apply( item );
				branch.textTree.print( item, hasNext, header, breeder );
			}
		}
	}

	private final TextTree textTree;
	private final List<Group<?>> groups = new ArrayList<>();

	public Branch( TextTree textTree )
	{
		this.textTree = textTree;
	}

	public <T> void add( Collection<T> items, Function<T,String> stringizer, BiConsumer<TextTree,T> breeder )
	{
		if( items.isEmpty() )
			return;
		Group<T> group = new Group<>( this, items, stringizer, breeder );
		groups.add( group );
	}

	public <T> void add( Collection<T> items, Function<T,String> stringizer )
	{
		if( items.isEmpty() )
			return;
		add( items, stringizer, null );
	}

	@Override public void close()
	{
		for( Iterator<Group<?>> iterator = groups.iterator(); iterator.hasNext(); )
		{
			Group<?> group = iterator.next();
			boolean hasNext = iterator.hasNext();
			group.print( hasNext );
		}
	}
}
