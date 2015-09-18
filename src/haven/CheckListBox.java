/*
 *  This file is part of bdew's Haven & Hearth modified client.
 *  Copyright (C) 2015 bdew
 *
 *  Redistribution and/or modification of this file is subject to the
 *  terms of the GNU Lesser General Public License, version 3, as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Other parts of this source tree adhere to other copying
 *  rights. Please see the file `COPYING' in the root directory of the
 *  source tree for details.
 *
 *  A copy the GNU Lesser General Public License is distributed along
 *  with the source tree of which this file is a part in the file
 *  `doc/LPGL-3'. If it is missing for any reason, please see the Free
 *  Software Foundation's website at <http://www.fsf.org/>, or write
 *  to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 *  Boston, MA 02111-1307 USA
 */

package haven;

import haven.CheckListBox.CheckListBoxItem;

import java.util.ArrayList;
import java.util.List;

public class CheckListBox extends Listbox<CheckListBoxItem> {

    private static final Tex chk = Resource.loadtex("gfx/hud/chkmarks");
    public List<CheckListBoxItem> items = new ArrayList<>();
    public ConfigSettingCheckboxList data;
    private Button toggle;

    public CheckListBox(int w, int h, final ConfigSettingCheckboxList data) {
        super(w, h, 18);
        this.data = data;
        for (String ent : data.options) {
            items.add(new CheckListBoxItem(ent));
        }
        toggle = add(new Button(w, "Toggle All"), 0, sz.y + 3);
        pack();
    }

    public class CheckListBoxItem implements Comparable<CheckListBoxItem> {
        public String id;

        public CheckListBoxItem(String id) {
            this.id = id;
        }

        public void setSelected(boolean selected) {
            data.set(id, selected);
        }

        public boolean isSelected() {
            return data.isSelected(id);
        }

        @Override
        public int compareTo(CheckListBoxItem o) {
            return this.id.compareTo(o.id);
        }
    }

    @Override
    protected void itemclick(CheckListBoxItem itm, int button) {
        if (button == 1) {
            itm.setSelected(!itm.isSelected());
            super.itemclick(itm, button);
        }
    }

    @Override
    protected CheckListBoxItem listitem(int idx) {
        return (items.get(idx));
    }

    @Override
    protected int listitems() {
        return items.size();
    }

    @Override
    public void drawbg(GOut g) {
        g.chcolor(0, 0, 0, 128);
        g.frect(Coord.z, sz.sub(0, toggle.sz.y + 3));
        g.chcolor();
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == toggle) {
            data.setAll(!data.anySelected());
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

    @Override
    protected void drawitem(GOut g, CheckListBoxItem itm, int idx) {
        if (itm.isSelected()) {
            g.image(chk, new Coord(sz.x - sb.sz.x - chk.sz().x - 3, -1), new Coord(itemh, itemh));
        }
        Text t = Text.render(itm.id);
        Tex T = t.tex();
        g.image(T, new Coord(2, 2), t.sz());
        T.dispose();
    }
}