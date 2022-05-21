package io.github.mikenakis.tyraki;

import io.github.mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

import java.util.Comparator;

public final class CaseInsensitiveStringComparator implements Comparator<String>
{
	public static final Comparator<String> INSTANCE = new CaseInsensitiveStringComparator();

	private CaseInsensitiveStringComparator()
	{
	}

	@Override public int compare( String a, String b )
	{
		return String.CASE_INSENSITIVE_ORDER.compare( a, b );
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
