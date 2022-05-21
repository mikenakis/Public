package io.github.mikenakis.bytecode.writing;

import io.github.mikenakis.bytecode.model.Constant;
import io.github.mikenakis.kit.Kit;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the constant pool of a java class file.
 * <p>
 * Source of information: The Java Virtual Machine Specification (JVMS) Chapter 4: The class File Format
 * <p>
 * <a href="https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html">https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html</a>
 */
public final class WritingConstantPool implements Interner
{
	private final List<Constant> entries = new ArrayList<>();

	public WritingConstantPool()
	{
		entries.add( null ); // first entry is empty. (Ancient legacy bollocks.)
	}

	private int tryGetIndex( Constant constant )
	{
		for( int i = 0; i < entries.size(); i++ )
		{
			Constant existingConstant = entries.get( i );
			if( existingConstant == null )
				continue;
			if( existingConstant.equals( constant ) )
				return i;
		}
		return -1;
	}

	public int getConstantIndex( Constant constant )
	{
		int index = tryGetIndex( constant );
		assert index != -1;
		return index;
	}

	@Override public void intern( Constant constant )
	{
		int existingIndex = tryGetIndex( constant );
		assert existingIndex != 0;
		if( existingIndex == -1 )
		{
			assert !entries.isEmpty();
			entries.add( constant );
			if( constant.tag == Constant.tag_Long || constant.tag == Constant.tag_Double )
				entries.add( null ); //8-byte constants occupy two constant pool entries. (Ancient legacy bollocks.)
		}
	}

	public int size()
	{
		return entries.size();
	}

	public Iterable<Constant> constants()
	{
		return Kit.iterable.filter( entries, c -> c != null );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return size() + " entries";
	}
}
