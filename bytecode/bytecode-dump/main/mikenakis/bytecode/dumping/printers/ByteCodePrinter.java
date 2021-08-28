package mikenakis.bytecode.dumping.printers;

import mikenakis.bytecode.ByteCodeHelpers;
import mikenakis.bytecode.ByteCodeType;
import mikenakis.bytecode.Constant;
import mikenakis.bytecode.ConstantPool;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.dumping.RenderingContext;
import mikenakis.bytecode.dumping.Style;
import mikenakis.bytecode.dumping.twig.Twig;
import mikenakis.kit.Kit;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Prints a {@link ByteCodeType}.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public final class ByteCodePrinter
{
	public final ByteCodeType byteCodeType;

	public ByteCodePrinter( ByteCodeType byteCodeType )
	{
		this.byteCodeType = byteCodeType;
	}

	public String toLongString( Style style )
	{
		RenderingContext renderingContext = new RenderingContext( style, byteCodeType );
		List<Twig> children = new ArrayList<>();
		if( renderingContext.style.raw )
			children.add( constantPoolToTwig( renderingContext, byteCodeType.constantPool, "constantPool" ) );
		children.add( Twig.of( "fields (" + byteCodeType.fields.size() + " entries)",
			byteCodeType.fields.stream().map( field -> RenderingContext.newPrinter( field ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ) );
		children.add( Twig.of( "methods (" + byteCodeType.methods.size() + " entries)",
			byteCodeType.methods.stream().map( method -> RenderingContext.newPrinter( method ).toTwig( renderingContext, "" ) ).collect( Collectors.toList() ) ) );
		children.add( RenderingContext.newPrinter( byteCodeType.attributes ).toTwig( renderingContext, "attributes" ) );
		Twig rootNode = Twig.of( toString( renderingContext ), children );
		var builder = new StringBuilder( 1024 * 100 );
		RenderingContext.treeDump( rootNode, Twig::getChildren, Twig::getPayload, s -> emit( builder, s ) );
		return builder.toString();
	}

	private String toString( RenderingContext renderingContext )
	{
		var builder = new StringBuilder( 1024 );
		builder.append( getKeyword() ).append( " version = " ).append( byteCodeType.majorVersion ).append( '.' ).append( byteCodeType.minorVersion );
		if( renderingContext.style.raw )
		{
			builder.append( " accessFlags = 0x" ).append( Integer.toHexString( ByteCodeType.accessFlagEnum.toInt( byteCodeType.access ) ) );
			builder.append( ", thisClass = " );
			RenderingContext.newPrinter( byteCodeType.thisClassConstant ).appendRawIndexTo( renderingContext, builder );
			if( byteCodeType.superClassConstant.isPresent() )
			{
				builder.append( ", superClass = " );
				RenderingContext.newPrinter( byteCodeType.superClassConstant.get() ).appendRawIndexTo( renderingContext, builder );
			}
			builder.append( ", interfaces = [" );
			boolean first = true;
			for( ClassConstant interfaceClassConstant : byteCodeType.interfaceClassConstants )
			{
				first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
				RenderingContext.newPrinter( interfaceClassConstant ).appendRawIndexTo( renderingContext, builder );
			}
			builder.append( "] " );
		}
		if( renderingContext.style == Style.MIXED )
			builder.append( ' ' );
		if( renderingContext.style.gild )
		{
			builder.append( RenderingContext.GILDING_PREFIX );
			appendGildedTo( builder );
			builder.append( RenderingContext.GILDING_SUFFIX );
		}
		return builder.toString();
	}

	private void appendGildedTo( StringBuilder builder )
	{
		EnumSet<ByteCodeType.Access> adjustedAccess = EnumSet.copyOf( byteCodeType.access );
		if( byteCodeType.isInterface() )
		{
			Kit.collection.tryRemove( adjustedAccess, ByteCodeType.Access.Interface );
			Kit.collection.tryRemove( adjustedAccess, ByteCodeType.Access.Abstract );
		}
		else
		{
			Kit.collection.tryRemove( adjustedAccess, ByteCodeType.Access.Super );
			Kit.collection.tryRemove( adjustedAccess, ByteCodeType.Access.Enum );
		}
		ByteCodeType.accessFlagEnum.toStringBuilder( builder, adjustedAccess );
		builder.append( getKeyword() ).append( ' ' ).append( byteCodeType.getName() );
		Optional<String> superClassName = byteCodeType.getSuperClassName();
		if( superClassName.isPresent() && !superClassName.get().equals( Object.class.getName() ) )
			builder.append( " extends " ).append( superClassName );
		if( !byteCodeType.interfaceClassConstants.isEmpty() )
		{
			builder.append( byteCodeType.isInterface() ? " extends " : " implements " );
			boolean first = true;
			for( ClassConstant interfaceClassConstant : byteCodeType.interfaceClassConstants )
			{
				first = Kit.stringBuilder.appendDelimiter( builder, first, ", " );
				builder.append( interfaceClassConstant.getClassName() );
			}
		}
	}

	private static void emit( StringBuilder builder, String s )
	{
		builder.append( s );
		builder.append( '\n' );
	}

	private String getKeyword()
	{
		if( byteCodeType.access.contains( ByteCodeType.Access.Interface ) )
			return "interface";
		if( byteCodeType.access.contains( ByteCodeType.Access.Enum ) )
			return "enum";
		return "class";
	}

	private Twig constantPoolToTwig( RenderingContext renderingContext, ConstantPool constantPool, String prefix )
	{
		List<Twig> children = new ArrayList<>();
		for( int i = 0; i < constantPool.size(); i++ )
		{
			String childPrefix = renderingContext.style.raw ? ByteCodeHelpers.getConstantId( i ) + " " : "";
			Twig twig;
			if( !constantPool.isDefined( i ) )
				twig = Twig.of( childPrefix + "null" );
			else
			{
				Constant constant = constantPool.getConstant( i );
				twig = RenderingContext.newPrinter( constant ).toTwig( renderingContext, childPrefix + constant.kind.name + ' ' );
			}
			children.add( twig );
		}
		if( !renderingContext.style.raw )
			children.sort( Comparator.comparing( Twig::getPayload ) );
		return Twig.of( prefix + " (" + toString() + ")", children );
	}
}
