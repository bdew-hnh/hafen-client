/*
 *  This file is part of the Haven & Hearth game client.
 *  Copyright (C) 2009 Fredrik Tolf <fredrik@dolda2000.com>, and
 *                     Björn Johannessen <johannessen.bjorn@gmail.com>
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

import java.awt.*;
import java.awt.event.KeyEvent;

public class RootWidget extends ConsoleHost {
    public static final Resource defcurs = Resource.local().loadwait("gfx/hud/curs/arw");
    Profile guprof, grprof, ggprof;
    boolean afk = false;
	
    public RootWidget(UI ui, Coord sz) {
	super(ui, new Coord(0, 0), sz);
	setfocusctl(true);
	cursor = defcurs.indir();
    }
	
    public boolean globtype(char key, KeyEvent ev) {
	if(!super.globtype(key, ev)) {
	    if(key == '`') {
		GameUI gi = findchild(GameUI.class);
		if(Config.profile) {
		    add(new Profwnd(guprof, "UI profile"), new Coord(100, 100));
		    add(new Profwnd(grprof, "GL profile"), new Coord(450, 100));
		    if((gi != null) && (gi.map != null))
			add(new Profwnd(gi.map.prof, "Map profile"), new Coord(100, 250));
		}
		if(Config.profilegpu) {
		    add(new Profwnd(ggprof, "GPU profile"), new Coord(450, 250));
		}
	    } else if(key == ':') {
			entercmd();
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_N) {
			Config.nightVision.toggle();
			if (Config.nightVision.isEnabled())
				ui.message("Night Vision ENABLED", Color.green);
			else
				ui.message("Night Vision DISABLED", Color.red);
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_W) {
			Config.worldToolTips.toggle();
			if (Config.worldToolTips.isEnabled())
				ui.message("World Tooltip ENABLED", Color.green);
			else
				ui.message("World Tooltop DISABLED", Color.red);
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_G) {
			Config.showGrid.toggle();
			if (Config.showGrid.isEnabled())
				ui.message("Grid Overlay ENABLED", Color.green);
			else
				ui.message("Grid Overlay DISABLED", Color.red);
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_F) {
			if (ui.gui!=null) {
				ui.gui.map.startMouseWalking(true);
				ui.message("Mouse follow mode ENABLED - click to disable", Color.white);
			}
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_V) {
			if (ui.gui!=null)
				ui.gui.viewfilter.show(!ui.gui.viewfilter.visible);
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_D) {
			Config.showObjectRadius.toggle();
			if (Config.showObjectRadius.isEnabled())
				ui.message("Object Radius ENABLED", Color.green);
			else
				ui.message("Object Radius DISABLED", Color.red);
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_Q) {
			if (ui.gui!=null)
				ui.gui.swapHand();
		} else if(ev.isControlDown() && ev.getKeyCode() == KeyEvent.VK_X) {
			if (ui.gui!=null)
				ui.gui.act("lo");
	    } else if(key != 0) {
		wdgmsg("gk", (int)key);
	    }
	}
	return(true);
    }

    public void draw(GOut g) {
	super.draw(g);
	drawcmd(g, new Coord(20, sz.y - 20));
    }
    
    public void error(String msg) {
    }
}
