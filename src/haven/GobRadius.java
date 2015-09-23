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

import haven.States.ColState;
import haven.VertexBuf.NormalArray;
import haven.VertexBuf.VertexArray;

import java.awt.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class GobRadius implements Rendered {
    static final GLState smat = new ColState(new Color(192, 0, 0, 80));
    final VertexArray posa;
    final NormalArray nrma;
    final ShortBuffer sidx;

    public GobRadius(float rad) {
        int per = Math.max(24, (int) (2 * Math.PI * (double) rad / 11.0D));
        FloatBuffer pa = Utils.mkfbuf(per * 3 * 2);
        FloatBuffer na = Utils.mkfbuf(per * 3 * 2);
        ShortBuffer sa = Utils.mksbuf(per * 6);

        for (int i = 0; i < per; ++i) {
            float s = (float) Math.sin(2 * Math.PI * (double) i / (double) per);
            float c = (float) Math.cos(2 * Math.PI * (double) i / (double) per);
            pa.put(i * 3, c * rad).put(i * 3 + 1, s * rad).put(i * 3 + 2, 10.0F);
            pa.put((per + i) * 3, c * rad).put((per + i) * 3 + 1, s * rad).put((per + i) * 3 + 2, -10.0F);
            na.put(i * 3, c).put(i * 3 + 1, s).put(i * 3 + 2, 0.0F);
            na.put((per + i) * 3, c).put((per + i) * 3 + 1, s).put((per + i) * 3 + 2, 0.0F);
            int v = i * 6;
            sa.put(v, (short) i).put(v + 1, (short) (i + per)).put(v + 2, (short) ((i + 1) % per));
            sa.put(v + 3, (short) (i + per)).put(v + 4, (short) ((i + 1) % per + per)).put(v + 5, (short) ((i + 1) % per));
        }

        this.posa = new VertexArray(pa);
        this.nrma = new NormalArray(na);
        this.sidx = sa;
    }

    public boolean setup(RenderList rl) {
        if (!Config.showObjectRadius.enabled) return false;
        rl.prepo(Rendered.eyesort);
        rl.prepo(Material.nofacecull);
        Location.goback(rl.state(), "gobx");
        rl.state().put(States.color, null);
        return true;
    }

    public void draw(GOut g) {
        g.state(smat);
        g.apply();
        this.posa.bind(g, false);
        this.nrma.bind(g, false);
        this.sidx.rewind();
        g.gl.glDrawElements(4, this.sidx.capacity(), 5123, this.sidx);
        this.posa.unbind(g);
        this.nrma.unbind(g);
    }
}
