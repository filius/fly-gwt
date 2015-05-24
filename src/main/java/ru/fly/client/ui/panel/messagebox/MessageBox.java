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

package ru.fly.client.ui.panel.messagebox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.EndCallback;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.ui.Container;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.panel.FlowLayout;
import ru.fly.client.ui.panel.messagebox.decor.MessageBoxDecor;
import ru.fly.client.ui.panel.window.WindowManager;
import ru.fly.client.util.LastPassExecutor;
import ru.fly.shared.FlyException;

import java.util.Arrays;
import java.util.List;

/**
 * User: fil
 * Date: 22.01.14
 * Time: 22:19
 */
public class MessageBox extends Container {

    private static final MessageBoxDecor decor = GWT.create(MessageBoxDecor.class);

    private String title;
    private Widget w;
    private List<Button> buttons;
    private FElement modalEl;
    private boolean canseled = false;

    public MessageBox(String title, Widget w, List<Button> buttons) {
        super(DOM.createDiv());
        this.title = title;
        this.w = w;
        w.setStyleName(decor.css().inner());
        this.buttons = buttons;
        setStyleName(decor.css().mb());
        getStyle().setPosition(Style.Position.ABSOLUTE);
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        if(title != null){
            FElement hdr = DOM.createDiv().cast();
            hdr.setClassName(decor.css().header());
            hdr.setInnerHTML(title);
            getElement().insertFirst(hdr);
        }
        super.add(w);
        if(buttons != null){
            FlowLayout buttonsPanel = new FlowLayout();
            buttonsPanel.setStyleName(decor.css().buttons());
            super.add(buttonsPanel);
            for(Button btn : buttons){
                buttonsPanel.add(btn);
            }
        }
    }

    @Override
    public void add(Widget w) {
        throw new IllegalStateException("Unsupported operation");
    }

    @Override
    public void addAll(List<? extends Widget> childs) {
        throw new IllegalStateException("Unsupported operation");
    }

    private FElement getModal(){
        if(modalEl == null){
            modalEl = DOM.createDiv().cast();
            modalEl.setClassName(decor.css().modal());
        }
        return modalEl;
    }

    public MessageBox center(){
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                setLeft((Window.getClientWidth() - getWidth(false)) / 2);
                setTop((Window.getClientHeight() - getHeight(false)) / 2);
            }
        });
        return this;
    }

    public MessageBox show(){
        return show(null);
    }

    public MessageBox show(Double secDelay){
        canseled = false;
        if(secDelay == null){
            return doShow();
        }else{
            new LastPassExecutor(secDelay){
                @Override
                protected void exec(Object param) {
                    if(!canseled){
                        doShow();
                    }
                }
            }.pass();
        }
        return this;
    }

    private MessageBox doShow(){
        getModal().getStyle().setZIndex(WindowManager.getNextZIndex());
        if(RootPanel.getBodyElement() == null){
            throw new FlyException("BODY element is NULL");
        }
        RootPanel.getBodyElement().appendChild(getModal());
        this.setZIndex(WindowManager.getNextZIndex());
        RootPanel.get().add(this);
        int w = getWidth();
        if(w < 200)
            setWidth(200);
        else if(w > 600)
            setWidth(600);
        center();
        if(buttons != null && buttons.size() > 0){
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    buttons.get(0).focus();
                }
            });
        }
        return this;
    }

    public void hide(){
        canseled = true;
        if(isAttached()){
            removeFromParent();
            RootPanel.getBodyElement().removeChild(getModal());
        }
    }

    private static MessageBox alertInfo(String title, String msg, ImageResource ico, final EndCallback<Void> cback){
        Button ok = new Button("Ok");
        ok.setWidth(60);
        String icon = "<img src='"+ico.getSafeUri().asString()
                +"' style='float: left; margin: 0 10px 10px 0;'></img>";
        final MessageBox box = new MessageBox(title, new HTML(icon + msg), Arrays.asList(ok));
        ok.addClickHandler(new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                box.hide();
                if(cback != null){
                    cback.onEnd(null);
                }
            }
        });
        return box.show();
    }

    public static MessageBox alert(String title, String msg, EndCallback<Void> cback){
        return alertInfo(title, msg, decor.res.warning(), cback);
    }

    public static MessageBox alert(String title, String msg){
        return alert(title, msg, null);
    }

    public static MessageBox info(String title, String msg, EndCallback<Void> cback){
        return alertInfo(title, msg, decor.res.info(), cback);
    }

    public static MessageBox info(String title, String msg){
        return info(title, msg, null);
    }

    public static MessageBox waitHidden(String title, String msg){
        String icon = "<div style='overflow: hidden; text-align: center; padding-top: 4px;'><img src='"+decor.res.progress().getSafeUri().asString()
                +"'></img></div>";
        return new MessageBox(title, new HTML("<center>"+msg+icon+"</center>"), null);
    }

    public static MessageBox wait(String title, String msg){
        return waitHidden(title, msg).show();
    }

    public static MessageBox confirm(String title, String msg, final EndCallback<Boolean> cback){
        Button yes = new Button("Да");
        yes.setWidth(60);
        Button no = new Button("Нет");
        no.setWidth(60);
        final MessageBox box = new MessageBox(title, new HTML(msg), Arrays.asList(yes, no));
        yes.addClickHandler(new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                if(cback != null){
                    cback.onEnd(true);
                }
                box.hide();
            }
        });
        no.addClickHandler(new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                if(cback != null){
                    cback.onEnd(false);
                }
                box.hide();
            }
        });
        return box.show();
    }

}
