package mikenakis.tyraki;

import mikenakis.kit.EqualityComparator;
import mikenakis.kit.annotations.ExcludeFromJacocoGeneratedReport;

public final class CaseInsensitiveStringEqualityComparator implements EqualityComparator<String>
{
	public static final EqualityComparator<String> INSTANCE = new CaseInsensitiveStringEqualityComparator();

	private CaseInsensitiveStringEqualityComparator()
	{
	}

	@Override public boolean equals( String a, String b )
	{
		return String.CASE_INSENSITIVE_ORDER.compare( a, b ) == 0;
	}

	@ExcludeFromJacocoGeneratedReport @Override public String toString()
	{
		return getClass().getSimpleName();
	}
}
