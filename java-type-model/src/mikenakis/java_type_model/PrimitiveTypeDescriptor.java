package mikenakis.java_type_model;

import mikenakis.kit.Kit;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PrimitiveTypeDescriptor extends TypeDescriptor
{
	public static final PrimitiveTypeDescriptor Boolean /**/ = new PrimitiveTypeDescriptor( "boolean" );
	public static final PrimitiveTypeDescriptor Byte    /**/ = new PrimitiveTypeDescriptor( "byte" );
	public static final PrimitiveTypeDescriptor Char    /**/ = new PrimitiveTypeDescriptor( "char" );
	public static final PrimitiveTypeDescriptor Double  /**/ = new PrimitiveTypeDescriptor( "double" );
	public static final PrimitiveTypeDescriptor Float   /**/ = new PrimitiveTypeDescriptor( "float" );
	public static final PrimitiveTypeDescriptor Int     /**/ = new PrimitiveTypeDescriptor( "int" );
	public static final PrimitiveTypeDescriptor Long    /**/ = new PrimitiveTypeDescriptor( "long" );
	public static final PrimitiveTypeDescriptor Short   /**/ = new PrimitiveTypeDescriptor( "short" );
	public static final PrimitiveTypeDescriptor Void    /**/ = new PrimitiveTypeDescriptor( "void" );

	private static final Map<String,PrimitiveTypeDescriptor> byTypeName = Stream.of( Boolean, Byte, Char, Double, Float, Int, Long, Short, Void ) //
		.collect( Collectors.toMap( t -> t.typeName(), t -> t ) );

	public static PrimitiveTypeDescriptor of( Class<?> javaClass )
	{
		assert javaClass.isPrimitive();
		return ofTypeName( javaClass.getTypeName() );
	}

	public static PrimitiveTypeDescriptor ofTypeName( String typeName )
	{
		return Kit.map.get( byTypeName, typeName );
	}

	public final String typeName;

	private PrimitiveTypeDescriptor( String typeName )
	{
		this.typeName = typeName;
	}

	@Deprecated @Override public String typeName() { return typeName; }
	@Deprecated @Override public boolean isArray() { return false; }
	@Deprecated @Override public boolean isTerminal() { return false; }
	@Deprecated @Override public boolean isPrimitive() { return true; }
	@Deprecated @Override public PrimitiveTypeDescriptor asPrimitiveTypeDescriptor() { return this; }

	@Deprecated @Override public boolean equals( Object other )
	{
		if( other instanceof PrimitiveTypeDescriptor kin )
			return equals( kin );
		return false;
	}

	public boolean equals( PrimitiveTypeDescriptor other )
	{
		return typeName.equals( other.typeName );
	}

	@Override public int hashCode()
	{
		return Objects.hash( PrimitiveTypeDescriptor.class, typeName );
	}
}
