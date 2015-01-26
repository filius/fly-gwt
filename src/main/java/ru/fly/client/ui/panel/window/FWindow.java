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

package ru.fly.client.ui.panel.window;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.event.KeyEnterEvent;
import ru.fly.client.event.KeyEnterHandler;
import ru.fly.client.ui.Container;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.panel.Margin;
import ru.fly.client.ui.panel.SingleLayout;
import ru.fly.client.ui.panel.window.decor.WindowDecor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 09.01.14
 * Time: 19:40
 */
public class FWindow extends SingleLayout {

    private final WindowDecor decor = GWT.create(WindowDecor.class);

    private FElement headerEl;
    private FElement containerEl;
    private Container buttonPanel;
    private FElement modalEl;
    private FElement closeEl;

    private List<Button> buttons = new ArrayList<Button>();
    private boolean modal = false;
    private boolean closeable = true;
    private boolean listenESC = false;
    private boolean listenEnter = false;

    public FWindow(){
        getStyle().setPosition(Style.Position.ABSOLUTE);
        setStyleName(decor.css().window());
        headerEl = DOM.createDiv().cast();
        headerEl.setInnerHTML("&nbsp;");
        headerEl.setClassName(decor.css().header());
        buttonPanel = new Container(DOM.createDiv());
        buttonPanel.setStyleName(decor.css().buttonPanel());
        buttonPanel.getStyle().setPaddingTop(4, Style.Unit.PX);
    }

    public void setHeaderText(String text){
        headerEl.setInnerHTML(text);
    }

    public void addButton(Button btn){
        buttons.add(btn);
        if(buttonPanel.isAttached()) {
            buttonPanel.add(btn);
        }
    }

    public FWindow show(){
        RootPanel.get().add(this);
        if(modal){
            getModal().getStyle().setZIndex(WindowManager.getNextZIndex());
            RootPanel.getBodyElement().appendChild(getModal());
        }
        setZIndex(WindowManager.getNextZIndex());
        setTop(0);
        setLeft(0);
        return this;
    }

    public FWindow center(){
        setLeft((Window.getClientWidth() - getWidth(false)) / 2);
        setTop((Window.getClientHeight() - getHeight(false)) / 2);
        return this;
    }

    public void hide(){
        if(modal){
            RootPanel.getBodyElement().removeChild(getModal());
        }
        removeFromParent();
    }

    public void setModal(boolean val){
        modal = val;
    }

    public void setCloseable(boolean val){
        closeable = val;
        if(closeEl != null){
            closeEl.removeFromParent();
        }
        if(closeable){
            if(closeEl == null) {
                closeEl = DOM.createDiv().cast();
                closeEl.setClassName(decor.css().close());
            }
            headerEl.appendChild(closeEl);
            DOM.setEventListener(closeEl, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    hide();
                }
            });
            DOM.sinkEvents(closeEl, Event.ONCLICK);
        }
    }

    public void setCloseByESC(boolean val){
        listenESC = val;
    }

    public void setListenEnter(boolean val){
        listenEnter = val;
    }

    @Override
    public FElement getContainerElement() {
        if(containerEl == null){
            containerEl = DOM.createDiv().cast();
            containerEl.setClassName(decor.css().inner());
        }
        return containerEl;
    }

    @Override
    public void setLeft(int left) {
        super.setLeft(left < 0 ? 0 : left);
    }

    @Override
    public void setTop(int top) {
        super.setTop(top < 0 ? 0 : top);
    }

    public HandlerRegistration addKeyEnterHandler(KeyEnterHandler h){
        return addHandler(h, KeyEnterEvent.getType());
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(headerEl);
        F.setEnableTextSelection(headerEl, false);
        getElement().appendChild(getContainerElement());
    }

    @Override
    protected void doAttachChildren() {
        super.doAttachChildren();
        F.render(this, buttonPanel);
        for(Button btn : buttons){
            buttonPanel.add(btn);
        }
    }

    @Override
    protected void doDetachChildren() {
        for(Button btn : buttons) {
            if(btn.isAttached()){
                buttonPanel.remove(btn);
            }
        }
        if(buttonPanel.isAttached()){
            F.erase(this, buttonPanel);
        }
        super.doDetachChildren();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        setCloseable(closeable);
        layout(true);
        final EventListener oldLnr = DOM.getEventListener(getElement());
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (listenEnter && event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_ENTER) {
                    fireEvent(new KeyEnterEvent(event));
                }else if(listenESC && event.getTypeInt() == Event.ONKEYDOWN && event.getKeyCode() == KeyCodes.KEY_ESCAPE){
                    hide();
                }
                if (oldLnr != null) {
                    oldLnr.onBrowserEvent(event);
                }
            }
        });
        DOM.sinkEvents(getElement(), Event.ONKEYDOWN);
    }

    @Override
    protected void doLayout() {
        int w = getWidth(true)-6;
        int h = getHeight(true)-headerEl.getOffsetHeight()-6;
        if(buttons.size() > 0){
            h -= buttonPanel.getHeight();
        }
        containerEl.setWidth(w);
        containerEl.setHeight(h);
        Widget child = getWidget();
        if(child != null){
            if(child.getLayoutData() instanceof Margin){
                Margin m = (Margin) child.getLayoutData();
                m.fillMargins(child);
                w = w - m.getLeft() - m.getRight();
                h = h - m.getTop() - m.getBottom();
            }
            child.setPixelSize(w, h);
        }
    }

    private FElement getModal(){
        if(modalEl == null){
            modalEl = DOM.createDiv().cast();
            modalEl.setClassName(decor.css().modal());
        }
        return modalEl;
    }
}
