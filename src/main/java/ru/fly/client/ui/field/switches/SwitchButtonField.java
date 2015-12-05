package ru.fly.client.ui.field.switches;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.Field;
import ru.fly.client.ui.field.switches.decor.SwitchDecor;

/**
 * User: fil
 * Date: 05.12.15
 */
public class SwitchButtonField extends Field<Boolean> {

    private final SwitchDecor decor;
    private FElement textEl;

    public SwitchButtonField(){
        this(GWT.<SwitchDecor>create(SwitchDecor.class));
    }

    public SwitchButtonField(SwitchDecor decor){
        this.decor = decor;
        setStyleName(decor.css().switchButton());

        textEl = DOM.createDiv().cast();
    }

    @Override
    public void setWidth(int width) {
        // to nothing
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();

        FElement triggerEl = DOM.createAnchor().cast();
        triggerEl.setClassName(decor.css().trigger());
        getElement().appendChild(triggerEl);

        textEl.setClassName(decor.css().status());
        getElement().appendChild(textEl);
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
                        changeValue();
                        break;
                }
                if(oldLnr != null){
                    oldLnr.onBrowserEvent(event);
                }
            }
        });
        DOM.sinkEvents(getElement(), Event.ONCLICK);
    }

    @Override
    public boolean setValue(Boolean value, boolean fire) {
        if(value == null){
            removeStyleName(decor.css().on());
            removeStyleName(decor.css().off());
            textEl.setInnerText(null);
        } else {
            addStyleName(value ? decor.css().on() : decor.css().off());
            removeStyleName(value ? decor.css().off() : decor.css().on());
            textEl.setInnerText(value ? decor.textOn() : decor.textOff());
        }
        return super.setValue(value, fire);
    }

    protected void changeValue(){
        Boolean old = getValue();
        setValue(old == null || !old);
    }
}
