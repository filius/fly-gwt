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
import com.google.gwt.dom.client.ButtonElement;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.F;
import ru.fly.client.event.FocusEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.event.ClickHandler;
import ru.fly.client.ui.Tooltip;

/**
 * User: fil
 * Date: 22.08.13
 * Time: 20:11
 */
public class Button extends Component {

    private final BtnDecor decor;

    public static final int BUTTON_SIZE_16 = 16;
    public static final int BUTTON_SIZE_24 = 24;
    public static final int BUTTON_SIZE_36 = 36;

    private ImageElement iconEl;
    private ImageResource ico;
    private FElement textEl;
    private String text;
    private int btnSize = BUTTON_SIZE_16;
    private Tooltip tooltip;

    public Button(BtnDecor decor){
        super(DOM.createButton());
        this.decor = decor;
        F.setEnableTextSelection(getElement(), false);
    }

    public Button() {
        this(GWT.<BtnDecor>create(BtnDecor.class));
    }

    public Button(String text) {
        this();
        setText(text);
    }

    public Button(String text, ClickHandler lnr) {
        this(text);
        if(lnr != null)
            addClickHandler(lnr);
    }

    public Button(ImageResource ico, String text) {
        this(text);
        setIcon(ico);
    }

    public Button(ImageResource ico, ClickHandler lnr) {
        this();
        setIcon(ico);
        if(lnr != null)
            addClickHandler(lnr);
    }

    public Button(ImageResource ico, String text, ClickHandler lnr) {
        this(text, lnr);
        setIcon(ico);
    }

    public void click(){
        ((ButtonElement)getElement().cast()).click();;
    }

    @Override
    public void setEnabled(boolean val) {
        super.setEnabled(val);
        ((ButtonElement)getElement().cast()).setDisabled(!val);
        if(!val){
            getElement().removeClassName(decor.css().over());
        }
    }

    public void setText(String text){
        this.text = text;
        if(!isAttached())
            return;
        if(text == null || text.isEmpty()){
            if(textEl != null)
                getTextElement().removeFromParent();
        }else{
            getTextElement().setInnerHTML(text);
            getElement().appendChild(getTextElement());
        }
    }

    public void setIcon(ImageResource ico){
        this.ico = ico;
        if(!isAttached())
            return;
        if(iconEl != null){
            getIconElement().removeFromParent();
        }
        if(ico == null){
            if(iconEl != null)
                removeStyleName(decor.css().buttonIcon());
        }else{
            addStyleName(decor.css().buttonIcon());
            getIconElement().setSrc(ico.getSafeUri().asString());
            getElement().insertFirst(getIconElement());
            getIconElement().setHeight(btnSize);
            getIconElement().setWidth(btnSize);
        }
    }

    public void setTooltip(String html){
        if(html == null){
            tooltip = null;
        }else{
            if(tooltip == null){
                tooltip = new Tooltip(html);
            }else{
                tooltip.setContent(html);
            }
        }
    }

    private void showTooltip(Event event){
        if(tooltip == null)
            return;
        if(event == null){
            tooltip.hide();
        }else{
            tooltip.show(event.getClientX()+10, event.getClientY()+10);
        }
    }

    protected FElement getTextElement(){
        if(textEl == null){
            textEl = DOM.createSpan().cast();
        }
        return textEl;
    }

    private ImageElement getIconElement(){
        if(iconEl == null){
            iconEl = DOM.createImg().cast();
            iconEl.setHeight(BUTTON_SIZE_16);
        }
        return iconEl;
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        addStyleName(decor.css().button());
        setIcon(ico);
        setText(text);
    }

    @Override
    protected void onDetach() {
        DOM.setEventListener(getElement(), null);
        DOM.sinkEvents(getElement(), 0);
        super.onDetach();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        final EventListener oldLnr = DOM.getEventListener(getElement());
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch(event.getTypeInt()){
                    case Event.ONCLICK:
                        if(isEnabled())
                            fireEvent(new ClickEvent());
                        showTooltip(null);
                        break;
                    case Event.ONMOUSEOVER:
                        if(isEnabled()){
                            getElement().addClassName(decor.css().over());
                            showTooltip(event);
                        }
                        break;
                    case Event.ONMOUSEOUT:
                        getElement().removeClassName(decor.css().over());
                        showTooltip(null);
                        break;

                    case Event.ONFOCUS:
                        onFocus();
                        fireEvent(new FocusEvent());
                        break;
                    case Event.ONBLUR:
                        onBlur();
                        break;
                }
                if(oldLnr != null){
                    oldLnr.onBrowserEvent(event);
                }
            }
        });
        DOM.sinkEvents(getElement(), Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONFOCUS | Event.ONBLUR);
    }

    public void addClickHandler(ClickHandler lnr){
        addHandler(lnr, ClickEvent.getType());
    }

    public void setButtonSize(int size){
        btnSize = size;
        if(text != null && !text.isEmpty())
            getTextElement().setLineHeight(size);
        if(ico != null)
            getIconElement().setHeight(size);
    }

    public void focus(){
        getElement().focus();
    }

    protected void onFocus(){
        if(isEnabled()) {
            addStyleName(decor.css().focused());
        }
    }
    protected void onBlur(){
        removeStyleName(decor.css().focused());
    }
}
