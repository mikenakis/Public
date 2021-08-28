package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.InvokeDynamicConstant;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link InvokeDynamicConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InvokeDynamicConstantPrinter extends ConstantPrinter
{
	private final InvokeDynamicConstant invokeDynamicConstant;

	public InvokeDynamicConstantPrinter( InvokeDynamicConstant invokeDynamicConstant )
	{
		super( invokeDynamicConstant );
		this.invokeDynamicConstant = invokeDynamicConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "bootstrapMethod = " );
		if( renderingContext.style.raw )
			builder.append( invokeDynamicConstant.bootstrapMethodIndex );
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			RenderingContext.newPrinter( invokeDynamicConstant.getBootstrapMethod() ).appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}

		renderingContext.appendNameAndDescriptor( builder, invokeDynamicConstant.nameAndTypeConstant.nameConstant, invokeDynamicConstant.nameAndTypeConstant.descriptorConstant );
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
//		builder.append( "bootstrapMethod = [" ).append( bootstrapMethodIndex ).append( "] { " );
		RenderingContext.newPrinter( invokeDynamicConstant.getBootstrapMethod() ).appendGildedTo( renderingContext, builder );
		builder.append( " " );
		RenderingContext.appendGildedNameAndTypeAndDescriptor( builder, invokeDynamicConstant.nameAndTypeConstant.nameConstant,
			invokeDynamicConstant.nameAndTypeConstant.descriptorConstant );
	}
}
