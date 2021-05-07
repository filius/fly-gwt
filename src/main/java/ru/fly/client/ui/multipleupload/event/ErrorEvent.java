package ru.fly.client.ui.multipleupload.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;

/**
 * @author fil
 */
public class ErrorEvent extends GwtEvent<ErrorEvent.ErrorHandler> {

    private static Type<ErrorHandler> TYPE;

    private Event nativeEvent;

    public ErrorEvent(Event nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    public static Type<ErrorHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<ErrorHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<ErrorHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(ErrorHandler handler) {
        handler.onError(nativeEvent);
    }

    public interface ErrorHandler extends EventHandler {
        void onError(Event nativeEvent);
    }

    public interface HasErrorHandler extends EventHandler {
        HandlerRegistration addErrorHandler(ErrorHandler h);
    }
}