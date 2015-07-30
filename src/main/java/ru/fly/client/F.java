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

package ru.fly.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.CommonDecor;
import ru.fly.client.ui.Container;

/**
 * User: fil
 * Date: 22.08.13
 * Time: 18:56
 */
public class F {

    public static final CommonDecor commonDecor = GWT.create(CommonDecor.class);
    private static int uid = -1;
    private static int tabIdx = -1;

    /**
     * вернет host + контекст приложения.
     * например: http://www.host.com/appname/
     * @return
     */
    public static String getHostAndContext(){
        String url = GWT.getHostPageBaseURL();
        String moduleName = GWT.getModuleName();
        return url.replace(moduleName+"/", "");
    }

    public static int getNextTabIdx(){
        tabIdx++;
        return tabIdx;
    }

    public static String getUID(){
        uid++;
        return "fly-"+uid;
    }

    public static void erase(Widget parent, Widget widget){
        if(widget == null)
            throw new IllegalArgumentException("widget must not be null");
        if(parent instanceof Container)
            ((Container)parent).getContainerElement().removeChild(widget.getElement());
        else
            parent.getElement().removeChild(widget.getElement());
        setParent(widget, null);
    }

    public static void render(Widget parent, Widget widget){
        render(parent, widget, null);
    }

    public static void render(Widget parent, Widget widget, Integer idx){
        if(parent == null)
            throw new IllegalArgumentException("parent must not be null");
        if(widget == null)
            throw new IllegalArgumentException("widget must not be null");
        Element parentContainer = (parent instanceof Container)
                ? ((Container)parent).getContainerElement() : parent.getElement();
        if(idx != null){
            insertElement(parentContainer, widget.getElement(), idx);
        }else {
            parentContainer.appendChild(widget.getElement());
        }
        setParent(widget, parent);
    }

    private static native void setParent(Widget w, Widget parent)/*-{
        w.@com.google.gwt.user.client.ui.Widget::setParent(Lcom/google/gwt/user/client/ui/Widget;)(parent);
    }-*/;

    public static native void attach(Widget w)/*-{
        w.@com.google.gwt.user.client.ui.Widget::onAttach()();
    }-*/;

//    private static native void onDetach(Widget w)/*-{
//        w.@com.google.gwt.user.client.ui.Widget::onDetach()()
//    }-*/;

    public static void setEnableTextSelection(Element e, boolean enabled){
        if(enabled){
            e.removeClassName(commonDecor.css().disableTextSelection());
        }else{
            e.addClassName(commonDecor.css().disableTextSelection());
        }
        e.setPropertyString("unselectable", enabled ? "" : "on");
        setEnableTextSelectionEvent(e, enabled);
    }

    private native static void setEnableTextSelectionEvent(Element e, boolean enabled)/*-{
        if (enabled) {
            e.ondrag = null;
            e.onselectstart = null;
            e.style.MozUserSelect="text"
        } else {
            e.ondrag = function () { return false; };
            e.onselectstart = function () { return false; };
            e.style.MozUserSelect="none"
        }
    }-*/;

    /** enable/disable page text selection by double click */
    public native static void setEnableDblClickSelection(boolean enabled)/*-{
        if(enabled) {
            $doc.ondblclick = null;
        }else{
            $doc.ondblclick = function () {
                if ($wnd.getSelection) {
                    $wnd.getSelection().removeAllRanges();
                } else if ($doc.selection) {
                    $doc.selection.empty();
                }
            }
        }
    }-*/;

    /** clear selection of text */
    public native static void clearSelection()/*-{
        if ($wnd.getSelection) {
            $wnd.getSelection().removeAllRanges();
        } else if ($doc.selection) {
            $doc.selection.empty();
        }
    }-*/;

    /**
     * опредилит наследует ли один класс, другой
     * @param to - потомок
     * @param from - предок
     * @return - TRUE если to является потомком from
     */
    public static boolean isAssignable(Class to, Class from){
        return to != null && (to.getName().equals(from.getName()) || isAssignable(to.getSuperclass(), from));
    }

    public static boolean isWidgetVisible(Widget w){
        return w == null || (w.isVisible() && isWidgetVisible(w.getParent()));
    }

    public static void insertElement(Element parent, Element el, int idx){
        if(idx == 0){
            parent.insertFirst(el);
        }else if(idx < parent.getChildCount()) {
            parent.insertBefore(el, parent.getChild(idx));
        } else{
            parent.appendChild(el);
        }
    }

    public static native Integer asInteger(final String value)/*-{
        var number = parseInt(value, 10);
        if (isNaN(number)) {
            return null;
        } else {
            return @java.lang.Integer::valueOf(I)(number);
        }
    }-*/;

}
