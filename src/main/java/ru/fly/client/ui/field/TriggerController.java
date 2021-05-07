package ru.fly.client.ui.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.CommonDecor;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;

/**
 * @author fil
 */
public abstract class TriggerController extends Expander {

    private final CommonDecor decor = GWT.create(CommonDecor.class);

    private FElement tr;

    public TriggerController(Component component, FElement tr){
        super(component.getElement());
        this.tr = tr;
        component.addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                initListeners();
            }
        });
    }

    private void initListeners(){
        if(tr != null){
            DOM.setEventListener(tr, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    switch(event.getTypeInt()){
                        case Event.ONCLICK:
                            expandCollapse();
                            break;
                        case Event.ONMOUSEOVER:
                            if (isEnabled()) {
                                tr.addClassName(decor.css().over());
                            }
                            break;
                        case Event.ONMOUSEOUT:
                            if (isEnabled()) {
                                tr.removeClassName(decor.css().over());
                            }
                            break;
                    }
                }
            });
            DOM.sinkEvents(tr, Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
        }
    }

}
