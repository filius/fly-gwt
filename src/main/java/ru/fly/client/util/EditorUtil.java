/*
 * Copyright 2015 Valeriy Filatov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.fly.client.util;

import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.Container;
import ru.fly.client.ui.field.Field;
import ru.fly.client.ui.field.label.FieldLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 15.08.13
 * Time: 0:44
 */
public class EditorUtil {

    private static final String PERM_DISABLED = "perm_disabled";

    public static List<LeafValueEditor<?>> getEditors(Widget widget){
        List<LeafValueEditor<?>> ret = new ArrayList<LeafValueEditor<?>>();
        if(widget instanceof LeafValueEditor){
            ret.add((LeafValueEditor) widget);
        }else if(widget instanceof Container){
            for(Widget w : ((Container) widget).getWidgets()){
                List<LeafValueEditor<?>> f = getEditors(w);
                if(f != null)
                    ret.addAll(f);
            }
        }else if(widget instanceof ComplexPanel){
            ComplexPanel panel = (ComplexPanel)widget;
            for(int i=0; i<panel.getWidgetCount(); i++){
                List<LeafValueEditor<?>> f = getEditors(panel.getWidget(i));
                if(f != null)
                    ret.addAll(f);
            }
        }else if(widget instanceof FieldLabel){
            Widget w = ((FieldLabel)widget).getField();
            List<LeafValueEditor<?>> f = getEditors(w);
            if(f != null)
                ret.addAll(f);
        }
        return ret;
    }

    public static void setPermDisabled(Component c){
        c.setData(PERM_DISABLED, PERM_DISABLED);
        c.disable();
    }

    public static boolean isPermDisabled(Component c){
        return PERM_DISABLED.equals(c.getData(PERM_DISABLED));
    }


    public static void clearFields(Widget form){
        List<LeafValueEditor<?>> editors = EditorUtil.getEditors(form);
        for(LeafValueEditor<?> e : editors){
            if(e instanceof Field){
                ((Field) e).clear();
            }
        }
    }

    public static void setEnabled(Widget form, boolean enabled){
        List<LeafValueEditor<?>> editors = EditorUtil.getEditors(form);
        for(LeafValueEditor<?> e : editors){
            if(e instanceof Component) {
                Component c = (Component) e;
                if (enabled && !EditorUtil.isPermDisabled(c))
                    c.enable();
                else
                    c.disable();
            }
        }
    }

}
