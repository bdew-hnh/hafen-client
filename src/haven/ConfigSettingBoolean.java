package haven;

public class ConfigSettingBoolean {
	public String id;
	public String name;
	public boolean defaultState;

	public ConfigSettingBoolean(String id, String name, boolean defaultState) {
		this.id = id;
		this.name = name;
		this.defaultState = defaultState;
	}

	public boolean isEnabled() {
		return Utils.getprefb(id, defaultState);
	}

	public void setEnabled(Boolean state) {
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
