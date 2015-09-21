package haven;

public class ConfigSettingInt {
	public String id;
	public String name;
	public int value;

	public ConfigSettingInt(String id, int defaultValue) {
		this.id = id;
		this.value = Utils.getprefi(id, defaultValue);
	}

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
		Utils.setprefi(id, value);
	}
}
