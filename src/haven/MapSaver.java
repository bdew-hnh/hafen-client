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

import haven.resutil.Ridges;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MapSaver {
    private UI ui;
    private Coord lastCoord;
    private String session;
    private File mapDir;
    private File sessDir;
    private FileWriter fpWriter;

    public MapSaver(UI ui) {
        this.ui = ui;
        mapDir = new File("map");
        mapDir.mkdirs();
        try {
            fpWriter = new FileWriter(mapFile("fingerprints.txt"), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File mapFile(String name) {
        return new File(mapDir, name);
    }

    private File sessionFile(String name) {
        return new File(sessDir, name);
    }


    public void newSession() {
        session = (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date(System.currentTimeMillis()));
        sessDir = mapFile(session);

        try (FileWriter f = new FileWriter(new File(mapDir, "currentsession.js"))) {
            f.write("var currentSession = '" + session + "';\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ImageAndFingerprint {
        public BufferedImage im;
        public long fp;

        public ImageAndFingerprint(BufferedImage im, long fp) {
            this.im = im;
            this.fp = fp;
        }
    }

    private BufferedImage getTileImage(int t) {
        Resource r = ui.sess.glob.map.tilesetr(t);
        if (r == null) return null;
        Resource.Image ir = r.layer(Resource.imgc);
        if (ir == null) return null;
        return ir.img;
    }

    // Modified version of LocalMinimap.drawmap
    private ImageAndFingerprint drawMapImage(MCache m, MCache.Grid g, Coord ul) {
        BufferedImage[] texes = new BufferedImage[256];
        BufferedImage buf = TexI.mkbuf(MCache.cmaps);
        Coord c = new Coord();
        Coord sz = MCache.cmaps;

        long h = 1125899906842597L;

        for (c.y = 0; c.y < sz.y; c.y++) {
            for (c.x = 0; c.x < sz.x; c.x++) {
                int t = g.gettile(c);
                h = h * 31 + t;
                int rgb = 0;
                BufferedImage tex = texes[t];
                if (tex == null)
                    texes[t] = tex = getTileImage(t);
                if (tex != null)
                    rgb = tex.getRGB(Utils.floormod(c.x + ul.x, tex.getWidth()), Utils.floormod(c.y + ul.y, tex.getHeight()));
                buf.setRGB(c.x, c.y, rgb);
            }
        }
        for (c.y = 1; c.y < sz.y - 1; c.y++) {
            for (c.x = 1; c.x < sz.x - 1; c.x++) {
                int t = g.gettile(c);
                Tiler tl = m.tiler(t);
                if (tl instanceof Ridges.RidgeTile) {
                    if (Ridges.brokenp(m, ul.add(c))) {
                        for (int y = c.y - 1; y <= c.y + 1; y++) {
                            for (int x = c.x - 1; x <= c.x + 1; x++) {
                                Color cc = new Color(buf.getRGB(x, y));
                                buf.setRGB(x, y, Utils.blendcol(cc, Color.BLACK, ((x == c.x) && (y == c.y)) ? 1 : 0.1).getRGB());
                            }
                        }
                    }
                }
            }
        }
        for (c.y = 0; c.y < sz.y; c.y++) {
            for (c.x = 0; c.x < sz.x; c.x++) {
                int t = g.gettile(c);
                if (((c.x > 0) && (g.gettile(c.add(-1, 0)) > t)) ||
                        ((c.x < sz.x - 1) && (g.gettile(c.add(1, 0)) > t)) ||
                        ((c.y > 0) && (g.gettile(c.add(0, -1)) > t)) ||
                        ((c.y < sz.y - 1) && (g.gettile(c.add(0, 1)) > t)))
                    buf.setRGB(c.x, c.y, Color.BLACK.getRGB());
            }
        }
        return new ImageAndFingerprint(buf, h);
    }

    public void recordMapTile(final MCache m, final MCache.Grid g, final Coord c) {
        if (lastCoord == null || Math.abs(lastCoord.sub(c).x) > 5 || Math.abs(lastCoord.sub(c).y) > 5)
            newSession();
        lastCoord = c;
        try {
            ImageAndFingerprint res = drawMapImage(m, g, c.mul(MCache.cmaps));
            String fileName = String.format("tile_%d_%d.png", c.x, c.y);
            try {
                sessDir.mkdirs();
                ImageIO.write(res.im, "png", sessionFile(fileName));
                fpWriter.write(String.format("%s/%s:%s\n", session, fileName, Long.toHexString(res.fp)));
                fpWriter.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Loading e) {
            // This is a horrible hack, the first map packets will come in before
            // the resources needed to draw them are ready, so we schedule it to be called
            // again and sleep for a bit. There must be a better way to do this
            System.out.println("Map not ready, waiting...");
            Defer.later(new Defer.Callable<Void>() {
                @Override
                public Void call() throws InterruptedException {
                    Thread.sleep(500);
                    recordMapTile(m, g, c);
                    return null;
                }
            });
        }
    }
}