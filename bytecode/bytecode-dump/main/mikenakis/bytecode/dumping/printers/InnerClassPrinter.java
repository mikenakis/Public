package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.attributes.InnerClass;
import mikenakis.bytecode.dumping.Printer;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;

/**
 * {@link InnerClass} {@link Printer}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class InnerClassPrinter extends Printer
{
	private final InnerClass innerClass;

	public InnerClassPrinter( InnerClass innerClass )
	{
		this.innerClass = innerClass;
	}

	@Override public void appendTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( renderingContext.style.raw )
		{
			if( innerClass.outerClassConstant.isPresent() )
			{
				builder.append( "outerClass = " );
				renderingContext.newPrinter( innerClass.outerClassConstant.get() ).appendRawIndexTo( renderingContext, builder );
			}
			builder.append( " innerClassAccessFlags = 0x" ).append( Integer.toHexString( innerClass.innerClassAccessFlags ) );
			builder.append( " innerClass = " );
			renderingContext.newPrinter( innerClass.innerClassConstant ).appendRawIndexTo( renderingContext, builder );
			if( innerClass.innerNameConstant.isPresent() )
			{
				builder.append( ", innerName = " );
				renderingContext.newPrinter( innerClass.innerNameConstant.get() ).appendRawIndexTo( renderingContext, builder );
			}
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendGildedTo( renderingContext, builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
	}

	public void appendGildedTo( RenderingContext renderingContext, StringBuilder builder )
	{
		if( innerClass.outerClassConstant.isPresent() )
		{
			renderingContext.newPrinter( innerClass.outerClassConstant.get() ).appendGildedTo( renderingContext, builder );
			builder.append( " : " );
		}
		RenderingContext.appendBitsAsString( builder, innerClass.innerClassAccessFlags, InnerClassPrinter::getAccessFlagName, " " );
		builder.append( ' ' );
		renderingContext.newPrinter( innerClass.innerClassConstant ).appendGildedTo( renderingContext, builder );
		if( innerClass.innerNameConstant.isPresent() )
		{
			builder.append( ' ' );
			renderingContext.newPrinter( innerClass.innerNameConstant.get() ).appendGildedTo( renderingContext, builder );
		}
	}

	public static String getAccessFlagName( int flag )
	{
		assert Integer.bitCount( flag ) == 1;
		switch( flag )
		{
			//@formatter:off
			case InnerClass.ACC_PUBLIC	   : return "public";
			case InnerClass.ACC_PRIVATE    : return "private";
			case InnerClass.ACC_PROTECTED  : return "protected";
			case InnerClass.ACC_STATIC	   : return "static";
			case InnerClass.ACC_FINAL	   : return "final";
			case InnerClass.ACC_INTERFACE  : return "interface";
			case InnerClass.ACC_ABSTRACT   : return "abstract";
			case InnerClass.ACC_SYNTHETIC  : return "synthetic";
			case InnerClass.ACC_ANNOTATION : return "annotation";
			case InnerClass.ACC_ENUM	   : return "enum";
			default: return null;
			//@formatter:on
		}
	}
}
