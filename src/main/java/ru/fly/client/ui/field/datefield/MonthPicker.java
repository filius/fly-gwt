package ru.fly.client.ui.field.datefield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.CDate;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.field.datefield.decor.DateFieldDecor;
import ru.fly.client.ui.field.label.Label;
import ru.fly.client.ui.panel.FlowLayout;
import ru.fly.client.ui.panel.HLayout;
import ru.fly.client.ui.panel.Margin;
import ru.fly.client.ui.panel.VHLayoutData;
import ru.fly.client.ui.panel.VLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fil
 */
public class MonthPicker extends VLayout implements ValueChangeEvent.HasValueChangeHandler<Date> {

    private DateFieldDecor decor;
    private List<MonthButton> monthBtns = new ArrayList<>();
    private Date value;
    private Label yearLabel;

    public MonthPicker() {
        this(GWT.<DateFieldDecor>create(DateFieldDecor.class));
    }

    public MonthPicker(DateFieldDecor decor) {
        this.decor = decor;
        setStyleName(decor.css().monthPicker());

        HLayout yearNavigationPanel = new HLayout();
        yearNavigationPanel.setHeight(20);
        add(yearNavigationPanel);

        Button prev = new Button("<<", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                setValue(new CDate(value).addYear(-1).asDate(), false);
            }
        });
        prev.addStyleName(decor.css().monthPickerPrevBtn());
        yearNavigationPanel.add(prev, new VHLayoutData(30, 1));
        yearLabel = new Label(null);
        yearNavigationPanel.add(yearLabel, new VHLayoutData(1, 1));
        Button next = new Button(">>", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                setValue(new CDate(value).addYear(1).asDate(), false);
            }
        });
        next.addStyleName(decor.css().monthPickerNextBtn());
        yearNavigationPanel.add(next, new VHLayoutData(30, 1));

        HLayout monthPanel = new HLayout();
        add(monthPanel, new VHLayoutData(1, 1, new Margin(4)));
        VLayout leftPanel = new VLayout();
        monthPanel.add(leftPanel, new VHLayoutData(.5, 1));
        VLayout rightPanel = new VLayout();
        monthPanel.add(rightPanel, new VHLayoutData(.5, 1));

        for (int i = 0; i < 6; i++) {
            MonthButton btn = new MonthButton(i + 1, CDate.getMonthName(i));
            monthBtns.add(btn);
            leftPanel.add(btn);
        }
        for (int i = 6; i < 12; i++) {
            MonthButton btn = new MonthButton(i + 1, CDate.getMonthName(i));
            monthBtns.add(btn);
            rightPanel.add(btn);
        }

        FlowLayout downBtnsPanel = new FlowLayout();
        add(downBtnsPanel, new VHLayoutData(1, -1, new Margin(2)));
        downBtnsPanel.add(new Button("сегодня", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                setValue(new Date(), true);
            }
        }));
        downBtnsPanel.add(new Button("очистить", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                setValue(null, true);
            }
        }));
    }

    public void setValue(Date value) {
        setValue(value, true);
    }

    public void setValue(Date value, boolean fire) {
        for (MonthButton m : monthBtns) {
            m.removeStyleName(decor.css().monthPickerMonthValue());
        }
        CDate d = new CDate(value).clearTime();
        if (value == null) {
            this.value = null;
        } else {
            this.value = d.asDate();
            int month = d.getMonth();
            monthBtns.get(month - 1).addStyleName(decor.css().monthPickerMonthValue());
        }
        yearLabel.setHtml(String.valueOf(d.getYear()));
        if (fire) {
            fireEvent(new ValueChangeEvent<Date>(this.value));
        }
    }

    public Date getValue() {
        return value;
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeEvent.ValueChangeHandler<Date> h) {
        return addHandler(h, ValueChangeEvent.<Date>getType());
    }

    private class MonthButton extends FlowLayout {

        int monthNum;

        MonthButton(int num, String name) {
            monthNum = num;
            setStyleName(decor.css().monthPickerMonth());
            getElement().setInnerHTML(name);
        }

        @Override
        protected void onAttach() {
            super.onAttach();
            final EventListener oldLnr = DOM.getEventListener(getElement());
            DOM.setEventListener(getElement(), new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    oldLnr.onBrowserEvent(event);
                    switch (event.getTypeInt()) {
                        case Event.ONMOUSEOVER:
                            if (isAttached()) {
                                addStyleName(decor.css().over());
                            }
                            break;
                        case Event.ONMOUSEOUT:
                            removeStyleName(decor.css().over());
                            break;
                        case Event.ONCLICK:
                            removeStyleName(decor.css().over());
                            setValue(new CDate(value == null ? new Date() : value).withMonth(monthNum).asDate());
                            break;
                    }
                }
            });
            DOM.sinkEvents(getElement(), Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK);
        }
    }
}
