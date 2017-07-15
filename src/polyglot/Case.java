package polyglot;

/**
 * This enumerated type represents several possible text cases: upper case,
 * lower case, title case.
 * 
 * A special case is provided: unchanged. When using this case, the output from
 * Google's API will not be changed in terms of capitalization.
 * 
 * The enumerated type provides a method to convert a String to a case. Each
 * case has its own method associated with it, which converts a String input to
 * the desired case.
 *
 * Title case conversion adapted from:
 * http://stackoverflow.com/questions/1086123/string-conversion-to-title-case.
 * 
 * @author Elliott Bolzan
 */
public enum Case {

	UPPER() {
		@Override
		public String convert(String input) {
			return input.toUpperCase();
		}
	},
	LOWER() {
		@Override
		public String convert(String input) {
			return input.toLowerCase();
		}
	},
	TITLE() {
		@Override
		public String convert(String input) {
			StringBuilder titleCase = new StringBuilder();
			boolean nextTitleCase = true;
			for (char c : input.toCharArray()) {
				if (Character.isSpaceChar(c)) {
					nextTitleCase = true;
				} else if (nextTitleCase) {
					c = Character.toTitleCase(c);
					nextTitleCase = false;
				}
				titleCase.append(c);
			}
			return titleCase.toString();
		}
	},
	UNCHANGED() {
		@Override
		public String convert(String input) {
			return input;
		}
	};

	/**
	 * Convert a String to the case it is associated with. The implementation is
	 * delegated using polymorphism.
	 * 
	 * @param input
	 *            the String to be converted
	 * @return a String with the desired case.
	 */
	public abstract String convert(String input);

}
