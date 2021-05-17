package mikenakis.bytecode;

import mikenakis.bytecode.attributes.BootstrapMethod;
import mikenakis.bytecode.constants.ClassConstant;
import mikenakis.bytecode.constants.DoubleConstant;
import mikenakis.bytecode.constants.FieldReferenceConstant;
import mikenakis.bytecode.constants.FloatConstant;
import mikenakis.bytecode.constants.IntegerConstant;
import mikenakis.bytecode.constants.InterfaceMethodReferenceConstant;
import mikenakis.bytecode.constants.InvokeDynamicConstant;
import mikenakis.bytecode.constants.LongConstant;
import mikenakis.bytecode.constants.MethodHandleConstant;
import mikenakis.bytecode.constants.MethodTypeConstant;
import mikenakis.bytecode.constants.NameAndTypeConstant;
import mikenakis.bytecode.constants.PlainMethodReferenceConstant;
import mikenakis.bytecode.constants.ReferenceConstant;
import mikenakis.bytecode.constants.StringConstant;
import mikenakis.bytecode.constants.Utf8Constant;
import mikenakis.kit.Kit;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * Dependency Extraction Helper.
 *
 * @author Michael Belivanakis (michael.gr)
 */
final class DependencyExtractor
{
	static Collection<String> getDependencyNames( ByteCodeType byteCodeType )
	{
		Collection<String> dependencyNames = new LinkedHashSet<>();
		for( Constant constant : byteCodeType.constantPool )
			addDependencyTypeNameFromConstant( byteCodeType, constant, dependencyNames );
		return dependencyNames;
	}

	private static void addDependencyName( String dependencyName, Collection<String> dependencyNames )
	{
		assert dependencyName != null;
		dependencyName = stripArrayNotation( dependencyName );
		assert ByteCodeHelpers.isJavaTypeName( dependencyName );
		Kit.collection.tryAdd( dependencyNames, dependencyName );
	}

	private static String stripArrayNotation( String typeName )
	{
		while( typeName.endsWith( "[]" ) )
			typeName = typeName.substring( 0, typeName.length() - 2 );
		return typeName;
	}

	private static void addDependencyTypeNameFromConstant( ByteCodeType byteCodeType, Constant constant, Collection<String> dependencyNames )
	{
		switch( constant.kind.tag )
		{
			case Utf8Constant.TAG:
			case IntegerConstant.TAG:
			case FloatConstant.TAG:
			case LongConstant.TAG:
			case DoubleConstant.TAG:
			case StringConstant.TAG:
				break;
			case ClassConstant.TAG:
			{
				ClassConstant classConstant = constant.asClassConstant();
				String typeName = classConstant.getClassName();
				addDependencyName( typeName, dependencyNames );
				break;
			}
			case FieldReferenceConstant.TAG:
			case PlainMethodReferenceConstant.TAG:
			case InterfaceMethodReferenceConstant.TAG:
			{
				ReferenceConstant referenceConstant = constant.asReferenceConstant();
				addDependencyTypeNamesFromReferenceConstant( referenceConstant, dependencyNames );
				break;
			}
			case NameAndTypeConstant.TAG:
			{
				NameAndTypeConstant nameAndTypeConstant = constant.asNameAndTypeConstant();
				String descriptor = nameAndTypeConstant.descriptorConstant.getStringValue();
				addDependencyTypeNamesFromDescriptorString( descriptor, dependencyNames );
				break;
			}
			case MethodHandleConstant.TAG:
			{
				MethodHandleConstant methodHandleConstant = constant.asMethodHandleConstant();
				addDependencyTypeNamesFromReferenceConstant( methodHandleConstant.referenceConstant, dependencyNames );
				break;
			}
			case MethodTypeConstant.TAG:
			{
				MethodTypeConstant methodTypeConstant = constant.asMethodTypeConstant();
				String descriptor = methodTypeConstant.descriptorConstant.getStringValue();
				addDependencyTypeNamesFromDescriptorString( descriptor, dependencyNames );
				break;
			}
			case InvokeDynamicConstant.TAG:
			{
				InvokeDynamicConstant invokeDynamicConstant = constant.asInvokeDynamicConstant();
				BootstrapMethod bootstrapMethod = byteCodeType.getBootstrapMethodByIndex( invokeDynamicConstant.bootstrapMethodIndex );
				addDependencyTypeNamesFromReferenceConstant( bootstrapMethod.methodHandleConstant.referenceConstant, dependencyNames );
				for( Constant argumentConstant : bootstrapMethod.argumentConstants )
					addDependencyTypeNameFromConstant( byteCodeType, argumentConstant, dependencyNames );
				addDependencyTypeNamesFromDescriptorString( invokeDynamicConstant.nameAndTypeConstant.descriptorConstant.getStringValue(), dependencyNames );
				break;
			}
			default:
				assert false;
		}
	}

	private static void addDependencyTypeNamesFromReferenceConstant( ReferenceConstant referenceConstant, Collection<String> dependencyNames )
	{
		addDependencyName( referenceConstant.typeConstant.getClassName(), dependencyNames );
		addDependencyTypeNamesFromDescriptorString( referenceConstant.nameAndTypeConstant.descriptorConstant.getStringValue(), dependencyNames );
	}

	private static void addDependencyTypeNamesFromDescriptorString( String descriptorString, Collection<String> dependencyNames )
	{
		Descriptor descriptor = Descriptor.from( descriptorString );
		addDependencyName( descriptor.typeName, dependencyNames );
		if( descriptor.argumentTypeNames != null )
			for( String argumentTypeName : descriptor.argumentTypeNames )
				addDependencyName( argumentTypeName, dependencyNames );
	}
}
