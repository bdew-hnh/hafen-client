package haven;


import java.awt.*;

public class QuickSlotsWdg extends Widget implements DTarget {
    private static final Color brdr = new Color(28, 35, 24);
    private static final Color bg = new Color(40, 50, 34);
    private static final Coord slotsz = new Coord(44, 44);
    private static final Coord itemsz = new Coord(42, 42);
    private static final Coord lc = Coord.z;
    private static final Coord rc = new Coord(50, 0);

    public QuickSlotsWdg() {
        super(new Coord(44 + 44 + 6, 44));

    }

    @Override
    public void draw(GOut g) {
        Equipory e = getequipory();
        if (e != null) {
            g.chcolor(brdr);
            g.rect(lc, slotsz);
            g.chcolor(bg);
            g.frect(lc.add(1, 1), itemsz);
            WItem left = e.quickslots[6];
            g.chcolor();
            if (left != null)
                left.draw(g.reclipl(lc.add(6, 6), g.sz));

            g.chcolor(brdr);
            g.rect(rc, slotsz);
            g.chcolor(bg);
            g.frect(rc.add(1, 1), itemsz);
            WItem right = e.quickslots[7];
            if (right != null)
                right.draw(g.reclipl(rc.add(6, 6), g.sz));

            g.chcolor();
            g.atextstroked("C1", lc.add(4, 4).add(itemsz), Color.gray, Color.black, 1, 1);
            g.atextstroked("C2", rc.add(4, 4).add(itemsz), Color.gray, Color.black, 1, 1);
        }
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
        Equipory e = getequipory();
        if (e != null) {
            WItem left = e.quickslots[6];
            WItem right = e.quickslots[7];
            if (c.isect(lc, slotsz) && left != null)
                return left.tooltip(c.sub(lc), prev);
            if (c.isect(rc, slotsz) && right != null)
                return right.tooltip(c.sub(rc), prev);
        }
        return super.tooltip(c, prev);
    }

    @Override
    public boolean drop(Coord cc, Coord ul) {
        Equipory e = getequipory();
        if (e != null) {
            e.wdgmsg("drop", cc.x <= 47 ? 6 : 7);
            return true;
        }
        return false;
    }

    @Override
    public boolean iteminteract(Coord cc, Coord ul) {
        Equipory e = getequipory();
        if (e != null) {
            WItem w = e.quickslots[cc.x <= 47 ? 6 : 7];
            if (w != null) {
                return w.iteminteract(cc, ul);
            }
        }
        return false;
    }

    public void click(int slot) {
        Equipory e = getequipory();
        if (e != null) {
            if (!ui.gui.hand.isEmpty()) {
                e.wdgmsg("drop", slot);
            } else {
                WItem w = e.quickslots[slot];
                if (w != null)
                    w.item.wdgmsg("take", Coord.z);
            }
        }
    }

    @Override
    public boolean mousedown(Coord c, int button) {
        Equipory e = getequipory();
        if (e != null) {
            WItem w = e.quickslots[c.x <= 47 ? 6 : 7];
            if (w != null) {
                w.mousedown(Coord.z, button);
                return true;
            }
        }
        return false;
    }

    private Equipory getequipory() {
        Window e = ((GameUI) parent).equwnd;
        if (e != null) {
            for (Widget w = e.lchild; w != null; w = w.prev) {
                if (w instanceof Equipory)
                    return (Equipory) w;
            }
        }
        return null;
    }
}