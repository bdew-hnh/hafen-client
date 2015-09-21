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

import java.awt.*;

public class AttnMeter extends Widget {
    private static final Tex bg = Resource.loadtex("bdew/gfx/hud/meter/attention");

    private final CharWnd.StudyInfo study;

    public AttnMeter(CharWnd.StudyInfo study) {
        super(IMeter.fsz);
        this.study = study;
    }

    @Override
    public void draw(GOut g) {
        Coord isz = IMeter.msz;
        Coord off = IMeter.off;
        g.chcolor(0, 0, 0, 255);
        g.frect(off, isz);
        g.chcolor();

        study.upd();

        int w = isz.x;
        int c = study.tw;
        int t = ui.sess.glob.cattr.get("int").comp;

        if (c < t) {
            g.chcolor(Color.YELLOW);
        } else if (c == t) {
            g.chcolor(Color.GREEN);
        } else {
            g.chcolor(Color.RED);
        }

        g.frect(off, new Coord((int) Math.floor((float) c / t * w), isz.y));
        g.chcolor();
        g.image(bg, Coord.z);
    }

    @Override
    public Object tooltip(Coord c, Widget prev) {
        return RichText.render(String.format("Attention: %d/%d\nXP Cost: %d\nLP Gain: %d", study.tw, ui.sess.glob.cattr.get("int").comp, study.tenc, study.texp), -1).tex();
    }
}
