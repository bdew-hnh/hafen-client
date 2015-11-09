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

public class UIIntercept {
    public static Widget newWidget(final Widget widget, Widget parent, String type, Object[] cargs, Object[] pargs) {
        if (type.equals("ui/surv:37")) {
            final Field ftz;
            try {
                ftz = widget.getClass().getDeclaredField("tz");
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return widget;
            }
            ftz.setAccessible(true);

            widget.add(new DynLabel(() -> {
                try {
                    return "Level: " + (int) ftz.get(widget);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return "ERROR";
                }
            }), 100, 0);
            return widget;
        } else if (type.equals("wnd") && cargs.length == 2 && cargs[1].equals("Table")) {
            widget.add(new TableCalc(widget), 105, 0);
        }
        return widget;
    }
}
