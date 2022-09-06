package io.github.mikenakis.testana.testplan.intent;

import io.github.mikenakis.kit.Kit;

import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

/**
 * Signifies that a test class should run because one or more of its dependencies were modified since its last test run.
 *
 * @author michael.gr
 */
public final class RunBecauseDependenciesModifiedIntent extends RunIntent
{
	public static class Entry
	{
		public final String className;
		public final Instant modifiedTime;

		public Entry( String className, Instant modifiedTime )
		{
			this.className = className;
			this.modifiedTime = modifiedTime;
		}

		@Deprecated @Override public boolean equals( Object other )
		{
			return other instanceof Entry kin && equals( kin );
		}

		@Override public int hashCode()
		{
			return Objects.hash( className, modifiedTime );
		}

		public boolean equals( Entry other )
		{
			if( !className.equals( other.className ) )
				return false;
			if( !modifiedTime.equals( other.modifiedTime ) )
				return false;
			return true;
		}

		@Override public String toString()
		{
			return className + " " + Kit.time.localTimeString( modifiedTime );
		}
	}

	public final Instant lastTestRunTime;
	public final Collection<Entry> entries;

	public RunBecauseDependenciesModifiedIntent( Instant lastTestRunTime, Collection<Entry> entries )
	{
		this.lastTestRunTime = lastTestRunTime;
		assert entries.stream().allMatch( e -> e.modifiedTime.isAfter( lastTestRunTime ) );
		this.entries = entries;
	}

	@Override public String getMessage()
	{
		return "must run because it has dependencies that were modified since its last test run. (Last run: " + Kit.time.localTimeString( lastTestRunTime ) + "; dependencies: " + stringFromDependencies() + ")";
	}

	@Override public boolean equals( Intent other )
	{
		if( other instanceof RunBecauseDependenciesModifiedIntent )
			return equalsIntention( (RunBecauseDependenciesModifiedIntent)other );
		return false;
	}

	public boolean equalsIntention( RunBecauseDependenciesModifiedIntent other )
	{
		if( !Objects.equals( lastTestRunTime, other.lastTestRunTime ) )
			return false;
		if( !Objects.equals( entries, other.entries ) )
			return false;
		return true;
	}

	private String stringFromDependencies()
	{
		return Kit.string.make( ", ", entries );
	}
}
