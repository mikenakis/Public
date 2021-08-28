package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.CodeAttribute;
import mikenakis.bytecode.attributes.code.Instruction;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.twig.Twig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link CodeAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class CodeAttributePrinter extends AttributePrinter
{
	private final CodeAttribute codeAttribute;

	public CodeAttributePrinter( CodeAttribute codeAttribute )
	{
		this.codeAttribute = codeAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		codeAttribute.toStringBuilder( builder );
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		appendGildedTo( renderingContext, builder );
	}

	@Override public List<Twig> getTwigChildren( RenderingContext renderingContext )
	{
		return List.of(
			Twig.of( "instructions (" + codeAttribute.instructions.size() + " entries)", getInstructionTwigs( renderingContext ) ),
			Twig.of( "exceptionInfos (" + codeAttribute.exceptionInfos.size() + " entries)",
				codeAttribute.exceptionInfos.stream().map( e -> RenderingContext.newPrinter( e ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ),
			RenderingContext.newPrinter( codeAttribute.attributes ).toTwig( renderingContext, "attributes" ) );
	}

	private List<Twig> getInstructionTwigs( RenderingContext renderingContext )
	{
		List<Twig> twigs = new ArrayList<>();
		for( Instruction instruction : codeAttribute.instructions )
		{
			Optional<RenderingContext.Data> data = renderingContext.tryGetData( instruction );
			if( data.isPresent() )
			{
				var builder = new StringBuilder();
				builder.append( data.get().label );
				builder.append( ':' );
				data.get().source.ifPresent( s -> builder.append( " // " ).append( s ) );
				twigs.add( Twig.of( builder.toString() ) );
			}
			twigs.add( RenderingContext.newPrinter( instruction ).toTwig( renderingContext, "" ) );
		}
		return twigs;
	}
}
