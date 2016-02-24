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

import javax.media.opengl.GL2;

public class MoveLine implements Rendered {
    private final Gob gob;

    public MoveLine(Gob gob) {
        this.gob = gob;
    }

    @Override
    public boolean setup(RenderList r) {
        if (!Config.moveLines.enabled) return false;
        try {
            r.prepo(States.vertexcolor);
            Location.goback(r.state(), "gobx");
            r.prepo(States.xray);
        } catch (Exception e) {
            System.err.println("Exception in MoveLine.setup");
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }

    @Override
    public void draw(GOut g) {
        Moving m = gob.getattr(Moving.class);
        if (m != null) {
            if (m instanceof LinMove) {
                g.apply();
                BGL gl = g.gl;
                Coord t = ((LinMove) m).t;
                Coord3f p = new Coord3f(t.x, t.y, gob.glob.map.getcz(t.x, t.y)).sub(gob.getc());
                gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
                gl.glLineWidth(2F);
                gl.glColor4f(1, 0, 0, 1);
                gl.glBegin(GL2.GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(p.x, -p.y, p.z);
                gl.glEnd();
            } else if (m instanceof Homing) {
                g.apply();
                BGL gl = g.gl;
                Gob tgt = gob.glob.oc.getgob(((Homing) m).tgt);
                if (tgt != null) {
                    Coord t = tgt.rc;
                    Coord3f p = new Coord3f(t.x, t.y, gob.glob.map.getcz(t.x, t.y)).sub(gob.getc());
                    gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
                    gl.glLineWidth(2F);
                    gl.glColor4f(1, 0, 1, 1);
                    gl.glBegin(GL2.GL_LINES);
                    gl.glVertex3f(0, 0, 0);
                    gl.glVertex3f(p.x, -p.y, p.z);
                    gl.glEnd();
                }
            }
        }
    }
}
