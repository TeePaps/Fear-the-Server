package routing;

/**
 * Container to ease passing around a tuple of two objects. This object provides
 * a sensible implementation of equals(), returning true if equals() is true on
 * each of the contained objects.
 */
public class Pair<F, S> {
	public final F first;
	public final S second;

	/**
	 * Constructor for a Pair.
	 * 
	 * @param first
	 *            the first object in the Pair
	 * @param second
	 *            the second object in the pair
	 */
	public Pair(F first, S second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Checks the two objects for equality by delegating to their respective
	 * {@link Object#equals(Object)} methods.
	 * 
	 * @param o
	 *            the {@link Pair} to which this one is to be checked for
	 *            equality
	 * @return true if the underlying objects of the Pair are both considered
	 *         equal
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair otherPair = (Pair) other;
			return ((this.first == otherPair.first || (this.first != null
					&& otherPair.first != null && this.first
						.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
					&& otherPair.second != null && this.second
						.equals(otherPair.second))));
		}

		return false;
	}

	/**
	 * Compute a hash code using the hash codes of the underlying objects
	 * 
	 * @return a hashcode of the Pair
	 */
	@Override
	public int hashCode() {
		return (first == null ? 0 : first.hashCode())
				^ (second == null ? 0 : second.hashCode());
	}

	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	/**
	 * Convenience method for creating an appropriately typed pair.
	 * 
	 * @param a
	 *            the first object in the Pair
	 * @param b
	 *            the second object in the pair
	 * @return a Pair that is templatized with the types of a and b
	 */
	public static <A, B> Pair<A, B> create(A a, B b) {
		return new Pair<A, B>(a, b);
	}
}