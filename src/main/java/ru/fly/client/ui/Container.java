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

package ru.fly.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import ru.fly.client.F;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * User: fil
 * Date: 03.09.13
 * Time: 22:00
 */
public class Container extends Component implements HasWidgets {

    private WidgetCollection widgets;

    public Container(Element el) {
        super(el);
        widgets = new WidgetCollection(this);
    }

    public WidgetCollection getWidgets(){
        return widgets;
    }

    public int getWidgetsCount(){
        return widgets.size();
    }

    @Override
    public void add(Widget w) {
        widgets.add(w);
        if(isAttached())
            F.render(this, w);
    }

    public void insert(Widget w, int idx){
        widgets.insert(w, idx);
        if(isAttached())
            F.render(this, w, idx);
    }

    public void addAll(List<? extends Widget> childs) {
        for(Widget w : childs){
            add(w);
        }
    }

    @Override
    public void clear() {
        Iterator<Widget> it = iterator();
        while(it.hasNext() && it.next() != null){
            it.remove();
        };
    }

    @Override
    public Iterator<Widget> iterator() {
        return widgets.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        widgets.remove(w);
        if(w.isAttached())
            F.erase(this, w);
        return true;
    }

    public void removeAll(Collection<Widget> list){
        for(Widget w : list){
            remove(w);
        }
    }

    protected void doAttachChild(Widget w){
        F.render(this, w);
    }

    @Override
    protected void doAttachChildren() {
        super.doAttachChildren();
        for(Widget w : widgets){
            doAttachChild(w);
        }
    }

    @Override
    protected void doDetachChildren() {
        for(Widget w : widgets){
            if(w.isAttached())
                F.erase(this, w);
        }
        super.doDetachChildren();
    }

    public FElement getContainerElement(){
        return getElement();
    }
}
