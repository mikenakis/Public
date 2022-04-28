package mikenakis.immutability.object.assessments.mutable;

import mikenakis.immutability.Assessment;
import mikenakis.immutability.helpers.Stringizer;
import mikenakis.immutability.mykit.MyKit;
import mikenakis.immutability.mykit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.immutability.object.assessments.MutableObjectAssessment;
import mikenakis.immutability.type.ImmutabilitySelfAssessable;
import mikenakis.immutability.type.assessments.provisory.SelfAssessableAssessment;

import java.util.List;

/**
 * Signifies that a self-assessable object has assessed itself as mutable.
 */
public final class MutableSelfAssessment extends MutableObjectAssessment
{
	public final SelfAssessableAssessment typeAssessment;
	public final ImmutabilitySelfAssessable object;

	public MutableSelfAssessment( Stringizer stringizer, SelfAssessableAssessment typeAssessment, //
		ImmutabilitySelfAssessable object )
	{
		super( stringizer, object );
		this.typeAssessment = typeAssessment;
		this.object = object;
	}

	@Override public Iterable<Assessment> children() { return List.of( typeAssessment ); }

	@ExcludeFromJacocoGeneratedReport @Override protected void appendToStringBuilder( StringBuilder stringBuilder )
	{
		stringBuilder.append( "object " );
		if( MyKit.get( false ) )
			stringBuilder.append( stringizer.stringize( object ) ).append( " " );
		stringBuilder.append( "is self-assessable" );
		stringBuilder.append( " because it is of type '" ).append( stringizer.stringize( typeAssessment.type ) ).append( "'" );
	}
}
