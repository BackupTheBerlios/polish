package de.enough.skylight.dom;
public interface DocumentEvent {
    public Event createEvent(String eventType) throws DomException;

}