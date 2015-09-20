package haven;

import javax.media.opengl.GL2;
import java.nio.FloatBuffer;

public class GridOutline implements Rendered {
    private final MCache map;
    private final Buffer[] buffers;
    private final int area;
    private final Coord size;
    private final States.ColState color;
    private Location location;
    private Coord ul;
    private int curIndex;

    static class Buffer {
        public FloatBuffer vertex;
        public FloatBuffer color;

        public Buffer(int area) {
            vertex = Utils.mkfbuf(area * 3 * 4);
            color = Utils.mkfbuf(area * 4 * 4);
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

        // double-buffer to prevent flickering
        buffers = new Buffer[2];
        for (int i = 0; i < buffers.length; i++)
            buffers[i] = new Buffer(area);
        curIndex = 0;
    }

    @Override
    public void draw(GOut g) {
        g.apply();
        BGL gl = g.gl;
        Buffer buf = getActiveBuffer();
        buf.rewind();
        gl.glHint(GL2.GL_LINE_SMOOTH_HINT, GL2.GL_NICEST);
        gl.glLineWidth(2F);
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_COLOR_ARRAY);
        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, buf.vertex);
        gl.glColorPointer(4, GL2.GL_FLOAT, 0, buf.color);
        gl.glDrawArrays(GL2.GL_LINES, 0, area * 4);
        gl.glDisableClientState(GL2.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @Override
    public boolean setup(RenderList rl) {
        if (location != null) {
            rl.prepo(color);
            rl.prepo(location);
            return true;
        } else {
            return false;
        }
    }

    public void update(Coord ul) {
        try {
            this.ul = ul;
            this.location = Location.xlate(new Coord3f(ul.x * MCache.tilesz.x, -ul.y * MCache.tilesz.y, 0.0F));
            Coord c = new Coord();
            for (c.y = ul.y; c.y <= ul.y + size.y; c.y++)
                for (c.x = ul.x; c.x <= ul.x + size.x; c.x++)
                    addLineStrip(mapToScreen(c), mapToScreen(c.add(1, 0)), mapToScreen(c.add(1, 1)));
            swapBuffers();
        } catch (Loading ignored) {
        }
    }

    private Coord3f mapToScreen(Coord c) {
        return new Coord3f((c.x - ul.x) * MCache.tilesz.x, -(c.y - ul.y) * MCache.tilesz.y, map.getz(c));
    }

    private void addLineStrip(Coord3f... vertices) {
        Buffer buf = getOtherBuffer();
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

    private Buffer getActiveBuffer() {
        return buffers[curIndex];
    }

    private Buffer getOtherBuffer() {
        return buffers[1 - curIndex];
    }

    private void swapBuffers() {
        curIndex = 1 - curIndex;
    }
}
