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
import java.awt.image.BufferedImage;

public class GobInfo extends GAttrib {
    private GobInfoSprite infoSprite;

    static class GobInfoSprite extends Sprite {
        private Tex tex;
        private Gob gob;
        private Coord wndsz;
        private Projection proj;
        private Location.Chain loc;
        private Camera camera;

        public GobInfoSprite(Gob gob, Tex tex) {
            super(null, null);
            this.gob = gob;
            this.tex = tex;
        }

        @Override
        public boolean setup(RenderList r) {
            if (gob != null) {
                r.prepo(last);
                GLState.Buffer buf = r.state();
                proj = r.state().get(PView.proj);
                wndsz = r.state().get(PView.wnd).sz();
                loc = buf.get(PView.loc);
                camera = buf.get(PView.cam);

                return true;
            } else return false;
        }

        public void draw(GOut g) {
            Matrix4f mv = new Matrix4f();
            Matrix4f cam = new Matrix4f();
            Matrix4f wxf = new Matrix4f();
            mv.load(cam.load(camera.fin(Matrix4f.id))).mul1(wxf.load(loc.fin(Matrix4f.id)));
            Coord3f s = proj.toscreen(mv.mul4(Coord3f.o), wndsz);
            Coord c = new Coord((int) s.x, (int) s.y - 20);
            g.aimage(tex, c, 0.5, 0.5);
        }
    }

    private static GobInfoSprite nullSprite = new GobInfoSprite(null, null);

    public GobInfo(Gob gob, Tex tex) {
        super(gob);
        if (tex != null)
            infoSprite = new GobInfoSprite(gob, tex);
        else
            infoSprite = nullSprite;
    }

    public GobInfoSprite draw() {
        return infoSprite;
    }

    public static GobInfo get(Gob gob) {
        if (gob == null || gob.getres() == null) return new GobInfo(gob, null);
        Text.Line line = null;
        if (Config.showPlantGrowth.isEnabled()) {
            try {
                if (isSpriteKind("GrowingPlant", gob) || isSpriteKind("TrellisPlant", gob)) {
                    int maxStage = 0;
                    for (FastMesh.MeshRes layer : gob.getres().layers(FastMesh.MeshRes.class)) {
                        if (layer.id / 10 > maxStage)
                            maxStage = layer.id / 10;
                    }
                    Message data = getDrawableData(gob);
                    if (data != null) {
                        int stage = data.uint8();
                        if (stage >= maxStage) {
                            line = Text.std.renderstroked(String.format("%d/%d", stage, maxStage), Color.GREEN, Color.BLACK);
                        } else {
                            line = Text.std.renderstroked(String.format("%d/%d", stage, maxStage), Color.YELLOW, Color.BLACK);
                        }
                    }
                } else if (isSpriteKind("Tree", gob)) {
                    Message data = getDrawableData(gob);
                    if (data != null && !data.eom()) {
                        int growth = data.uint8();
                        if (growth < 100)
                            line = Text.std.renderstroked(String.format("%d%%", growth), Color.YELLOW, Color.BLACK);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (line == null && Config.showObjectDamage.isEnabled()) {
            GobHealth hp = gob.getattr(GobHealth.class);
            if (hp != null && hp.hp < 4)
                line = Text.std.renderstroked(String.format("%.0f%%", (1f - hp.hp / 4f) * 100f), Color.RED, Color.BLACK);
        }
        GobQuality gq = gob.getattr(GobQuality.class);
        if (line != null)
            if (gq != null)
                return new GobInfo(gob, combine(line.tex(), gq.draw().tex()));
            else
                return new GobInfo(gob, line.tex());
        else if (gq != null)
            return new GobInfo(gob, gq.draw().tex());
        else
            return new GobInfo(gob, null);
    }

    private static Message getDrawableData(Gob gob) {
        Drawable dr = gob.getattr(Drawable.class);
        ResDrawable d = (dr instanceof ResDrawable) ? (ResDrawable) dr : null;
        if (d != null)
            return d.sdt.clone();
        else
            return null;
    }

    private static boolean isSpriteKind(String kind, Gob gob) {
        Resource.CodeEntry ce = gob.getres().layer(Resource.CodeEntry.class);
        if (ce != null) {
            Class spc = ce.getClassByTag("spr", false);
            return spc != null && (spc.getSimpleName().equals(kind) || spc.getSuperclass().getSimpleName().equals(kind));
        } else {
            return false;
        }
    }

    private static TexI combine(Tex a, Tex b) {
        if (!(a instanceof TexI) || !(b instanceof TexI))
            throw new RuntimeException("Combine only works with TexI instances");
        BufferedImage buff = TexI.mkbuf(new Coord(Math.max(a.sz().x, b.sz().x), a.sz().y + b.sz().y));
        Graphics g = buff.getGraphics();
        g.drawImage(((TexI) a).back, (buff.getWidth() - a.sz().x) / 2, 0, null);
        g.drawImage(((TexI) b).back, (buff.getWidth() - b.sz().x) / 2, a.sz().y, null);
        return new TexI(buff);
    }
}
