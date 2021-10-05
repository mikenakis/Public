package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.Annotation;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

public final class ParameterAnnotationSet
{
	public static ParameterAnnotationSet of()
	{
		return of( new ArrayList<>() );
	}

	public static ParameterAnnotationSet of( List<Annotation> annotations )
	{
		return new ParameterAnnotationSet( annotations );
	}

	public final List<Annotation> annotations;

	private ParameterAnnotationSet( List<Annotation> annotations )
	{
		this.annotations = annotations;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString() { return annotations.size() + " annotations"; }
}
