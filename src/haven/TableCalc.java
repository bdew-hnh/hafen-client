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

import java.lang.reflect.Field;

public class TableCalc extends Widget {
    private Widget table;

    private Class<? extends ItemInfo> gClass;
    private Field fGlut;
    private Field fFev;

    private final Label lFep;
    private final Label lSat;

    public TableCalc(Widget table) {
        this.table = table;
        add(new Label("Sat. Bonus:"), 0, 0);
        add(new Label("FEP Bonus:"), 0, 40);
        lSat = add(new Label("XXXX.XX%"), 20, 20);
        lFep = add(new Label("XXXX.XX%"), 20, 60);
        pack();
    }

    @Override
    public void tick(double dt) {
        super.tick(dt);
        update();
    }

    private void update() {
        try {
            double glut = 1;
            double fev = 1;
            for (GItem gi : table.findAll(GItem.class)) {
                for (ItemInfo info : gi.info()) {
                    if (gClass == null && info.getClass().getName().equals("Gast")) {
                        gClass = info.getClass();
                        fGlut = gClass.getDeclaredField("glut");
                        fFev = gClass.getDeclaredField("fev");
                    }
                    if (gClass != null && gClass.isInstance(info)) {
                        glut *= (Double) fGlut.get(info);
                        fev *= (Double) fFev.get(info);
                    }
                }
            }
            lSat.settext(String.format("%.2f%%", (1 - glut) * 100));
            lFep.settext(String.format("%.2f%%", (fev - 1) * 100));
        } catch (Loading ignored) {
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
