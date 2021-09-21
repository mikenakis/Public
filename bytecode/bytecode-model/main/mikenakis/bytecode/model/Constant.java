package mikenakis.bytecode.model;

import mikenakis.bytecode.exceptions.UnknownConstantException;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.bytecode.model.constants.DoubleConstant;
import mikenakis.bytecode.model.constants.FieldReferenceConstant;
import mikenakis.bytecode.model.constants.FloatConstant;
import mikenakis.bytecode.model.constants.IntegerConstant;
import mikenakis.bytecode.model.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.model.constants.InvokeDynamicConstant;
import mikenakis.bytecode.model.constants.LongConstant;
import mikenakis.bytecode.model.constants.MethodHandleConstant;
import mikenakis.bytecode.model.constants.MethodReferenceConstant;
import mikenakis.bytecode.model.constants.MethodTypeConstant;
import mikenakis.bytecode.model.constants.Mutf8Constant;
import mikenakis.bytecode.model.constants.NameAndDescriptorConstant;
import mikenakis.bytecode.model.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.model.constants.ReferenceConstant;
import mikenakis.bytecode.model.constants.StringConstant;
import mikenakis.bytecode.model.constants.ValueConstant;
import mikenakis.kit.Kit;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.lang.constant.ConstantDesc;
import java.util.Comparator;
import java.util.List;

/**
 * Represents a java class file constant.
 *
 * @author Michael Belivanakis (michael.gr)
 */
public abstract class Constant implements Comparable<Constant>
{
	public enum Tag
	{
		UndefinedOrdinal0        /**/( 0, true), //
		Mutf8                    /**/( 1 ), // JVMS::CONSTANT_Utf8 addresses JVMS::CONSTANT_Utf8_info
		UndefinedOrdinal2        /**/( 2, true ), //
		Integer                  /**/( 3 ), // JVMS::CONSTANT_Integer addresses JVMS::CONSTANT_Integer_info
		Float                    /**/( 4 ), // JVMS::CONSTANT_Float addresses JVMS::CONSTANT_Float_info
		Long                     /**/( 5 ), // JVMS::CONSTANT_Long addresses JVMS::CONSTANT_Long_info
		Double                   /**/( 6 ), // JVMS::CONSTANT_Double addresses JVMS::CONSTANT_Double_info
		Class                    /**/( 7 ), // JVMS::CONSTANT_Class addresses JVMS::CONSTANT_Class_info
		String                   /**/( 8 ), // JVMS::CONSTANT_String addresses JVMS::CONSTANT_String_info
		FieldReference           /**/( 9 ), // JVMS::CONSTANT_Fieldref addresses JVMS::CONSTANT_Fieldref_info
		MethodReference          /**/( 10 ), // JVMS::CONSTANT_MethodRef addresses JVMS::CONSTANT_MethodRef_info
		InterfaceMethodReference /**/( 11 ), // JVMS::CONSTANT_InterfaceMethodRef addresses JVMS::CONSTANT_InterfaceMethodRef_info
		NameAndDescriptor        /**/( 12 ), // JVMS::CONSTANT_NameAndType addresses JVMS::CONSTANT_NameAndType_info
		UndefinedOrdinal13       /**/( 13, true ), //
		UndefinedOrdinal14       /**/( 14, true ), //
		MethodHandle             /**/( 15 ), // JVMS::CONSTANT_MethodHandle addresses JVMS::CONSTANT_MethodHandle_info
		MethodType               /**/( 16 ), // JVMS::CONSTANT_MethodType addresses JVMS::CONSTANT_MethodType_info
		UndefinedOrdinal17       /**/( 17, true ), //
		InvokeDynamic            /**/( 18 ); // JVMS::CONSTANT_InvokeDynamic addresses JVMS::CONSTANT_InvokeDynamic_info

		private static final List<Tag> values = List.of( values() );

		private final boolean undefined;

		Tag( int tagNumber )
		{
			this( tagNumber, false );
		}

		Tag( int tagNumber, boolean undefined )
		{
			assert tagNumber == ordinal();
			this.undefined = undefined;
		}

		public static Tag fromNumber( int tagNumber )
		{
			if( tagNumber < 0 || tagNumber >= values.size() )
				throw new UnknownConstantException( tagNumber );
			Tag tag = values.get( tagNumber );
			if( tag.undefined )
				throw new UnknownConstantException( tagNumber );
			return tag;
		}

		public int tagNumber()
		{
			return ordinal();
		}
	}

	public final Tag tag;

	protected Constant( Tag tag )
	{
		this.tag = tag;
	}

	public ConstantDesc constantDescriptor()
	{
		assert false;
		return null;
	}

	@Override public int compareTo( Constant other )
	{
		int d = tag.compareTo( other.tag );
		if( d != 0 )
			return d;
		@SuppressWarnings( "unchecked" ) Comparator<Constant> comparator = (Comparator<Constant>)comparator();
		return comparator.compare( this, other );
	}

	public static final Comparator<Constant> comparator = Comparator.naturalOrder();

	protected Comparator<? extends Constant> comparator()
	{
		assert false; //we do not expect this kind of constant to be compared against another.
		return null;
	}

	@Override public abstract boolean equals( Object other );

	@Override public abstract int hashCode();

	@ExcludeFromJacocoGeneratedReport public <T extends Comparable<T>> ValueConstant<T> /**/ asValueConstant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public Mutf8Constant                              /**/ asMutf8Constant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public IntegerConstant                            /**/ asIntegerConstant                  /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FloatConstant                              /**/ asFloatConstant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public LongConstant                               /**/ asLongConstant                     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public DoubleConstant                             /**/ asDoubleConstant                   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ClassConstant                              /**/ asClassConstant                    /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public StringConstant                             /**/ asStringConstant                   /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public ReferenceConstant                          /**/ asReferenceConstant                /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public FieldReferenceConstant                     /**/ asFieldReferenceConstant           /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodReferenceConstant                    /**/ asMethodReferenceConstant          /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public PlainMethodReferenceConstant               /**/ asPlainMethodReferenceConstant     /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InterfaceMethodReferenceConstant           /**/ asInterfaceMethodReferenceConstant /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public NameAndDescriptorConstant                  /**/ asNameAndDescriptorConstant        /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodHandleConstant                       /**/ asMethodHandleConstant             /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public MethodTypeConstant                         /**/ asMethodTypeConstant               /**/() { return Kit.fail(); }
	@ExcludeFromJacocoGeneratedReport public InvokeDynamicConstant                      /**/ asInvokeDynamicConstant            /**/() { return Kit.fail(); }
}
