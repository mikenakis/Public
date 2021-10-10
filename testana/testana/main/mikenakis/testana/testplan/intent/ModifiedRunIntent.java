package mikenakis.testana.testplan.intent;

import mikenakis.kit.Kit;

import java.time.Instant;
import java.util.Objects;

/**
 * Signifies that a test Class should run because it was modified since its last test run.
 *
 * @author michael.gr
 */
public final class ModifiedRunIntent extends RunIntent
{
	public final Instant lastTestRunTime;
	public final Instant modifiedTime;

	public ModifiedRunIntent( Instant lastTestRunTime, Instant modifiedTime )
	{
		assert modifiedTime.isAfter( lastTestRunTime );
		this.lastTestRunTime = lastTestRunTime;
		this.modifiedTime = modifiedTime;
	}

	@Override public String getMessage()
	{
		return "must run because it has been modified since last run. (Last run: " + Kit.time.localTimeString( lastTestRunTime ) + "; modified: " + Kit.time.localTimeString( modifiedTime ) + ")";
	}

	@Override public boolean equals( Intent other )
	{
		if( other instanceof ModifiedRunIntent )
			return equalsIntention( (ModifiedRunIntent)other );
		return false;
	}

	public boolean equalsIntention( ModifiedRunIntent other )
	{
		if( !Objects.equals( lastTestRunTime, other.lastTestRunTime ) )
			return false;
		if( !Objects.equals( modifiedTime, other.modifiedTime ) )
			return false;
		return true;
	}
}
