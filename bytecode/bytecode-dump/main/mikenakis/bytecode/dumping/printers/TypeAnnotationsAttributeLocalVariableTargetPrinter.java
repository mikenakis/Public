package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.TypeAnnotationsAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link TypeAnnotationsAttribute.LocalVariableTarget} {@link TypeAnnotationsAttributeTargetPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class TypeAnnotationsAttributeLocalVariableTargetPrinter extends TypeAnnotationsAttributeTargetPrinter
{
	private final TypeAnnotationsAttribute.LocalVariableTarget localVariableTarget;

	public TypeAnnotationsAttributeLocalVariableTargetPrinter( TypeAnnotationsAttribute.LocalVariableTarget localVariableTarget )
	{
		super( localVariableTarget );
		this.localVariableTarget = localVariableTarget;
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return localVariableTarget.entries.stream().map( e -> renderingContext.newPrinter( e ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() );
	}
}
