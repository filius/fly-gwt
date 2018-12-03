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

import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.EditorError;
import com.google.gwt.editor.client.HasEditorErrors;
import com.google.gwt.editor.client.LeafValueEditor;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.F;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.Tooltip;
import ru.fly.client.ui.validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fil
 */
public class Field<T> extends Component implements LeafValueEditor<T>, HasEditorErrors<T>,
        ValueChangeEvent.HasValueChangeHandler<T> {

    private final FieldDecor decor = GWT.create(FieldDecor.class);

    protected T value;
    private String errorMsg;
    private Tooltip errorToolTip;
    private List<Validator<T>> validators = new ArrayList<>();
    private boolean focused = false;
    private boolean alwaysDisabled = false;

    public Field(){
        super(DOM.createDiv());
        setStyleName(decor.css().field());
        setWidth(160);
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        FElement errorIcon = DOM.createDiv().cast();
        errorIcon.setClassName(decor.css().fieldErrorIcon());
        getElement().appendChild(errorIcon);
        DOM.setEventListener(errorIcon, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch (event.getTypeInt()) {
                    case Event.ONMOUSEOVER:
                        showErrorTooltip(event);
                        break;
                    case Event.ONMOUSEOUT:
                        showErrorTooltip(null);
                        break;
                }
            }
        });
        DOM.sinkEvents(errorIcon, Event.ONMOUSEOVER | Event.ONMOUSEOUT);
    }

    public T getValue(){
        return value;
    }

    public void setValue(T value){
        setValue(value, true);
    }

    /**
     * set value to the field
     * @param value -
     * @param fire - TRUE if need fire ValueChangeEvent
     * @return - TRUE if value is really new
     */
    public boolean setValue(T value, boolean fire){
        T old = getValue();
        this.value = value;
        boolean isNew = (old != null && !old.equals(value)) || (value != null && !value.equals(old));
        if(fire && isNew){
            fireEvent(new ValueChangeEvent<T>(value));
        }
        return isNew;
    }

    public void clear(){
        setValue(null);
    }

    @Override
    public void setWidth(int width) {
        if(isError())
            super.setWidth(width-18);
        else
            super.setWidth(width);
    }

    @Override
    public int getWidth(boolean client) {
        if(isError())
            return super.getWidth(client)+18;
        else
            return super.getWidth(client);
    }

    /**
     * mark field as permanently disabled
     * @param val - TRUE always disabled (default FALSE)
     */
    public void setAlwaysDisabled(boolean val){
        alwaysDisabled = val;
        setEnabled(isEnabled());
    }

    public void setError(String msg){
        if(!isAttached() || !F.isWidgetVisible(this)){
            return;
        }
        int width = getWidth(false);
        errorMsg = msg;
        setWidth(width);
        addStyleName(decor.css().error());
    }

    public void clearError(){
        if(!isAttached() || !F.isWidgetVisible(this)){
            return;
        }
        if(isError()) {
            int width = getWidth(false);
            errorMsg = null;
            setWidth(width);
            removeStyleName(decor.css().error());
        }
    }

    public boolean isError(){
        return errorMsg != null;
    }

    @Override
    public void showErrors(List<EditorError> errors) {
        if(errors == null || errors.size() < 1)
            clearError();
        else
            setError(errors.get(0).getMessage());
    }

    private void showErrorTooltip(Event event){
        if(event == null){
            if(errorToolTip == null)
                return;
            else
                errorToolTip.hide();
            return;
        }
        if(errorToolTip == null){
            errorToolTip = new Tooltip();
            errorToolTip.addStyleName(decor.css().error());
        }
        errorToolTip.setContent(errorMsg);
        errorToolTip.show(event.getClientX()+10, event.getClientY()+10);
    }

    public void addValidator(Validator<T> validator){
        validators.add(validator);
    }

    public boolean validate(){
        boolean oldErrorState = isError();
        T value = getValue();
        for(Validator<T> validator : validators){
            String errMsg = validator.validate(value);
            if(errMsg != null){
                setError(errMsg);
                return false;
            }
        }
        if(oldErrorState)
            clearError();
        return true;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeEvent.ValueChangeHandler<T> handler) {
        return addHandler(handler, ValueChangeEvent.<T>getType());
    }

    protected void onFocus(){
        focused = true;
    }

    protected void onBlur(){
        focused = false;
    }
      
    public boolean isFocused(){
        return focused;
    }

    public void focus(){}

    @Override
    public void setEnabled(boolean val) {
        super.setEnabled(!alwaysDisabled && val);
    }

    @Override
    public void enable() {
        super.enable();
    }
}
