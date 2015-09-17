package haven;

public class ViewFilter extends Window {
	public static ConfigSettingBoolean trees = new ConfigSettingBoolean("filter-tress", "Trees", true);
	public static ConfigSettingBoolean plants = new ConfigSettingBoolean("filter-plants", "Plants", true);
	public static ConfigSettingBoolean fences = new ConfigSettingBoolean("filter-fences", "Fences", true);
	public static ConfigSettingBoolean houses = new ConfigSettingBoolean("filter-houses", "Houses", true);

	public ViewFilter() {
		super(Coord.z, "View", true);
		int y = 0;

		add(trees.makeCheckBox(), new Coord(0, y));

		y += 25;
		add(plants.makeCheckBox(), new Coord(0, y));

		y += 25;
		add(fences.makeCheckBox(), new Coord(0, y));

		y += 25;
		add(houses.makeCheckBox(), new Coord(0, y));

		pack();
	}

	@SuppressWarnings("SimplifiableIfStatement")
	public static boolean shouldShowGob(Gob gob) {
		Resource res = gob.getres();
		if (res == null) return true;

		if (res.name.startsWith("gfx/terobjs/trees/"))
			return trees.isEnabled();

		if (res.name.startsWith("gfx/terobjs/plants/"))
			return plants.isEnabled();

		if (res.name.startsWith("gfx/terobjs/arch/palisade") || res.name.startsWith("gfx/terobjs/arch/pole"))
			return fences.isEnabled();

		if (res.name.startsWith("gfx/terobjs/arch/logcabin")
				|| res.name.startsWith("gfx/terobjs/arch/stonemansion")
				|| res.name.startsWith("gfx/terobjs/arch/timberhouse")
				|| res.name.startsWith("gfx/terobjs/arch/logcabin")
				|| res.name.startsWith("gfx/terobjs/arch/stonestead")
				)
			return houses.isEnabled();


		return true;
	}
}
