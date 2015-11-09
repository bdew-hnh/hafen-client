package haven;

public class ConfigSettingInt {
	public String id;
	public String name;
	public int value;
	public int min;
	public int max;

	public ConfigSettingInt(String id, int defaultValue, int min, int max) {
		this.id = id;
		this.value = Utils.getprefi(id, defaultValue);
		this.min = min;
		this.max = max;
	}

	public int get() {
		return value;
	}

	public void set(int value) {
		this.value = value;
		Utils.setprefi(id, value);
	}

	public HSlider mkSlider(int w) {
		return new HSlider(w, min, max, value) {
			protected void attach(UI ui) {
				super.attach(ui);
				val = value;
			}

			public void changed() {
				ConfigSettingInt.this.set(val);
			}
		};
	}

    public Label mkLabel() {
        return new DynLabel(() -> Integer.toString(value));
    }
}
