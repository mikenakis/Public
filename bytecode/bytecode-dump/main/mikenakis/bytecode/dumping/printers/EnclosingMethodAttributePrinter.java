package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.EnclosingMethodAttribute;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link EnclosingMethodAttribute} {@link AttributePrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class EnclosingMethodAttributePrinter extends AttributePrinter
{
	private final EnclosingMethodAttribute enclosingMethodAttribute;

	public EnclosingMethodAttributePrinter( EnclosingMethodAttribute enclosingMethodAttribute )
	{
		this.enclosingMethodAttribute = enclosingMethodAttribute;
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "class = " ).append( enclosingMethodAttribute.classConstant.getClassName() );
		if( enclosingMethodAttribute.methodNameAndTypeConstant.isPresent() )
		{
			builder.append( ", methodNameAndType = { " );
			renderingContext.newPrinter( enclosingMethodAttribute.methodNameAndTypeConstant.get() ).appendGildedTo( renderingContext, builder );
			builder.append( " }" );
		}
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			builder.append( "class = " );
			renderingContext.newPrinter( enclosingMethodAttribute.classConstant ).appendRawIndexTo( renderingContext, builder );
			if( enclosingMethodAttribute.methodNameAndTypeConstant.isPresent() )
			{
				builder.append( ", method = " );
				renderingContext.newPrinter( enclosingMethodAttribute.methodNameAndTypeConstant.get() ).appendRawIndexTo( renderingContext, builder );
			}
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			if( enclosingMethodAttribute.methodNameAndTypeConstant.isEmpty() )
				builder.append( enclosingMethodAttribute.classConstant.getClassName() );
			else
				RenderingContext.appendGildedNameAndTypeAndDescriptor( builder, enclosingMethodAttribute.methodNameAndTypeConstant.get().nameConstant,
					enclosingMethodAttribute.methodNameAndTypeConstant.get().descriptorConstant, enclosingMethodAttribute.classConstant );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}
}
