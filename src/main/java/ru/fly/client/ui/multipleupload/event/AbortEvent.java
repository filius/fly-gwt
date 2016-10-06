package ru.fly.client.ui.multipleupload.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

/**
 * @author fil
 */
public class AbortEvent extends GwtEvent<AbortEvent.AbortHandler> {

    private static Type<AbortHandler> TYPE;

    private Event nativeEvent;

    public AbortEvent(Event nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    public static Type<AbortHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<AbortHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<AbortHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(AbortHandler handler) {
        handler.onAbort(nativeEvent);
    }

    public interface AbortHandler extends EventHandler {
        void onAbort(Event nativeEvent);
    }

    public interface HasAbortHandler extends EventHandler {
        HandlerRegistration addAbortHandler(AbortHandler h);
    }
}