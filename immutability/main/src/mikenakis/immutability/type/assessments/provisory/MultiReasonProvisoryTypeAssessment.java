package mikenakis.immutability.type.assessments.provisory;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.internal.helpers.Helpers;
import mikenakis.immutability.internal.helpers.Stringizer;
import mikenakis.immutability.internal.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.type.assessments.ProvisoryTypeAssessment;

import java.util.List;

/**
 * Signifies that a class is provisory due to multiple reasons.
 */
public final class MultiReasonProvisoryTypeAssessment extends ProvisoryTypeAssessment
{
	public final List<ProvisoryTypeAssessment> provisoryReasons;

	public MultiReasonProvisoryTypeAssessment( Stringizer stringizer, Class<?> jvmClass, List<ProvisoryTypeAssessment> provisoryReasons )
	{
		super( stringizer, jvmClass );
		assert Helpers.isClass( jvmClass ); //the type must be a class; there is a different assessment for interfaces.
		this.provisoryReasons = provisoryReasons;
	}

	@Override public List<? extends Assessment> children()
	{
		return provisoryReasons;
	}

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		super.appendToStringBuilder( stringBuilder );
		stringBuilder.append( " due to multiple reasons" );
	}
}
