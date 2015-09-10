package haven;

public class ConfigSettingInt {
	public String id;
	public String name;
	public int defaultValue;

	public ConfigSettingInt(String id, int defaultValue) {
		this.id = id;
		this.defaultValue = defaultValue;
	}

	public int get() {
		return Utils.getprefi(id, defaultValue);
	}

	public void set(int value) {
		Utils.setprefi(id, value);
	}
}
