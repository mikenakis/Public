package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.Hasher;
import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class CaseInsensitiveStringHasher implements Hasher<String>
{
	public static final Hasher<String> INSTANCE = new CaseInsensitiveStringHasher();

	private CaseInsensitiveStringHasher()
	{
	}

	@Override public int getHashCode( String item )
	{
		return item.toUpperCase().hashCode();
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
