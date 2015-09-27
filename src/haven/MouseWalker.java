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

public class MouseWalker implements MapView.Grabber {
	static final int TICKSPEED = 200;
	private final UI.Grab mgrab;
	private final MapView mv;
	private Coord mousePos;
	private long lastTick;

	public MouseWalker(MapView mv, Boolean now) {
		this.mv = mv;
		mousePos = mv.lastMouse;
		mgrab = mv.ui.grabmouse(mv);
		if (now)
			lastTick = Long.MIN_VALUE;
		else
			lastTick = System.currentTimeMillis();
	}

	public void tick() {
		if (lastTick + TICKSPEED < System.currentTimeMillis()) {
			lastTick = System.currentTimeMillis();
			mv.delay(mv.new Click(mousePos, 1, true));
		}
	}

	@Override
	public boolean mmousedown(Coord mc, int button) {
		return button == 1;
	}

	@Override
	public boolean mmouseup(Coord mc, int button) {
		synchronized (mv) {
			if (button == 1) {
				mv.stopMouseWalking();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean mmousewheel(Coord mc, int amount) {
		return (false);
	}

	@Override
	public void mmousemove(Coord mc) {
		mousePos = mc;
	}

	public void remove() {
		mgrab.remove();
	}
}
