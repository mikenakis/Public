package mikenakis.bytecode.model.attributes;

import mikenakis.bytecode.model.ByteCodeAnnotation;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ParameterAnnotationSet
{
	public static ParameterAnnotationSet of()
	{
		return of( new ArrayList<>() );
	}

	public static ParameterAnnotationSet of( List<ByteCodeAnnotation> annotations )
	{
		return new ParameterAnnotationSet( annotations );
	}

	private final List<ByteCodeAnnotation> annotations;

	private ParameterAnnotationSet( List<ByteCodeAnnotation> annotations )
	{
		this.annotations = annotations;
	}

	public List<ByteCodeAnnotation> annotations()
	{
		return Collections.unmodifiableList( annotations );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return annotations.size() + " annotations";
	}
}
