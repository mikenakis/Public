package mikenakis.testana.testplan.dependency;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

public class DependencyMatrix<T>
{
	private final SquareMap<T,T,Integer> matrix = new SquareMap<>();

	public DependencyMatrix( Collection<T> domain, Function<T,Collection<T>> getDependencies )
	{
		for( T dependent : domain )
			populateRecursively( matrix, dependent, getDependencies.apply( dependent ), 1, getDependencies );
	}

	public Optional<Integer> getDistance( T dependent, T dependency )
	{
		return matrix.tryGet( dependent, dependency );
	}

	private static <T> void populateRecursively( SquareMap<T,T,Integer> matrix, T dependent, Iterable<T> dependencies, int depth, //
		Function<T,Collection<T>> getDependencies )
	{
		boolean changed = false;
		for( T dependency : dependencies )
			if( matrix.addOrRecompute( dependent, dependency, depth, v -> Math.max( depth, v ) ) )
				changed = true;
		if( !changed )
			return;
		for( T dependency : dependencies )
			populateRecursively( matrix, dependent, getDependencies.apply( dependency ), depth + 1, getDependencies );
	}
}
