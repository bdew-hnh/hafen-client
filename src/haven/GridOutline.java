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
import java.nio.FloatBuffer;

public class GridOutline implements Rendered {
    private final MCache map;
    private final int area;
    private final Coord size;
    private final States.ColState color;
    private Coord ul;
    private Buffer buffer;

    static class Buffer {
        public FloatBuffer vertex;
        public FloatBuffer color;
        public Location location;

        public Buffer(int area, Location location) {
            vertex = Utils.mkfbuf(area * 3 * 4);
            color = Utils.mkfbuf(area * 4 * 4);
            this.location = location;
        }

        void rewind() {
            vertex.rewind();
            color.rewind();
        }

        void putColor(float r, float g, float b, float a) {
            color.put(r).put(g).put(b).put(a);
        }

        void putVertex(float x, float y, float z) {
            vertex.put(x).put(y).put(z);
        }
    }

    public GridOutline(MCache map, Coord size) {
        this.map = map;
        this.size = size;
        this.area = (size.x + 1) * (size.y + 1);
        this.color = new States.ColState(255, 255, 255, 64);
    }

    @Override
    public void draw(GOut g) {
        g.apply();
        BGL gl = g.gl;
        buffer.rewind();
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glLineWidth(2F);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, buffer.vertex);
        gl.glColorPointer(4, GL2.GL_FLOAT, 0, buffer.color);
        gl.glDrawArrays(GL2.GL_LINES, 0, area * 4);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public boolean setup(RenderList rl) {
        if (buffer != null) {
            rl.prepo(color);
            rl.prepo(buffer.location);
            return true;
        } else {
            return false;
        }
    }

    public void update(Coord ul) {
        try {
            Buffer nb = new Buffer(area, Location.xlate(new Coord3f(ul.x * MCache.tilesz.x, -ul.y * MCache.tilesz.y, 0.0F)));
            this.ul = ul;
            Coord c = new Coord();
            for (c.y = ul.y; c.y <= ul.y + size.y; c.y++)
                for (c.x = ul.x; c.x <= ul.x + size.x; c.x++)
                    addLineStrip(nb, mapToScreen(c), mapToScreen(c.add(1, 0)), mapToScreen(c.add(1, 1)));
            buffer = nb;
        } catch (Loading ignored) {
        }
    }

    private Coord3f mapToScreen(Coord c) {
        return new Coord3f((c.x - ul.x) * MCache.tilesz.x, -(c.y - ul.y) * MCache.tilesz.y, map.getz(c));
    }

    private void addLineStrip(Buffer buf, Coord3f... vertices) {
        for (int i = 0; i < vertices.length - 1; i++) {
            Coord3f a = vertices[i];
            Coord3f b = vertices[i + 1];
            buf.putVertex(a.x, a.y, a.z + 0.1F);
            buf.putVertex(b.x, b.y, b.z + 0.1F);
            if (a.z == b.z) {
                buf.putColor(0F, 1F, 0F, 0.5F);
                buf.putColor(0F, 1F, 0F, 0.5F);
            } else {
                buf.putColor(1F, 0F, 0F, 0.5F);
                buf.putColor(1F, 0F, 0F, 0.5F);
            }
        }
    }
}
