package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.dumping.RenderingContext;

/**
 * {@link ClassConstant} {@link ConstantPrinter}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ClassConstantPrinter extends ConstantPrinter
{
	private final ClassConstant classConstant;

	public ClassConstantPrinter( ClassConstant classConstant )
	{
		super( classConstant );
		this.classConstant = classConstant;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( "name = " );
		RenderingContext.newPrinter( classConstant.nameConstant.orElseThrow() ).appendIndexTo( renderingContext, builder );
		builder.append( ' ' );
	}

	@Override public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		builder.append( classConstant.getClassName() );
	}
}
