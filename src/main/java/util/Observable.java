package util;

import java.util.concurrent.CopyOnWriteArrayList;

public class Observable {

    private boolean changed = false;
    private boolean paused = false;
    private CopyOnWriteArrayList<Observer> observers;

    public Observable() {
        observers = new CopyOnWriteArrayList<>();
    }

    public void pauseNotify() {
        paused = true;
    }

    public void resumeNotify() {
        paused = false;
        notifyObservers();
    }

    public void addObserver(Observer o) {
        if (o != null && !observers.contains(o)) {
            observers.add(o);
        }
    }

    public void removeObserver(Observer o) {
        observers.remove(o);
    }

    public void notifyObservers() {
        if (!paused && hasChanged()) {
            clearChanged();
            for (Observer observer : observers) {
                observer.update();
            }
        }
    }

    public void setChanged() {
        changed = true;
    }

    public void clearChanged() {
        changed = false;
    }

    public boolean hasChanged() {
        return changed;
    }
}
