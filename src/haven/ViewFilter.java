package haven;

public class ViewFilter extends Window {
    public static class ConfigSettingFilter extends ConfigSettingBoolean {
        public ConfigSettingFilter(String id, String name, boolean defaultState) {
            super(id, name, defaultState);
        }

        @Override
        public void setEnabled(boolean state) {
            super.setEnabled(state);
            MapView.needsRefresh = true;
        }
    }

    public static ConfigSettingFilter trees = new ConfigSettingFilter("filter-tress", "Trees", true);
    public static ConfigSettingFilter bushes = new ConfigSettingFilter("filter-bushes", "Bushes", true);
    public static ConfigSettingFilter plants = new ConfigSettingFilter("filter-plants", "Plants", true);
    public static ConfigSettingFilter fences = new ConfigSettingFilter("filter-fences", "Fences", true);
    public static ConfigSettingBoolean houses = new ConfigSettingFilter("filter-houses", "Houses", true);

    public ViewFilter() {
        super(Coord.z, "View", true);
        int y = 0;

        add(trees.makeCheckBox(), new Coord(0, y));

        y += 25;
        add(bushes.makeCheckBox(), new Coord(0, y));

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

        if (res.name.startsWith("gfx/terobjs/bushes/"))
            return bushes.isEnabled();

        if (res.name.startsWith("gfx/terobjs/plants/") && !res.basename().equals("trellis"))
            return plants.isEnabled();

        if (res.name.startsWith("gfx/terobjs/arch/palisade") || res.name.startsWith("gfx/terobjs/arch/pole") || res.name.startsWith("gfx/terobjs/arch/brickwall"))
            return fences.isEnabled();

        if (res.name.startsWith("gfx/terobjs/arch/logcabin")
                || res.name.startsWith("gfx/terobjs/arch/stonemansion")
                || res.name.startsWith("gfx/terobjs/arch/timberhouse")
                || res.name.startsWith("gfx/terobjs/arch/logcabin")
                || res.name.startsWith("gfx/terobjs/arch/stonestead")
                || res.name.startsWith("gfx/terobjs/arch/greathall")
                || res.name.startsWith("gfx/terobjs/arch/stonetower")
                )
            return houses.isEnabled();


        return true;
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (msg.equals("close")) {
            hide();
        } else super.wdgmsg(sender, msg, args);
    }
}
