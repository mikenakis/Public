package mikenakis.testana.discovery;

import mikenakis.kit.Kit;
import mikenakis.kit.functional.Function1;
import mikenakis.kit.logging.Log;

import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * Represents a module of the project.
 *
 * @author michael.gr
 */
public abstract class DiscoveryModule
{
	private boolean dependenciesResolved; // TODO FIXME restructure things so that it is unnecessary to have this state.

	protected DiscoveryModule()
	{
	}

	protected boolean dependenciesResolved()
	{
		return dependenciesResolved;
	}

	public abstract String name();

	public final void resolveDependencies( Map<String,DiscoveryModule> nameToModuleMap )
	{
		assert !dependenciesResolved;
		dependenciesResolved = true;
		onResolveDependencies( nameToModuleMap );
	}

	protected abstract void onResolveDependencies( Map<String,DiscoveryModule> nameToModuleMap );

	public final boolean detectCycles()
	{
		//Graph<DiscoveryModule> graph = Graph.create( this, module -> module.projectDependencies() );
		Graph<DiscoveryModule> graph = Graph.create( this, module -> module.allProjectDependencies() );
		Collection<DiscoveryModule> cycle = graph.shortestCycle();
		if( cycle != null )
		{
			Log.error( "Cyclic module dependency!" );
			for( var module : cycle )
				Log.error( "    " + module );
			return true;
		}
		return false;
	}

	private static class Graph<V>
	{
		public static <V> Graph<V> create()
		{
			return new Graph<>( new LinkedHashMap<>() );
		}

		public static <V> Graph<V> create( V root, Function1<Iterable<V>,V> breeder )
		{
			Graph<V> graph = create();
			graph.tryAddRecursively( root, breeder );
			return graph;
		}

		private final LinkedHashMap<V,LinkedHashSet<V>> map;

		private Graph( LinkedHashMap<V,LinkedHashSet<V>> map )
		{
			this.map = map;
		}

		public void tryAddRecursively( V root, Function1<Iterable<V>,V> breeder )
		{
			tryAddVertex( root );
			for( var neighbor : breeder.invoke( root ) )
			{
				tryAddNeighbor( root, neighbor );
				tryAddRecursively( neighbor, breeder );
			}
		}

		public Set<V> vertices()
		{
			return map.keySet();
		}

		public Set<V> neighbors( V vertex )
		{
			return Kit.map.get( map, vertex );
		}

		public void addVertex( V vertex )
		{
			Kit.map.add( map, vertex, new LinkedHashSet<>() );
		}

		public boolean tryAddVertex( V vertex )
		{
			return Kit.map.tryAdd( map, vertex, new LinkedHashSet<>() );
		}

		public void addNeighbor( V vertex, V neighbor )
		{
			LinkedHashSet<V> neighbors = Kit.map.get( map, vertex );
			Kit.collection.add( neighbors, neighbor );
		}

		public boolean tryAddNeighbor( V vertex, V neighbor )
		{
			LinkedHashSet<V> neighbors = Kit.map.get( map, vertex );
			return Kit.collection.tryAdd( neighbors, neighbor );
		}

		public void removeNeighbor( V vertex, V neighbor )
		{
			LinkedHashSet<V> neighbors = Kit.map.get( map, vertex );
			Kit.collection.remove( neighbors, neighbor );
		}

		public int size()
		{
			return map.size();
		}

		public Graph<V> reverse()
		{
			Graph<V> reverse = create();
			for( Map.Entry<V,LinkedHashSet<V>> entry : map.entrySet() )
			{
				V vertex = entry.getKey();
				reverse.tryAddVertex( vertex );
				LinkedHashSet<V> neighbors = entry.getValue();
				for( V neighbor : neighbors )
				{
					reverse.tryAddVertex( neighbor );
					reverse.tryAddNeighbor( neighbor, vertex );
				}
			}
			return reverse;
		}

		private static class MetaGraph<V>
		{
			private record Entry<V>( V vertex, int distance ) { }

			private final Map<V,Entry<V>> vertexToEntryMap = new LinkedHashMap<>();

			public MetaGraph( Graph<V> graph, V startVertex )
			{
				Kit.map.add( vertexToEntryMap, startVertex, new Entry<>( startVertex, 0 ) );
				LinkedList<V> queue = new LinkedList<>();
				queue.addLast( startVertex );
				while( !queue.isEmpty() )
				{
					V vertex = queue.removeFirst();
					int distanceToVertex = Kit.map.get( vertexToEntryMap, vertex ).distance;
					for( V neighbor : graph.neighbors( vertex ) )
					{
						if( vertexToEntryMap.containsKey( neighbor ) )
							continue;
						Kit.map.tryAdd( vertexToEntryMap, neighbor, new Entry<>( vertex, distanceToVertex + 1 ) );
						queue.addLast( neighbor );
					}
				}
			}

			public Collection<V> pathTo( V vertex )
			{
				LinkedHashSet<V> path = new LinkedHashSet<>();
				do
				{
					path.add( vertex );
					vertex = Kit.map.get( vertexToEntryMap, vertex ).vertex;
				}
				while( vertex != null );
				return path;
			}

			public int distanceTo( V vertex )
			{
				Entry<V> entry = Kit.map.tryGet( vertexToEntryMap, vertex );
				if( entry == null )
					return Integer.MAX_VALUE;
				return entry.distance;
			}
		}

		public Collection<V> shortestCycle()
		{
			Graph<V> reverse = reverse();
			Collection<V> shortestCycle = null;
			for( V v : map.keySet() )
			{
				MetaGraph<V> metaGraph = new MetaGraph<>( reverse, v );
				for( V w : neighbors( v ) )
					if( shortestCycle == null || metaGraph.distanceTo( w ) < shortestCycle.size() )
						shortestCycle = metaGraph.pathTo( w );
			}
			return shortestCycle;
		}
	}

	public abstract Path sourcePath();

	public abstract Collection<OutputDirectory> outputDirectories();

	public abstract Collection<DiscoveryModule> projectDependencies();

	public abstract Collection<Path> externalDependencyPaths();

	public abstract Collection<DiscoveryModule> nestedModules();

	@Override public abstract String toString();

	public final Collection<Path> allOutputPaths()
	{
		if( Kit.get( true ) )
		{
			Collection<Path> mutablePaths = new LinkedHashSet<>();
			allOutputPathsRecursive( this, mutablePaths );
			return mutablePaths;
		}
		else
		{
			Collection<Path> mutableOutputPaths = new LinkedHashSet<>();
			mutableOutputPaths.addAll( outputPaths() );
			mutableOutputPaths.addAll( externalDependencyPaths() );
			for( DiscoveryModule dependency : allProjectDependencies() )
			{
				mutableOutputPaths.addAll( dependency.outputPaths() );
				mutableOutputPaths.addAll( dependency.externalDependencyPaths() );
			}
			return mutableOutputPaths;
		}
	}

	private static void allOutputPathsRecursive( DiscoveryModule discoveryModule, Collection<Path> mutableOutputPaths )
	{
		mutableOutputPaths.addAll( discoveryModule.outputPaths() );
		mutableOutputPaths.addAll( discoveryModule.externalDependencyPaths() );
		for( DiscoveryModule dependency : discoveryModule.projectDependencies() )
			allOutputPathsRecursive( dependency, mutableOutputPaths );
	}

	public final Collection<Path> outputPaths()
	{
		return outputDirectories().stream().map( o -> o.path ).toList();
	}

	public final Collection<Path> allDependencyAndExternalPaths()
	{
		if( Kit.get( true ) )
		{
			Collection<Path> mutablePaths = new LinkedHashSet<>();
			mutablePaths.addAll( externalDependencyPaths() );
			for( DiscoveryModule dependency : projectDependencies() )
				dependencyAndExternalPathsRecursive( dependency, mutablePaths );
			return mutablePaths;
		}
		else
		{
			Collection<Path> mutablePaths = new LinkedHashSet<>();
			mutablePaths.addAll( externalDependencyPaths() );
			for( DiscoveryModule dependency : allProjectDependencies() )
				mutablePaths.addAll( dependency.externalDependencyPaths() );
			return mutablePaths;
		}
	}

	private static void dependencyAndExternalPathsRecursive( DiscoveryModule discoveryModule, Collection<Path> mutableDependencyPaths )
	{
		mutableDependencyPaths.addAll( discoveryModule.outputPaths() );
		mutableDependencyPaths.addAll( discoveryModule.externalDependencyPaths() );
		for( DiscoveryModule dependency : discoveryModule.projectDependencies() )
			dependencyAndExternalPathsRecursive( dependency, mutableDependencyPaths );
	}

	public final Collection<DiscoveryModule> allProjectDependencies()
	{
		Collection<DiscoveryModule> mutableDependencies = new LinkedHashSet<>();
		collectProjectDependenciesRecursively( mutableDependencies );
		return mutableDependencies;
	}

	private void collectProjectDependenciesRecursively( Collection<DiscoveryModule> mutableDependencies )
	{
		for( DiscoveryModule dependency : projectDependencies() )
			if( mutableDependencies.add( dependency ) )
				dependency.collectProjectDependenciesRecursively( mutableDependencies );
	}
}
