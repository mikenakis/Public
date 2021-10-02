package mikenakis.bytecode.reading;

import mikenakis.bytecode.model.Constant;
import mikenakis.bytecode.model.attributes.BootstrapMethodsAttribute;
import mikenakis.bytecode.model.constants.ClassConstant;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;
import mikenakis.kit.functional.Procedure1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html
 */
final class ConstantPool
{
	private final List<Constant> constants;
	private final boolean[] used;
	private final Collection<Procedure1<BootstrapMethodsAttribute>> bootstrapFixUps;

	ConstantPool( List<Constant> constants, boolean[] used, Collection<Procedure1<BootstrapMethodsAttribute>> bootstrapFixUps )
	{
		this.constants = constants;
		this.used = used;
		this.bootstrapFixUps = bootstrapFixUps;
	}

	void runBootstrapFixUps( BootstrapMethodsAttribute bootstrapMethodsAttribute )
	{
		for( var bootstrapFixUp : bootstrapFixUps )
			bootstrapFixUp.invoke( bootstrapMethodsAttribute );
		bootstrapFixUps.clear();
	}

	Constant getConstant( int constantIndex )
	{
		used[constantIndex] = true;
		return constants.get( constantIndex );
	}

	Collection<ClassConstant> getExtraClassReferences()
	{
		Collection<ClassConstant> extraClassReferences = new ArrayList<>();
		for( int i = 0;  i < constants.size();  i++ )
		{
			Constant constant = constants.get( i );
			if( constant == null )
				continue;
			if( used[i] )
				continue;
			assert constant.tag == Constant.tag_Class;
			extraClassReferences.add( constant.asClassConstant() );
		}
		return extraClassReferences;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return constants.size() + " constants";
	}
}
