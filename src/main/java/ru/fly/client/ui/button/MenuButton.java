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

package ru.fly.client.ui.button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.button.decor.BtnDecor;
import ru.fly.client.ui.field.Expander;
import ru.fly.client.ui.toolbar.Menu;

/**
 * @author fil
 */
public class MenuButton extends Button{

    private final BtnDecor decor = GWT.create(BtnDecor.class);

    private Expander expander;
    private Menu menu;

    public MenuButton(String text, final Menu menu) {
        super(text);
        addClickHandler(new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                expander.expandCollapse();
            }
        });
        this.menu = menu;
        menu.addHandler(new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                expander.collapse();
            }
        }, ClickEvent.getType());
        expander = new Expander(getElement()) {
            @Override
            protected FElement getExpandedElement() {
                return menu.getElement();
            }

            @Override
            public void onExpand() {
                menu.getStyle().setPosition(Style.Position.ABSOLUTE);
                menu.setPosition(-10000, -10000);
                menu.getStyle().clearWidth();
                RootPanel.get().add(menu);
                updatePositionAndSize();
            }

            @Override
            public void onCollapse() {
                if(menu.isAttached())
                    RootPanel.get().remove(menu);
            }

            @Override
            public boolean isEnabled() {
                return MenuButton.this.isEnabled();
            }
        };
        addStyleName(decor.css().buttonMenu());
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        ImageElement arrow = DOM.createImg().cast();
        arrow.setSrc(decor.res.dropArrow().getSafeUri().asString());
        getElement().appendChild(arrow);
    }

    private void updatePositionAndSize(){
        if(!expander.isExpanded())
            return;
        int top = getAbsoluteTop() + getHeight();
        int left = getAbsoluteLeft();
        menu.setPosition(left, top);
        if(menu.getWidth(false) < getWidth()) {
            menu.getStyle().setWidth(getWidth(), Style.Unit.PX);
        }
    }

    public Menu getMenu(){
        return menu;
    }

}
