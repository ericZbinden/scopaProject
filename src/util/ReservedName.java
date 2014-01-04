package util;

public enum ReservedName {

	EMPTY_CONF_NAME("Open"), SERVER_NAME("Server"), CLOSED_CONF_NAME("Closed"), UNKNOWN_NAME("Unknown"), EMPTY_NAME(""), NULL("null");

	public String name;

	private ReservedName(String name) {
		this.name = name;
	}

	public static boolean isReserved(String name) {
		try {
			// Std reserved names
			fromString(name);
			return true;
		} catch (IllegalArgumentException e1) {
			return false;
		}
	}

	public static boolean isReserved(PlayerName name) {
		if (name != null)
			return isReserved(name.getName());

		return false;
	}

	public String getName() {
		return name;
	}

	public static ReservedName fromString(String text) throws IllegalArgumentException {
		if (text != null) {
			for (ReservedName rn : ReservedName.values()) {
				if (text.equalsIgnoreCase(rn.getName()))
					return rn;
			}
		}
		throw new IllegalArgumentException("Unable to find ReservedName " + text);
	}

}
