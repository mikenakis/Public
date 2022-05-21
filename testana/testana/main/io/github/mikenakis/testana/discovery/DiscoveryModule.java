package io.github.mikenakis.testana.discovery;

import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.functional.Function1;
import io.github.mikenakis.kit.logging.Log;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		Graph<DiscoveryModule> graph = Graph.create( this, module -> module.allProjectDependencies() );
		List<DiscoveryModule> cycle = graph.shortestCycle();
		if( cycle != null )
		{
			Log.error( "Cyclic module dependency!" );
			Kit.tree.print( 0, i -> i + 1 >= cycle.size() ? List.of() : List.of( i + 1 ),  i -> cycle.get( i ).toString(), s -> Log.error( s ) );
			return true;
		}
		return false;
	}

	//This is some experimental graph stuff which I adapted from here: https://algs4.cs.princeton.edu/42digraph/BreadthFirstDirectedPaths.java.html

	private static class Graph<V>
	{
		static <V> Graph<V> create()
		{
			return new Graph<>( new LinkedHashMap<>() );
		}

		static <V> Graph<V> create( V root, Function1<Iterable<V>,V> breeder )
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

		void tryAddRecursively( V root, Function1<Iterable<V>,V> breeder )
		{
			tryAddVertex( root );
			for( var neighbor : breeder.invoke( root ) )
			{
				if( tryAddNeighbor( root, neighbor ) )
					tryAddRecursively( neighbor, breeder );
			}
		}

		Set<V> vertices()
		{
			return map.keySet();
		}

		Set<V> neighbors( V vertex )
		{
			return Kit.map.get( map, vertex );
		}

		void addVertex( V vertex )
		{
			Kit.map.add( map, vertex, new LinkedHashSet<>() );
		}

		boolean tryAddVertex( V vertex )
		{
			if( Kit.map.containsKey( map, vertex ) )
				return false;
			Kit.map.add( map, vertex, new LinkedHashSet<>() );
			return true;
		}

		void addNeighbor( V vertex, V neighbor )
		{
			LinkedHashSet<V> neighbors = Kit.map.get( map, vertex );
			Kit.collection.add( neighbors, neighbor );
		}

		boolean tryAddNeighbor( V vertex, V neighbor )
		{
			LinkedHashSet<V> neighbors = Kit.map.get( map, vertex );
			return Kit.collection.tryAdd( neighbors, neighbor );
		}

		void removeNeighbor( V vertex, V neighbor )
		{
			LinkedHashSet<V> neighbors = Kit.map.get( map, vertex );
			Kit.collection.remove( neighbors, neighbor );
		}

		int size()
		{
			return map.size();
		}

		Graph<V> reverse()
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

			@SuppressWarnings( { "FieldCanBeLocal", "unused" } ) private final V startVertex;
			private final Map<V,Entry<V>> vertexToEntryMap = new LinkedHashMap<>();

			MetaGraph( Graph<V> graph, V startVertex )
			{
				this.startVertex = startVertex;
				Kit.map.add( vertexToEntryMap, startVertex, new Entry<>( startVertex, 0 ) );
				LinkedList<V> queue = new LinkedList<>();
				queue.addLast( startVertex );
				while( !queue.isEmpty() )
				{
					V vertex = queue.removeFirst();
					int nextDistance = Kit.map.get( vertexToEntryMap, vertex ).distance + 1;
					for( V neighbor : graph.neighbors( vertex ) )
					{
						if( vertexToEntryMap.containsKey( neighbor ) )
							continue;
						Kit.map.add( vertexToEntryMap, neighbor, new Entry<>( vertex, nextDistance ) );
						queue.addLast( neighbor );
					}
				}
			}

			List<V> pathTo( V vertex )
			{
				List<V> path = new ArrayList<>();
				for( ; ; )
				{
					assert !Kit.collection.contains( path, vertex );
					Kit.collection.add( path, vertex );
					Entry<V> entry = Kit.map.get( vertexToEntryMap, vertex );
					if( entry.vertex == vertex )
						break;
					vertex = entry.vertex;
				}
				Kit.collection.add( path, vertex );
				return path;
			}

			Optional<Integer> distanceTo( V vertex )
			{
				Entry<V> entry = Kit.map.tryGet( vertexToEntryMap, vertex );
				if( entry == null )
					return Optional.empty();
				return Optional.of( entry.distance );
			}
		}

		List<V> shortestCycle()
		{
			Graph<V> reverse = reverse();
			List<V> shortestCycle = null;
			for( V v : map.keySet() )
			{
				MetaGraph<V> metaGraph = new MetaGraph<>( reverse, v );
				for( V w : neighbors( v ) )
				{
					Optional<Integer> distance = metaGraph.distanceTo( w );
					if( distance.isEmpty() )
						continue;
					if( shortestCycle == null || distance.get() < shortestCycle.size() )
						shortestCycle = metaGraph.pathTo( w );
				}
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
		Collection<Path> mutablePaths = new LinkedHashSet<>();
		dependencyAndExternalPathsRecursive( this, mutablePaths );
		return mutablePaths;
	}

	private static void dependencyAndExternalPathsRecursive( DiscoveryModule discoveryModule, Collection<Path> mutableDependencyPaths )
	{
		mutableDependencyPaths.addAll( discoveryModule.outputPaths() );
		mutableDependencyPaths.addAll( discoveryModule.externalDependencyPaths() );
		for( DiscoveryModule dependencyModule : discoveryModule.projectDependencies() )
			dependencyAndExternalPathsRecursive( dependencyModule, mutableDependencyPaths );
		for( DiscoveryModule nestedModule : discoveryModule.nestedModules() )
			dependencyAndExternalPathsRecursive( nestedModule, mutableDependencyPaths );
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
