package haven;

public class ConfigSettingBoolean {
	public String id;
	public String name;
	public boolean enabled;

	public ConfigSettingBoolean(String id, String name, boolean defaultState) {
		this.id = id;
		this.name = name;
		this.enabled = Utils.getprefb(id, defaultState);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean state) {
		enabled = state;
		Utils.setprefb(id, state);
	}

	public void toggle() {
		setEnabled(!isEnabled());
	}

	public CheckBox makeCheckBox() {
		return new CheckBox(name) {
			{
				a = isEnabled();
			}

			public void set(boolean val) {
				setEnabled(val);
				a = val;
			}
		};
	}
}
