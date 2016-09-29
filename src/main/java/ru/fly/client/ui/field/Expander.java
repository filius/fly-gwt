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

package ru.fly.client.ui.field;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import ru.fly.client.ui.FElement;

import java.util.List;

/**
 * @author fil
 */
public abstract class Expander {

    private boolean expanded = false;
    private HandlerRegistration hReg;
    private FElement element;

    public Expander(FElement element){
        this.element = element;
    }

    private void addGlobalHideListener(){
        if (hReg != null) {
            removeGlobalHideListener();
        }
        hReg = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                switch (event.getTypeInt()) {
                    case Event.ONSCROLL:
                    case Event.ONCLICK:
                        Element target = event.getNativeEvent().getEventTarget().cast();
                        boolean outside = !element.isOrHasChild(target);
                        Element expandElement = getExpandedElement();
                        if (expandElement != null) {
                            outside &= !expandElement.isOrHasChild(target);
                        }
                        List<FElement> expElements = getExpandedElements();
                        if (expElements != null) {
                            for (Element expElement : expElements) {
                                outside &= !expElement.isOrHasChild(target);
                            }
                        }
                        if (outside) {
                            collapse();
                        }
                }
            }
        });
    }

    private void removeGlobalHideListener(){
        if(hReg != null){
            hReg.removeHandler();
            hReg = null;
        }
    }

    public void expandCollapse(){
        if(expanded){
            collapse();
        }else{
            expand(false);
        }
    }

    public void expand(boolean force){
        if(!isEnabled() || (expanded && !force))
            return;
        expanded = true;
        addGlobalHideListener();
        onExpand();
    }

    public void collapse(){
        expanded = false;
        removeGlobalHideListener();
        onCollapse();
    }

    public boolean isExpanded(){
        return expanded;
    }

    /**
     * @return - expanded element
     * @deprecated - use {@see getExpandedElements}
     */
    protected abstract FElement getExpandedElement();

    protected List<FElement> getExpandedElements() {
        return null;
    }

    public abstract void onExpand();

    public abstract void onCollapse();

    public abstract boolean isEnabled();

}
